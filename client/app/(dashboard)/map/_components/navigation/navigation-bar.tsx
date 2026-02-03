'use client';

import { X } from 'lucide-react';
import { useMap } from '../map-provider';

export function NavigationBar() {
  const { state, dispatch } = useMap();

  const stats = [
    { value: state.navigationEta, label: 'ETA' },
    { value: state.navigationDistance, label: 'Distance' },
    { value: state.navigationArrival, label: 'Arrival' },
  ];

  return (
    <div className="flex flex-col gap-3 rounded-t-3xl border-t border-[#2E2E2E] bg-[#1A1A1A] px-4 pb-8 pt-4 shadow-[0_-8px_24px_rgba(0,0,0,0.2)]">
      <div className="flex justify-between">
        {stats.map((stat) => (
          <div key={stat.label} className="flex flex-col items-center">
            <span className="font-mono text-lg font-semibold text-white">{stat.value}</span>
            <span className="text-xs text-[#B8B9B6]">{stat.label}</span>
          </div>
        ))}
      </div>
      <button
        onClick={() => dispatch({ type: 'END_NAVIGATION' })}
        className="flex h-12 items-center justify-center gap-2 rounded-2xl bg-[#DC2626] font-mono text-sm font-semibold text-white transition-colors hover:bg-[#B91C1C]"
      >
        <X className="size-4" />
        End Navigation
      </button>
    </div>
  );
}
