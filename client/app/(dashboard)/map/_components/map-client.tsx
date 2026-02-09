'use client';

import { MapProvider, useMap } from './map-provider';
import { GoogleMapView, GoogleMapsProvider } from './map-view/google-map-view';
import { SearchBar } from './overlays/search-bar';
import { WeatherPanel } from './overlays/weather-panel';
import { MapControls } from './overlays/map-controls';
import { MenuButton } from './overlays/menu-button';
import { AskGeminiFab } from './overlays/ask-gemini-fab';
import { HazardAlertPopup } from './overlays/hazard-alert-popup';
import { SOSPopup } from './overlays/sos-popup';
import { SidePanel } from './desktop/side-panel';
import { RouteSheet } from './mobile/route-sheet';
import { ChatSheet } from './mobile/chat-sheet';
import { HazardDetailSheet } from './mobile/hazard-detail-sheet';
import { TurnInstructionCard } from './navigation/turn-instruction-card';
import { NavigationBar } from './navigation/navigation-bar';
import { DEFAULT_CENTER, HAZARD_SEARCH_RADIUS_METERS } from '@/lib/constants/map-config';
import { useIsDesktop } from '@/lib/hooks/use-media-query';
import { useDirections, metersToMiles, secondsToMinutes } from './hooks/use-directions';
import { sendChatMessage } from '@/lib/actions/chat-action';
import { useSOS } from './hooks/use-sos';
import { useAuth } from '@/context/AuthContext';
import { auth } from '@/lib/firebase';
import { getSOSEventsByUserId, deleteSOSEvent } from '@/lib/actions/sos-action';
import { SOSEventStatus } from '@/types';
import { useCallback, useEffect, useRef, useState } from 'react';
import { useSearchParams } from 'next/navigation';
import { X, AlertTriangle } from 'lucide-react';
import { getNearbyHazards } from '@/lib/actions/hazard-action';

import type { HazardMarker, HazardSeverity } from '@/types/map';
import type { HazardDTO } from '@/types';

function dtoToMarker(dto: HazardDTO): HazardMarker {
  // Use the first sentence (up to 60 chars) as a short title
  const raw = dto.description ?? '';
  const firstSentence = raw.split(/[.!?]\s/)[0];
  const title = firstSentence.length > 60 ? firstSentence.slice(0, 57) + '...' : firstSentence;

  return {
    id: dto.id!,
    position: { lat: dto.latitude, lng: dto.longitude },
    type: 'other',
    severity: dto.severity.toLowerCase() as HazardSeverity,
    title,
    description: dto.description,
    imageUrl: dto.imageUrl ?? undefined,
    reportedAt: new Date().toISOString(),
    reportCount: (dto.verificationCount ?? 0) + 1,
  };
}

