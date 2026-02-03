'use client';

import { MapProvider, useMap } from './map-provider';
import { GoogleMapView } from './map-view/google-map-view';
import { SearchBar } from './overlays/search-bar';
import { WeatherPanel } from './overlays/weather-panel';
import { MapControls } from './overlays/map-controls';
import { MenuButton } from './overlays/menu-button';
import { AskGeminiFab } from './overlays/ask-gemini-fab';
import { HazardAlertPopup } from './overlays/hazard-alert-popup';
import { SidePanel } from './desktop/side-panel';
import { RouteSheet } from './mobile/route-sheet';
import { ChatSheet } from './mobile/chat-sheet';
import { HazardDetailSheet } from './mobile/hazard-detail-sheet';
import { TurnInstructionCard } from './navigation/turn-instruction-card';
import { NavigationBar } from './navigation/navigation-bar';
import { useIsDesktop } from '@/lib/hooks/use-media-query';
import {
  MOCK_ROUTES,
  MOCK_HAZARDS,
  MOCK_CHAT_MESSAGES,
  MOCK_TURN_INSTRUCTION,
} from '@/lib/constants/map-config';
import { useEffect } from 'react';

function MapLayout() {
  const { state, dispatch } = useMap();
  const isDesktop = useIsDesktop();

  // Load mock data on mount for demo
  useEffect(() => {
    dispatch({ type: 'SET_ROUTE', payload: { active: MOCK_ROUTES.active, alternate: MOCK_ROUTES.alternate } });
    // Pre-load chat messages for demo
    MOCK_CHAT_MESSAGES.forEach((msg) => {
      dispatch({ type: 'ADD_CHAT_MESSAGE', payload: msg });
    });
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const showNavigationUI = state.viewState === 'navigating';
  const showBrowseOverlays = state.viewState !== 'navigating';

  return (
    <div className="relative h-dvh w-full overflow-hidden bg-[#1A1A1A]">
      {/* Full-screen map */}
      <div className={`absolute inset-0 ${isDesktop ? 'lg:right-[380px]' : ''}`}>
        <GoogleMapView />
      </div>

      {/* Dim overlay when hazard detail is open on mobile */}
      {!isDesktop && state.isHazardDetailOpen && (
        <div className="absolute inset-0 z-10 bg-black/40" />
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
          <div className="pointer-events-auto absolute left-4 top-[120px] lg:top-20">
            <WeatherPanel />
          </div>

          {/* Map controls - right side */}
          <div className="pointer-events-auto absolute right-4 top-[120px] lg:right-[400px] lg:top-20">
            <MapControls />
          </div>

          {/* Ask Gemini FAB */}
          {state.viewState === 'browse' && (
            <div className="pointer-events-auto absolute bottom-[380px] left-4 lg:bottom-6 lg:left-6">
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
            <div className="pointer-events-auto absolute left-4 right-4 top-14 lg:left-6 lg:right-[400px] lg:top-6">
              <HazardAlertPopup />
            </div>
          )}

          {/* Turn instruction */}
          <div className="pointer-events-auto absolute left-4 right-4 top-[180px] lg:left-6 lg:right-[400px] lg:top-[180px]">
            <TurnInstructionCard
              direction={MOCK_TURN_INSTRUCTION.direction}
              distanceMiles={MOCK_TURN_INSTRUCTION.distanceMiles}
              streetName={MOCK_TURN_INSTRUCTION.streetName}
            />
          </div>

          {/* Navigation bottom bar */}
          <div className="pointer-events-auto absolute bottom-0 left-0 right-0 lg:right-[380px]">
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
    </div>
  );
}

export function MapClient() {
  return (
    <MapProvider>
      <MapLayout />
    </MapProvider>
  );
}
