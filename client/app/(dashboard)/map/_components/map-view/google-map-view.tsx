'use client';

import { APIProvider, Map } from '@vis.gl/react-google-maps';
import { DEFAULT_CENTER, DEFAULT_ZOOM, DARK_MAP_STYLES } from '@/lib/constants/map-config';

interface GoogleMapViewProps {
  children?: React.ReactNode;
}

const API_KEY = process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY ?? '';
const MAP_ID = process.env.NEXT_PUBLIC_GOOGLE_MAP_ID;

export function GoogleMapView({ children }: GoogleMapViewProps) {
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

  return (
    <APIProvider apiKey={API_KEY}>
      <Map
        defaultCenter={DEFAULT_CENTER}
        defaultZoom={DEFAULT_ZOOM}
        gestureHandling="greedy"
        disableDefaultUI
        className="h-full w-full"
        {...(MAP_ID
          ? { mapId: MAP_ID, colorScheme: 'DARK' as const }
          : { styles: DARK_MAP_STYLES })}
      >
        {children}
      </Map>
    </APIProvider>
  );
}
