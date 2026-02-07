'use client';

import { useEffect, useRef } from 'react';
import { useMap } from '@vis.gl/react-google-maps';
import type { HazardMarker } from '@/types/map';

interface HazardMarkersProps {
  hazards: HazardMarker[];
  onSelect: (hazard: HazardMarker) => void;
}

const SEVERITY_COLORS: Record<string, string> = {
  critical: '#FF3333',
  high: '#FF8400',
  medium: '#FFB800',
  low: '#4CAF50',
};

export function HazardMarkers({ hazards, onSelect }: HazardMarkersProps) {
  const map = useMap();
  const markersRef = useRef<google.maps.Marker[]>([]);

  useEffect(() => {
    if (!map) return;

    // Clear old markers
    markersRef.current.forEach((m) => m.setMap(null));
    markersRef.current = [];

    hazards.forEach((hazard) => {
      const color = SEVERITY_COLORS[hazard.severity] || '#FF8400';

      const marker = new google.maps.Marker({
        position: { lat: hazard.position.lat, lng: hazard.position.lng },
        map,
        icon: {
          path: 'M12 2L1 21h22L12 2z', // triangle
          fillColor: color,
          fillOpacity: 1,
          strokeColor: '#FFFFFF',
          strokeWeight: 2,
          scale: 1.2,
          anchor: new google.maps.Point(12, 21),
        },
        title: hazard.title,
        zIndex: 5,
      });

      marker.addListener('click', () => onSelect(hazard));
      markersRef.current.push(marker);
    });

    return () => {
      markersRef.current.forEach((m) => m.setMap(null));
      markersRef.current = [];
    };
  }, [map, hazards, onSelect]);

  return null;
}
