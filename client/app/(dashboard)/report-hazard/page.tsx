'use client';

import { useState, useCallback, useRef, useEffect, } from 'react';
import { useRouter } from 'next/navigation';
import {
  X,
  Search,
  Plus,
  Minus,
  Crosshair,
  MapPin,
  Check,
} from 'lucide-react';
import { APIProvider, Map, useMap as useGoogleMap } from '@vis.gl/react-google-maps';
import { DEFAULT_CENTER, DEFAULT_ZOOM, DARK_MAP_STYLES } from '@/lib/constants/map-config';

const API_KEY = process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY ?? '';
const MAP_ID = process.env.NEXT_PUBLIC_GOOGLE_MAP_ID;

type LocationMode = 'current' | 'pin';

interface SelectedLocation {
  lat: number;
  lng: number;
  address?: string;
}

function MapControls({ onLocate }: { onLocate: () => void }) {
  const map = useGoogleMap();

  return (
    <div className="absolute right-4 top-44 z-10 flex flex-col gap-2">
      <button
        onClick={() => map?.setZoom((map.getZoom() ?? DEFAULT_ZOOM) + 1)}
        className="flex size-10 items-center justify-center rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-[0_2px_8px_rgba(0,0,0,0.12)] transition-colors hover:bg-[#252525]"
      >
        <Plus className="size-[18px] text-white" />
      </button>
      <button
        onClick={() => map?.setZoom((map.getZoom() ?? DEFAULT_ZOOM) - 1)}
        className="flex size-10 items-center justify-center rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-[0_2px_8px_rgba(0,0,0,0.12)] transition-colors hover:bg-[#252525]"
      >
        <Minus className="size-[18px] text-white" />
      </button>
      <div className="mt-4">
        <button
          onClick={onLocate}
          className="flex size-10 items-center justify-center rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-[0_2px_8px_rgba(0,0,0,0.12)] transition-colors hover:bg-[#252525]"
        >
          <Crosshair className="size-[18px] text-[#0066CC]" />
        </button>
      </div>
    </div>
  );
}

function CenterPin() {
  return (
    <div className="pointer-events-none absolute left-1/2 top-1/2 z-10 -translate-x-1/2 -translate-y-full">
      <div className="flex flex-col items-center">
        <div className="flex size-12 items-center justify-center rounded-full bg-[#CC3333] shadow-[0_4px_16px_rgba(204,51,51,0.4)]">
          <MapPin className="size-6 text-white" />
        </div>
        <div className="h-3 w-1 rounded-b bg-[#CC3333]" />
      </div>
    </div>
  );
}

function InstructionBubble() {
  return (
    <div className="absolute left-1/2 top-[52%] z-10 -translate-x-1/2">
      <div className="rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] px-3 py-2 shadow-[0_4px_12px_rgba(0,0,0,0.3)]">
        <span className="whitespace-nowrap text-[13px] font-medium text-white">
          Drag map to position pin
        </span>
      </div>
    </div>
  );
}

function MapInner({
  locationMode,
  onCenterChange,
  onLocate,
}: {
  locationMode: LocationMode;
  onCenterChange: (lat: number, lng: number) => void;
  onLocate: () => void;
}) {
  const map = useGoogleMap();

  useEffect(() => {
    if (!map) return;
    const listener = map.addListener('idle', () => {
      const center = map.getCenter();
      if (center) {
        onCenterChange(center.lat(), center.lng());
      }
    });
    return () => google.maps.event.removeListener(listener);
  }, [map, onCenterChange]);

  return (
    <>
      <MapControls onLocate={onLocate} />
      {locationMode === 'pin' && (
        <>
          <CenterPin />
          <InstructionBubble />
        </>
      )}
    </>
  );
}

