'use client';

import { useCallback, useEffect, useRef, useState } from 'react';
import { ChatHeader } from './chat-header';
import { ChatMessages } from './chat-messages';
import { QuickActions } from './quick-actions';
import { ChatInput } from './chat-input';
import { useMap } from '../map-provider';
import { auth } from '@/lib/firebase';
import { sendChatMessage, type ChatRequestPayload, type ChatRouteOption } from '@/lib/actions/chat-action';
import type { RouteCardData } from '@/types/map';

const STARTER_MESSAGE = 'Tell me where you are going and I can suggest safer route options around hazards.';
const ERROR_MESSAGE = 'Unable to reach AI assistant right now. Please try again in a moment.';

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
    };
  });
}

export function ChatPanel() {
  const { state, dispatch } = useMap();
  const [isSending, setIsSending] = useState(false);
  const seededMessageRef = useRef(false);

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

  return (
    <div className="flex h-full flex-col">
      <ChatHeader />
      <ChatMessages isSending={isSending} />
      <QuickActions onSelect={(value) => void handleSendMessage(value)} disabled={isSending} />
      <ChatInput onSend={handleSendMessage} isSending={isSending} />
    </div>
  );
}
