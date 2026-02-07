'use client';

import { APIProvider, Map, AdvancedMarker, Marker, MapMouseEvent } from '@vis.gl/react-google-maps';
import { DEFAULT_CENTER, DEFAULT_ZOOM, DARK_MAP_STYLES } from '@/lib/constants/map-config';
import { RoutePolylines } from './route-polyline';
import { RouteMarkers } from './route-markers';
import { useMap } from '../map-provider';
import { MapPin } from 'lucide-react';

interface GoogleMapViewProps {
  children?: React.ReactNode;
}

const API_KEY = process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY ?? '';
const MAP_ID = process.env.NEXT_PUBLIC_GOOGLE_MAP_ID;

interface SOSMarkerProps {
  position: { lat: number; lng: number };
  index: number;
  isSelected: boolean;
  onClick: () => void;
}

function SOSMarker({ position, index, isSelected, onClick }: SOSMarkerProps) {
  // Use AdvancedMarker if Map ID is available, otherwise use regular Marker
  if (MAP_ID) {
    return (
      <AdvancedMarker position={position} onClick={onClick}>
        <div
          className={`flex items-center justify-center rounded-full shadow-lg transition-all ${isSelected
            ? 'size-12 bg-red-500 animate-pulse ring-4 ring-red-300'
            : 'size-10 bg-red-500/80 hover:bg-red-500 hover:scale-110'
            }`}
        >
          <span className="text-sm font-bold text-white">{index + 1}</span>
        </div>
      </AdvancedMarker>
    );
  }

  // Fallback to regular Marker when no Map ID
  return (
    <Marker
      position={position}
      onClick={onClick}
      icon={{
        path: 'M12 2C8.13 2 5 5.13 5 9c0 5.25 7 13 7 13s7-7.75 7-13c0-3.87-3.13-7-7-7zm0 9.5c-1.38 0-2.5-1.12-2.5-2.5s1.12-2.5 2.5-2.5 2.5 1.12 2.5 2.5-1.12 2.5-2.5 2.5z',
        fillColor: isSelected ? '#ef4444' : '#f87171',
        fillOpacity: 1,
        strokeColor: '#ffffff',
        strokeWeight: isSelected ? 3 : 2,
        scale: isSelected ? 1.8 : 1.5,
        anchor: { x: 12, y: 24 } as google.maps.Point,
      }}
      label={{
        text: String(index + 1),
        color: '#ffffff',
        fontSize: '12px',
        fontWeight: 'bold',
      }}
    />
  );
}

function MapContent({ children }: { children?: React.ReactNode }) {
  const { state, dispatch } = useMap();

  return (
    <>
      <RoutePolylines
        activePath={state.activeRoute?.path}
        alternatePath={state.alternateRoute?.path}
      />
      <RouteMarkers origin={state.fromPosition} destination={state.toPosition} />
      {/* SOS Location Markers */}
      {state.sosLocations.map((location, index) => (
        <SOSMarker
          key={`sos-${index}`}
          position={location}
          index={index}
          isSelected={state.selectedSOSIndex === index}
          onClick={() => dispatch({ type: 'SELECT_SOS_PIN', payload: index })}
        />
      ))}
      {children}
    </>
  );
}

export function GoogleMapView({ children }: GoogleMapViewProps) {
  const { state, dispatch } = useMap();

  const handleMapClick = (event: MapMouseEvent) => {
    if (state.isSOSPinMode && event.detail.latLng) {
      const lat = event.detail.latLng.lat;
      const lng = event.detail.latLng.lng;
      dispatch({ type: 'ADD_SOS_PIN', payload: { lat, lng } });
    }
  };

  // Custom cursor for SOS mode - using crosshair as reliable fallback
  // Google Maps may not support SVG data URIs for draggableCursor
  const sosCursor = 'crosshair';

  return (
    <div
      className="h-full w-full"
      style={state.isSOSPinMode ? { cursor: sosCursor } : undefined}
    >
      <Map
        defaultCenter={DEFAULT_CENTER}
        defaultZoom={DEFAULT_ZOOM}
        gestureHandling="greedy"
        disableDefaultUI
        className={`h-full w-full ${state.isSOSPinMode ? 'cursor-sos-pin' : ''}`}
        onClick={handleMapClick}
        draggableCursor={state.isSOSPinMode ? sosCursor : undefined}
        draggingCursor={state.isSOSPinMode ? sosCursor : undefined}
        {...(MAP_ID
          ? { mapId: MAP_ID, colorScheme: 'DARK' as const }
          : { styles: DARK_MAP_STYLES })}
      >
        <MapContent>{children}</MapContent>
      </Map>
    </div>
  );
}

interface GoogleMapsProviderProps {
  children: React.ReactNode;
}

export function GoogleMapsProvider({ children }: GoogleMapsProviderProps) {
  if (!API_KEY) {
    return (
      <div className="flex h-full w-full items-center justify-center bg-[#1a2633]">
        <div className="text-center text-sm text-[#B8B9B6]">
          <p className="font-mono text-lg">Map</p>
          <p className="mt-1">Set NEXT_PUBLIC_GOOGLE_MAPS_API_KEY to load Google Maps</p>
        </div>
      </div>
    );
  }

  return <APIProvider apiKey={API_KEY}>{children}</APIProvider>;
}