function ReportHazardContent() {
  const router = useRouter();
  const [locationMode, setLocationMode] = useState<LocationMode>('current');
  const [selectedLocation, setSelectedLocation] = useState<SelectedLocation | null>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const mapCenter = useRef<{ lat: number; lng: number }>(DEFAULT_CENTER);

  const handleCenterChange = useCallback((lat: number, lng: number) => {
    mapCenter.current = { lat, lng };
    if (locationMode === 'pin') {
      setSelectedLocation({ lat, lng });
    }
  }, [locationMode]);

  const handleLocate = useCallback(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          const loc = { lat: pos.coords.latitude, lng: pos.coords.longitude };
          setSelectedLocation(loc);
          mapCenter.current = loc;
        },
      );
    }
  }, []);

  const handleUseCurrentLocation = useCallback(() => {
    setLocationMode('current');
    handleLocate();
  }, [handleLocate]);

  const handleDropPin = useCallback(() => {
    setLocationMode('pin');
    setSelectedLocation(mapCenter.current);
  }, []);

  const handleConfirm = useCallback(() => {
    const params = new URLSearchParams();
    if (selectedLocation) {
      params.set('lat', selectedLocation.lat.toString());
      params.set('lng', selectedLocation.lng.toString());
      if (selectedLocation.address) params.set('address', selectedLocation.address);
    }
    router.push(`/report-hazard/details?${params.toString()}`);
  }, [selectedLocation, router]);

  return (
    <div className="relative flex h-dvh w-full flex-col bg-[#111111]">
      {/* Map */}
      <div className="absolute inset-0">
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
          <MapInner
            locationMode={locationMode}
            onCenterChange={handleCenterChange}
            onLocate={handleLocate}
          />
        </Map>
      </div>

      {/* Header */}
      <div className="relative z-20 flex h-14 items-center justify-between border-b border-[#2E2E2E] bg-[#1A1A1A] px-4">
        <button
          onClick={() => router.back()}
          className="flex size-9 items-center justify-center rounded-full bg-[#2E2E2E] transition-colors hover:bg-[#3E3E3E]"
        >
          <X className="size-[18px] text-white" />
        </button>
        <h1 className="text-base font-semibold text-white" style={{ fontFamily: 'var(--font-mono)' }}>
          Select Location
        </h1>
        <div className="flex items-center rounded-xl bg-[#2E2E2E] px-2.5 py-1">
          <span className="text-[11px] font-medium text-[#B8B9B6]">Step 1 of 2</span>
        </div>
      </div>

      {/* Search Bar */}
      <div className="relative z-20 px-4 pt-4">
        <div className="flex h-12 items-center gap-2.5 rounded-3xl border border-[#2E2E2E] bg-[#1A1A1A] px-4 shadow-[0_4px_16px_rgba(0,0,0,0.2)]">
          <Search className="size-5 shrink-0 text-[#B8B9B6]" />
          <input
            type="text"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            placeholder="Search address or place..."
            className="flex-1 bg-transparent text-sm text-white placeholder:text-[#B8B9B6] focus:outline-none"
          />
        </div>
      </div>

      {/* Bottom Sheet */}
      <div className="absolute inset-x-0 bottom-0 z-20 rounded-t-3xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-[0_-8px_24px_rgba(0,0,0,0.27)]">
        {/* Handle */}
        <div className="flex h-5 items-center justify-center">
          <div className="h-1 w-10 rounded-full bg-[#B8B9B6]" />
        </div>

        {/* Location Options */}
        <div className="flex flex-col gap-2 px-4">
          {/* Use Current Location */}
          <button
            onClick={handleUseCurrentLocation}
            className={`flex items-center gap-3 rounded-xl p-3.5 transition-colors ${
              locationMode === 'current'
                ? 'border-2 border-[#FF8400] bg-[#2E2E2E]'
                : 'border border-[#2E2E2E] bg-[#1A1A1A]'
            }`}
          >
            <div className={`flex size-10 items-center justify-center rounded-full ${
              locationMode === 'current' ? 'bg-[#FF8400]' : 'bg-[#2E2E2E]'
            }`}>
              <Crosshair className={`size-5 ${
                locationMode === 'current' ? 'text-[#111111]' : 'text-white'
              }`} />
            </div>
            <div className="flex flex-col items-start gap-0.5">
              <span className="text-sm font-semibold text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                Use Current Location
              </span>
              <span className="text-xs text-[#B8B9B6]">
                Report hazard at your current position
              </span>
            </div>
          </button>

          {/* Drop Pin on Map */}
          <button
            onClick={handleDropPin}
            className={`flex items-center gap-3 rounded-xl p-3.5 transition-colors ${
              locationMode === 'pin'
                ? 'border-2 border-[#FF8400] bg-[#2E2E2E]'
                : 'border border-[#2E2E2E] bg-[#1A1A1A]'
            }`}
          >
            <div className={`flex size-10 items-center justify-center rounded-full ${
              locationMode === 'pin' ? 'bg-[#FF8400]' : 'bg-[#2E2E2E]'
            }`}>
              <MapPin className={`size-5 ${
                locationMode === 'pin' ? 'text-[#111111]' : 'text-white'
              }`} />
            </div>
            <div className="flex flex-col items-start gap-0.5">
              <span className="text-sm font-semibold text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                Drop Pin on Map
              </span>
              <span className="text-xs text-[#B8B9B6]">
                Drag map to select hazard location
              </span>
            </div>
          </button>
        </div>

        {/* Confirm Button */}
        <div className="px-4 pb-8 pt-4">
          <button
            onClick={handleConfirm}
            className="flex h-[52px] w-full items-center justify-center gap-2 rounded-3xl bg-[#FF8400] font-semibold text-[#111111] transition-opacity hover:opacity-90"
            style={{ fontFamily: 'var(--font-mono)' }}
          >
            <Check className="size-[18px]" />
            <span className="text-[15px]">Confirm Location</span>
          </button>
        </div>
      </div>
    </div>
  );
}

export default function ReportHazardPage() {
  if (!API_KEY) {
    return (
      <div className="flex h-dvh w-full items-center justify-center bg-[#111111]">
        <div className="text-center text-sm text-[#B8B9B6]">
          <p className="font-mono text-lg">Map</p>
          <p className="mt-1">Set NEXT_PUBLIC_GOOGLE_MAPS_API_KEY to load Google Maps</p>
        </div>
      </div>
    );
  }

  return (
    <APIProvider apiKey={API_KEY}>
      <ReportHazardContent />
    </APIProvider>
  );
}
