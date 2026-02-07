'use client';

import { X } from 'lucide-react';
import { useMap } from '../map-provider';

export function HazardAlertPopup() {
  const { dispatch } = useMap();

  return (
    <div className="flex flex-col gap-2 rounded-2xl border border-[#2E2E2E] bg-[#1A1A1A] p-3 shadow-[0_4px_16px_rgba(0,0,0,0.2)]">
      <div className="flex items-start justify-between gap-3">
        <div className="flex items-start gap-3">
          <div className="mt-0.5 flex size-8 shrink-0 items-center justify-center rounded-full bg-[#FF8400]">
            <span className="text-sm font-bold text-black">!</span>
          </div>
          <div>
            <p className="font-mono text-sm font-semibold text-white">Hazard Ahead</p>
            <p className="text-xs text-[#B8B9B6]">Road construction in 0.3 mi</p>
          </div>
        </div>
        <button
          onClick={() => dispatch({ type: 'SET_HAZARD_ALERT', payload: false })}
          className="shrink-0 text-[#B8B9B6] transition-colors hover:text-white"
        >
          <X className="size-4" />
        </button>
      </div>
      <div className="flex gap-2">
        <button
          onClick={() => dispatch({ type: 'SHOW_HAZARD_DETAIL', payload: true })}
          className="flex h-9 flex-1 items-center justify-center gap-1.5 rounded-full border border-[#2E2E2E] text-xs font-medium text-white transition-colors hover:bg-[#252525]"
        >
          <span>&#9432;</span> Details
        </button>
        <button className="flex h-9 flex-1 items-center justify-center rounded-full bg-[#FF8400] text-xs font-semibold text-black transition-colors hover:bg-[#e67700]">
          Reroute
        </button>
      </div>
    </div>
  );
}
