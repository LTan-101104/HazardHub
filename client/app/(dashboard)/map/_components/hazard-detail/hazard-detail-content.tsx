'use client';

import { X, Clock, Users, Sparkles } from 'lucide-react';
import { useMap } from '../map-provider';
import { GeminiSuggestion } from './gemini-suggestion';
import { HazardActions } from './hazard-actions';
import type { HazardMarker } from '@/types/map';

function formatTimeAgo(isoDate: string): string {
  const mins = Math.round((Date.now() - new Date(isoDate).getTime()) / 60000);
  if (mins < 60) return `${mins} min ago`;
  return `${Math.round(mins / 60)}h ago`;
}

const severityColors: Record<string, string> = {
  critical: 'bg-red-600',
  high: 'bg-[#FF8400]',
  medium: 'bg-yellow-500',
  low: 'bg-green-500',
};

export function HazardDetailContent({ hazard }: { hazard: HazardMarker }) {
  const { dispatch } = useMap();

  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="flex items-start justify-between px-4 pt-4">
        <div className="flex flex-col gap-1">
          <h3 className="font-mono text-lg font-semibold text-white">{hazard.title}</h3>
          <div className="flex items-center gap-2">
            <span className={`inline-block size-2 rounded-full ${severityColors[hazard.severity]}`} />
            <span className="text-xs capitalize text-[#FF8400]">{hazard.severity} Severity</span>
          </div>
        </div>
        <button
          onClick={() => dispatch({ type: 'SELECT_HAZARD', payload: null })}
          className="text-[#B8B9B6] transition-colors hover:text-white"
        >
          <X className="size-5" />
        </button>
      </div>

      {/* Description */}
      <p className="px-4 pt-3 text-sm leading-relaxed text-[#B8B9B6]">{hazard.description}</p>

      {/* Meta */}
      <div className="flex gap-4 px-4 pt-3">
        <div className="flex items-center gap-1.5 text-xs text-[#B8B9B6]">
          <Clock className="size-3.5" />
          <span>{formatTimeAgo(hazard.reportedAt)}</span>
        </div>
        <div className="flex items-center gap-1.5 text-xs text-[#B8B9B6]">
          <Users className="size-3.5" />
          <span>{hazard.reportCount} reports</span>
        </div>
      </div>

      {/* Gemini Suggestion */}
      <div className="px-4 pt-4">
        <GeminiSuggestion />
      </div>

      {/* Actions */}
      <div className="px-4 pb-6 pt-4">
        <HazardActions />
      </div>
    </div>
  );
}
