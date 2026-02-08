'use client';

import { AdvancedMarker } from '@vis.gl/react-google-maps';
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
  return (
    <>
      {hazards.map((hazard) => {
        const color = SEVERITY_COLORS[hazard.severity] || '#FF8400';
        return (
          <AdvancedMarker
            key={hazard.id}
            position={hazard.position}
            onClick={() => onSelect(hazard)}
            zIndex={5}
            title={hazard.title}
          >
            <svg
              width="28"
              height="28"
              viewBox="0 0 24 24"
              xmlns="http://www.w3.org/2000/svg"
              style={{ filter: 'drop-shadow(0 2px 4px rgba(0,0,0,0.5))' }}
            >
              <path d="M12 2L1 21h22L12 2z" fill={color} stroke="#FFFFFF" strokeWidth="2" />
              <text x="12" y="17" textAnchor="middle" fill="#FFFFFF" fontSize="10" fontWeight="bold">
                !
              </text>
            </svg>
          </AdvancedMarker>
        );
      })}
    </>
  );
}
