'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { ChatHeader } from './chat-header';
import { ChatMessages } from './chat-messages';
import { QuickActions } from './quick-actions';
import { ChatInput } from './chat-input';
import { useMap } from '../map-provider';
import { auth } from '@/lib/firebase';
import { sendChatMessage, type ChatRequestPayload, type ChatRouteOption } from '@/lib/actions/chat-action';
import type { LatLng, RouteCardData, RouteInfo } from '@/types/map';

const STARTER_MESSAGE = 'Tell me where you are going and I can suggest safer route options around hazards.';
const ERROR_MESSAGE = 'Unable to reach AI assistant right now. Please try again in a moment.';
const AUTO_ROUTE_PROMPT_SUFFIX = 'Please suggest the safest route options and explain the tradeoffs.';

function toSafetyBadge(tier?: string): RouteCardData['safetyBadge'] {
  if (tier === 'RECOMMENDED') return 'safe';
  if (tier === 'RISKY') return 'danger';
  return 'caution';
}

function metersToMiles(meters?: number): number {
  if (!meters || Number.isNaN(meters)) return 0;
  return Number((meters * 0.000621371).toFixed(1));
}

function secondsToMinutes(seconds?: number): number {
  if (!seconds || Number.isNaN(seconds)) return 0;
  return Math.max(1, Math.round(seconds / 60));
}

function toRouteCards(routeOptions: ChatRouteOption[]): RouteCardData[] {
  return routeOptions.map((option, index) => {
    const recommendationTier = option.recommendationTier ?? 'ALTERNATIVE';
    const hazardCount = option.hazardCount ?? 0;

    return {
      name: option.name || `Route Option ${index + 1}`,
      distanceMiles: metersToMiles(option.distanceMeters),
      etaMinutes: secondsToMinutes(option.durationSeconds),
      safetyBadge: toSafetyBadge(recommendationTier),
      terrain: recommendationTier.toLowerCase(),
      tags: [`${hazardCount} hazard${hazardCount === 1 ? '' : 's'}`],
      polyline: option.polyline,
      summary: option.summary,
      recommendationTier,
      hazardCount,
    };
  });
}

function decodePolyline(encoded: string): LatLng[] {
  const points: LatLng[] = [];
  let index = 0;
  let latitude = 0;
  let longitude = 0;

  while (index < encoded.length) {
    let shift = 0;
    let result = 0;
    let byte: number;

    do {
      byte = encoded.charCodeAt(index++) - 63;
      result |= (byte & 0x1f) << shift;
      shift += 5;
    } while (byte >= 0x20);

    const deltaLat = result & 1 ? ~(result >> 1) : result >> 1;
    latitude += deltaLat;

    shift = 0;
    result = 0;

    do {
      byte = encoded.charCodeAt(index++) - 63;
      result |= (byte & 0x1f) << shift;
      shift += 5;
    } while (byte >= 0x20);

    const deltaLng = result & 1 ? ~(result >> 1) : result >> 1;
    longitude += deltaLng;

    points.push({
      lat: latitude / 1e5,
      lng: longitude / 1e5,
    });
  }

  return points;
}

function recommendationTierToRouteType(recommendationTier?: string): RouteInfo['type'] {
  return recommendationTier === 'RECOMMENDED' ? 'safest' : 'fastest';
}

function toSafetyPercent(safetyBadge: RouteCardData['safetyBadge']): number {
  if (safetyBadge === 'safe') return 92;
  if (safetyBadge === 'danger') return 55;
  return 74;
}