function MapLayout() {
  const { state, dispatch } = useMap();
  const { user } = useAuth();
  const isDesktop = useIsDesktop();
  const { calculateRoute, isReady } = useDirections();
  const isCalculatingRef = useRef(false);
  const { sosEvent, triggerSOS } = useSOS();

  const prevSOSCountRef = useRef(0);
  const hasLoadedSOSRef = useRef(false);
  const searchParams = useSearchParams();
  const hasAppliedNavParamsRef = useRef(false);

  // Handle navigation from saved locations (safety-profile page)
  useEffect(() => {
    if (hasAppliedNavParamsRef.current) return;

    const lat = searchParams.get('lat');
    const lng = searchParams.get('lng');
    const name = searchParams.get('name');

    if (lat && lng) {
      const latitude = parseFloat(lat);
      const longitude = parseFloat(lng);
      if (Number.isNaN(latitude) || Number.isNaN(longitude)) return;

      hasAppliedNavParamsRef.current = true;

      // Set destination from the saved location
      dispatch({
        type: 'SET_TO_LOCATION',
        payload: {
          text: name || `${latitude.toFixed(4)}, ${longitude.toFixed(4)}`,
          position: { lat: latitude, lng: longitude },
        },
      });

      // Set origin from browser geolocation
      if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(
          (pos) => {
            dispatch({
              type: 'SET_FROM_LOCATION',
              payload: {
                text: 'Current Location',
                position: { lat: pos.coords.latitude, lng: pos.coords.longitude },
              },
            });
          },
          () => {
            dispatch({
              type: 'SET_FROM_LOCATION',
              payload: {
                text: 'Current Location',
                position: DEFAULT_CENTER,
              },
            });
          },
        );
      } else {
        dispatch({
          type: 'SET_FROM_LOCATION',
          payload: {
            text: 'Current Location',
            position: DEFAULT_CENTER,
          },
        });
      }
    }
  }, [searchParams, dispatch]);

  // Load existing SOS events from the backend on mount
  useEffect(() => {
    if (!user?.id || !auth?.currentUser || hasLoadedSOSRef.current) return;
    hasLoadedSOSRef.current = true;

    auth.currentUser.getIdToken().then((idToken) => {
      getSOSEventsByUserId(idToken, user.id)
        .then((events) => {
          const activeEvents = events.filter((e) => e.status !== SOSEventStatus.RESOLVED);
          if (activeEvents.length > 0) {
            const pins = activeEvents.map((e) => ({
              lat: e.latitude,
              lng: e.longitude,
              eventId: e.id,
            }));
            dispatch({ type: 'SET_SOS_PINS', payload: pins });
            prevSOSCountRef.current = pins.length;
          }
        })
        .catch((err) => {
          console.error('Failed to load SOS events:', err);
        });
    });
  }, [user, dispatch]);

  // When a new SOS pin is added, persist it to the backend
  useEffect(() => {
    const currentCount = state.sosLocations.length;
    if (currentCount > prevSOSCountRef.current && user?.id && auth?.currentUser) {
      // A new pin was just added — it's always the last element
      const newPinIndex = currentCount - 1;
      const newPin = state.sosLocations[newPinIndex];
      auth.currentUser.getIdToken().then(async (idToken) => {
        try {
          const created = await triggerSOS(idToken, newPin.lat, newPin.lng, user.id);
          if (created?.id) {
            dispatch({ type: 'SET_SOS_PIN_EVENT_ID', payload: { index: newPinIndex, eventId: created.id } });
          }
        } catch (err) {
          console.error('Failed to persist SOS event:', err);
        }
      });
    }
    prevSOSCountRef.current = currentCount;
  }, [state.sosLocations, user, triggerSOS, dispatch]);
  const [hazards, setHazards] = useState<HazardMarker[]>([]);

  // Fetch nearby hazards once user is authenticated
  useEffect(() => {
    if (!user) return;

    async function fetchHazards(lat: number, lng: number) {
      try {
        const currentUser = auth?.currentUser;
        if (!currentUser) return;
        const idToken = await currentUser.getIdToken();
        const dtos = await getNearbyHazards(idToken, lng, lat, HAZARD_SEARCH_RADIUS_METERS);
        setHazards(dtos.map(dtoToMarker));
      } catch (err) {
        console.error('Failed to fetch hazards:', err);
      }
    }

    if (navigator.geolocation) {
      console.log('Using browser geolocation...');
      navigator.geolocation.getCurrentPosition(
        (pos) => fetchHazards(pos.coords.latitude, pos.coords.longitude),
        () => {
          // Fallback: fetch around default center
          fetchHazards(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng);
        },
      );
    } else {
      fetchHazards(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng);
    }
  }, [user]);

  const handleHazardSelect = useCallback(
    (hazard: HazardMarker) => {
      dispatch({ type: 'SELECT_HAZARD', payload: hazard });
    },
    [dispatch],
  );

  useEffect(() => {
    const fetchRoute = async () => {
      if (!state.fromPosition || !state.toPosition || !isReady || isCalculatingRef.current) {
        return;
      }

      isCalculatingRef.current = true;
      dispatch({ type: 'SET_LOADING_ROUTE', payload: true });

      const result = await calculateRoute(state.fromPosition, state.toPosition, {
        provideAlternatives: true,
      });

      if (result && result.routes.length > 0) {
        const [primary, ...alternates] = result.routes;

        const activeRoute = { ...primary, type: 'safest' as const, safetyPercent: 0 };
        const alternateRoute = alternates[0] ? { ...alternates[0], type: 'fastest' as const, safetyPercent: 0 } : undefined;

        dispatch({
          type: 'SET_ROUTE',
          payload: { active: activeRoute, alternate: alternateRoute },
        });
        dispatch({ type: 'SET_ERROR', payload: null });
        dispatch({ type: 'SET_LOADING_ROUTE', payload: false });

        // Enhance with Gemini AI-assessed values
        try {
          const currentUser = auth?.currentUser;
          if (currentUser) {
            const idToken = await currentUser.getIdToken();
            const aiResponse = await sendChatMessage(idToken, {
              message: 'Analyze the route options for safety, distance, and travel time. Provide your assessment.',
              originLatitude: state.fromPosition.lat,
              originLongitude: state.fromPosition.lng,
              originAddress: state.fromLocation || undefined,
              destinationLatitude: state.toPosition.lat,
              destinationLongitude: state.toPosition.lng,
              destinationAddress: state.toLocation || undefined,
              vehicleType: 'CAR',
            });

            const opts = aiResponse.routeOptions ?? [];
            if (opts.length > 0) {
              const recommended = opts.find((r) => r.recommendationTier === 'RECOMMENDED') ?? opts[0];
              const alternative = opts.find((r) => r.recommendationTier !== 'RECOMMENDED') ?? opts[1];

              const enhancedActive = {
                ...activeRoute,
                distanceMiles: recommended.distanceMeters ? metersToMiles(recommended.distanceMeters) : activeRoute.distanceMiles,
                etaMinutes: recommended.durationSeconds ? secondsToMinutes(recommended.durationSeconds) : activeRoute.etaMinutes,
                safetyPercent: recommended.safetyScore != null ? Math.round(recommended.safetyScore) : activeRoute.safetyPercent,
              };

              let enhancedAlternate = alternateRoute;
              if (alternateRoute && alternative) {
                enhancedAlternate = {
                  ...alternateRoute,
                  distanceMiles: alternative.distanceMeters ? metersToMiles(alternative.distanceMeters) : alternateRoute.distanceMiles,
                  etaMinutes: alternative.durationSeconds ? secondsToMinutes(alternative.durationSeconds) : alternateRoute.etaMinutes,
                  safetyPercent: alternative.safetyScore != null ? Math.round(alternative.safetyScore) : alternateRoute.safetyPercent,
                };
              }

              dispatch({
                type: 'SET_ROUTE',
                payload: { active: enhancedActive, alternate: enhancedAlternate },
              });
            }
          }
        } catch (err) {
          console.error('Failed to get AI route assessment, keeping directions values:', err);
        }
      } else {
        dispatch({ type: 'SET_ERROR', payload: 'Unable to calculate route. Please try different locations.' });
        dispatch({ type: 'SET_LOADING_ROUTE', payload: false });
      }

      isCalculatingRef.current = false;
    };

    fetchRoute();
  }, [state.fromPosition, state.toPosition, state.fromLocation, state.toLocation, isReady, calculateRoute, dispatch]);

  const showNavigationUI = state.viewState === 'navigating';
  const showBrowseOverlays = state.viewState !== 'navigating';

  const currentInstruction = state.activeRoute?.steps?.[0];

  return (
    <div className="relative h-dvh w-full overflow-hidden bg-[#1A1A1A]">
      {/* Full-screen map */}
      <div className={`absolute inset-0 ${isDesktop ? 'lg:right-[380px]' : ''}`}>
        <GoogleMapView hazards={hazards} onHazardSelect={handleHazardSelect} />
      </div>

      {/* Dim overlay when hazard detail is open on mobile */}
      {!isDesktop && state.isHazardDetailOpen && <div className="absolute inset-0 z-10 bg-black/40" />}

      {/* SOS Pin Mode Banner */}
      {state.isSOSPinMode && (
        <div className="pointer-events-auto absolute left-4 right-4 top-4 z-50 lg:left-auto lg:right-6 lg:w-96">
          <div className="flex items-center gap-3 rounded-lg border border-red-500/50 bg-red-900/90 px-4 py-3 text-sm text-white shadow-lg backdrop-blur-sm animate-pulse">
            <AlertTriangle className="size-5 text-red-400 shrink-0" />
            <span className="flex-1 font-medium">Tap on the map to set your SOS location</span>
            <button
              onClick={() => dispatch({ type: 'TOGGLE_SOS_PIN_MODE', payload: false })}
              className="shrink-0 rounded p-1 hover:bg-red-800/50"
              aria-label="Cancel SOS pin mode"
            >
              <X className="size-4" />
            </button>
          </div>
        </div>
      )}

      {/* Error notification */}
      {state.error && (
        <div className="pointer-events-auto absolute left-4 right-4 top-4 z-50 lg:left-auto lg:right-6 lg:w-96">
          <div className="flex items-center gap-3 rounded-lg border border-red-500/30 bg-red-900/90 px-4 py-3 text-sm text-white shadow-lg backdrop-blur-sm">
            <span className="flex-1">{state.error}</span>
            <button
              onClick={() => dispatch({ type: 'SET_ERROR', payload: null })}
              className="shrink-0 rounded p-1 hover:bg-red-800/50"
              aria-label="Dismiss error"
            >
              <X className="size-4" />
            </button>
          </div>
        </div>
      )}

      {/* Floating overlays */}
      {showBrowseOverlays && (
        <div className="pointer-events-none absolute inset-0 z-20">
          {/* Top row: menu + search */}
          <div className="pointer-events-auto absolute left-4 top-14 flex gap-2 lg:top-6">
            <MenuButton />
            <SearchBar />
          </div>

          {/* Weather panel */}
          <div className="pointer-events-auto absolute left-4 top-30 lg:top-20">
            <WeatherPanel />
          </div>

          {/* Map controls - right side */}
          <div className="pointer-events-auto absolute right-4 top-30 lg:right-100 lg:top-20">
            <MapControls />
          </div>

          {/* Ask Gemini FAB */}
          {state.viewState === 'browse' && (
            <div className="pointer-events-auto absolute bottom-95 left-4 lg:bottom-6 lg:left-6">
              <AskGeminiFab />
            </div>
          )}
        </div>
      )}

      {/* Navigation mode overlays */}
      {showNavigationUI && (
        <div className="pointer-events-none absolute inset-0 z-20">
          {/* Hazard alert popup */}
          {state.isHazardAlertVisible && (
            <div className="pointer-events-auto absolute left-4 right-4 top-14 lg:left-6 lg:right-100 lg:top-6">
              <HazardAlertPopup />
            </div>
          )}

          {/* Turn instruction */}
          {currentInstruction && (
            <div className="pointer-events-auto absolute left-4 right-4 top-45 lg:left-6 lg:right-100 lg:top-45">
              <TurnInstructionCard
                direction={
                  (currentInstruction.maneuver?.includes('left')
                    ? 'left'
                    : currentInstruction.maneuver?.includes('right')
                      ? 'right'
                      : currentInstruction.maneuver?.includes('u-turn')
                        ? 'u-turn'
                        : 'straight') as 'left' | 'right' | 'straight' | 'u-turn' | 'arrive'
                }
                distanceMiles={
                  Number.isNaN(parseFloat(currentInstruction.distance)) ? 0.1 : parseFloat(currentInstruction.distance)
                }
                streetName={currentInstruction.instruction.replace(/<[^>]*>/g, '').slice(0, 50)}
              />
            </div>
          )}

          {/* Navigation bottom bar */}
          <div className="pointer-events-auto absolute bottom-0 left-0 right-0 lg:right-95">
            <NavigationBar />
          </div>
        </div>
      )}

      {/* Desktop: side panel */}
      {isDesktop && <SidePanel />}

      {/* Mobile: bottom sheets */}
      {!isDesktop && (
        <>
          <RouteSheet />
          <ChatSheet />
          <HazardDetailSheet />
        </>
      )}

      {/* SOS Popup - works on both mobile and desktop */}
      <SOSPopup
        isOpen={state.isSOSPopupOpen}
        onClose={() => dispatch({ type: 'CLOSE_SOS_POPUP' })}
        onAddPin={() => {
          dispatch({ type: 'CLOSE_SOS_POPUP' });
          dispatch({ type: 'TOGGLE_SOS_PIN_MODE', payload: true });
        }}
        onRemovePin={() => {
          if (state.selectedSOSIndex !== null) {
            const pin = state.sosLocations[state.selectedSOSIndex];
            // Delete from backend if it has an eventId
            if (pin?.eventId && auth?.currentUser) {
              auth.currentUser.getIdToken().then((idToken) => {
                deleteSOSEvent(idToken, pin.eventId!).catch((err) => {
                  console.error('Failed to delete SOS event:', err);
                });
              });
            }
            dispatch({ type: 'REMOVE_SOS_PIN', payload: state.selectedSOSIndex });
          }
        }}
        onClearAll={() => {
          // Delete all from backend
          if (auth?.currentUser && state.sosLocations.length > 0) {
            auth.currentUser.getIdToken().then((idToken) => {
              state.sosLocations.forEach((pin) => {
                if (pin.eventId) {
                  deleteSOSEvent(idToken, pin.eventId).catch((err) => {
                    console.error('Failed to delete SOS event:', err);
                  });
                }
              });
            });
          }
          dispatch({ type: 'CLEAR_ALL_SOS_PINS' });
        }}
        selectedLocation={state.selectedSOSIndex !== null ? state.sosLocations[state.selectedSOSIndex] : null}
        allLocations={state.sosLocations}
        selectedIndex={state.selectedSOSIndex}
        onSelectPin={(index) => dispatch({ type: 'SELECT_SOS_PIN', payload: index })}
        sosEvent={sosEvent}
      />
    </div>
  );
}

export function MapClient() {
  return (
    <GoogleMapsProvider>
      <MapProvider>
        <MapLayout />
      </MapProvider>
    </GoogleMapsProvider>
  );
}
