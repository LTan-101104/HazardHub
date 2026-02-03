'use client';

import { useMap } from '../map-provider';

export function LocationInputs() {
  const { state } = useMap();

  return (
    <div className="flex flex-col gap-2.5">
      <div className="flex items-center gap-2.5">
        <div className="size-2.5 shrink-0 rounded-full bg-[#FF8400]" />
        <div className="flex h-10 flex-1 items-center rounded-lg bg-[#2E2E2E] px-3">
          <span className="text-sm text-white">{state.fromLocation}</span>
        </div>
      </div>
      <div className="flex items-center gap-2.5">
        <div className="size-2.5 shrink-0 rounded-full bg-[#22C55E]" />
        <div className="flex h-10 flex-1 items-center rounded-lg bg-[#2E2E2E] px-3">
          <span className="text-sm text-white">{state.toLocation}</span>
        </div>
      </div>
    </div>
  );
}