export function ChatPanel() {
  const { state, dispatch } = useMap();
  const [isSending, setIsSending] = useState(false);
  const seededMessageRef = useRef(false);
  const autoRouteRequestKeyRef = useRef<string | null>(null);

  const { fromPosition, toPosition, fromLocation, toLocation, chatMessages } = state;

  useEffect(() => {
    if (seededMessageRef.current || chatMessages.length > 0) return;

    seededMessageRef.current = true;
    dispatch({
      type: 'ADD_CHAT_MESSAGE',
      payload: {
        id: crypto.randomUUID(),
        role: 'ai',
        content: STARTER_MESSAGE,
        timestamp: new Date().toISOString(),
      },
    });
  }, [chatMessages.length, dispatch]);

  const handleSendMessage = useCallback(
    async (message: string) => {
      if (!message.trim() || isSending) return;

      dispatch({
        type: 'ADD_CHAT_MESSAGE',
        payload: {
          id: crypto.randomUUID(),
          role: 'user',
          content: message.trim(),
          timestamp: new Date().toISOString(),
        },
      });

      setIsSending(true);
      try {
        const currentUser = auth?.currentUser;
        if (!currentUser) {
          throw new Error('User must be authenticated to chat with AI assistant');
        }

        const idToken = await currentUser.getIdToken();
        const payload: ChatRequestPayload = {
          message: message.trim(),
        };

        if (fromPosition && toPosition) {
          payload.originLongitude = fromPosition.lng;
          payload.originLatitude = fromPosition.lat;
          payload.originAddress = fromLocation || undefined;
          payload.destinationLongitude = toPosition.lng;
          payload.destinationLatitude = toPosition.lat;
          payload.destinationAddress = toLocation || undefined;
          payload.vehicleType = 'CAR';
        }

        const response = await sendChatMessage(idToken, payload);
        const routeCards = toRouteCards(response.routeOptions || []);

        dispatch({
          type: 'ADD_CHAT_MESSAGE',
          payload: {
            id: crypto.randomUUID(),
            role: 'ai',
            content: response.reply?.trim() || ERROR_MESSAGE,
            timestamp: new Date().toISOString(),
            routeCards,
          },
        });
      } catch (error) {
        console.error('Failed to send chat message', error);
        dispatch({
          type: 'ADD_CHAT_MESSAGE',
          payload: {
            id: crypto.randomUUID(),
            role: 'ai',
            content: ERROR_MESSAGE,
            timestamp: new Date().toISOString(),
          },
        });
      } finally {
        setIsSending(false);
      }
    },
    [dispatch, fromLocation, fromPosition, isSending, toLocation, toPosition],
  );

  useEffect(() => {
    if (!state.isChatOpen || state.viewState !== 'chat') return;
    if (!fromPosition || !toPosition) return;

    const routeKey = `${fromPosition.lat.toFixed(5)},${fromPosition.lng.toFixed(5)}->${toPosition.lat.toFixed(5)},${toPosition.lng.toFixed(5)}`;
    if (autoRouteRequestKeyRef.current === routeKey) return;
    autoRouteRequestKeyRef.current = routeKey;

    const originLabel = fromLocation?.trim() || `${fromPosition.lat.toFixed(5)}, ${fromPosition.lng.toFixed(5)}`;
    const destinationLabel = toLocation?.trim() || `${toPosition.lat.toFixed(5)}, ${toPosition.lng.toFixed(5)}`;
    const autoPrompt = `I want to travel from ${originLabel} to ${destinationLabel}. ${AUTO_ROUTE_PROMPT_SUFFIX}`;

    void handleSendMessage(autoPrompt);
  }, [fromLocation, fromPosition, handleSendMessage, state.isChatOpen, state.viewState, toLocation, toPosition]);

  const handleApplyRoute = useCallback(
    (card: RouteCardData) => {
      if (!card.polyline) {
        dispatch({ type: 'SET_ERROR', payload: 'This route cannot be applied because polyline data is missing.' });
        return;
      }

      const path = decodePolyline(card.polyline);
      if (path.length === 0) {
        dispatch({ type: 'SET_ERROR', payload: 'Unable to apply route path from AI response.' });
        return;
      }

      const fallbackOrigin = path[0];
      const fallbackDestination = path[path.length - 1];

      const activeRoute: RouteInfo = {
        id: crypto.randomUUID(),
        name: card.name,
        from: fromLocation || 'Origin',
        to: toLocation || 'Destination',
        fromPosition: fromPosition ?? fallbackOrigin,
        toPosition: toPosition ?? fallbackDestination,
        distanceMiles: card.distanceMiles,
        etaMinutes: card.etaMinutes,
        safetyPercent: toSafetyPercent(card.safetyBadge),
        type: recommendationTierToRouteType(card.recommendationTier),
        hazards: [],
        description: card.summary || card.terrain,
        path,
        steps: [],
      };

      dispatch({ type: 'SET_ROUTE', payload: { active: activeRoute } });
      dispatch({ type: 'TOGGLE_CHAT', payload: false });
      dispatch({ type: 'SET_ERROR', payload: null });
    },
    [dispatch, fromLocation, fromPosition, toLocation, toPosition],
  );

  return (
    <div className="flex h-full flex-col">
      <ChatHeader />
      <ChatMessages isSending={isSending} onApplyRoute={handleApplyRoute} />
      <QuickActions onSelect={(value) => void handleSendMessage(value)} disabled={isSending} />
      <ChatInput onSend={handleSendMessage} isSending={isSending} />
    </div>
  );
}
