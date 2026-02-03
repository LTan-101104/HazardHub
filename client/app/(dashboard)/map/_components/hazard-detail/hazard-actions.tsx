'use client';

import { RotateCcw } from 'lucide-react';
import { useMap } from '../map-provider';

export function HazardActions() {
  const { dispatch } = useMap();

  return (
    <div className="flex flex-col gap-2">
      <button className="flex h-12 items-center justify-center gap-2 rounded-2xl bg-[#FF8400] font-mono text-sm font-semibold text-black transition-colors hover:bg-[#e67700]">
        <RotateCcw className="size-4" />
        Avoid &amp; Reroute
      </button>
      <div className="flex gap-2">
        <button
          onClick={() => dispatch({ type: 'SELECT_HAZARD', payload: null })}
          className="flex h-10 flex-1 items-center justify-center rounded-xl border border-[#2E2E2E] text-sm text-[#B8B9B6] transition-colors hover:bg-[#252525]"
        >
          Confirm
        </button>
        <button
          onClick={() => dispatch({ type: 'SELECT_HAZARD', payload: null })}
          className="flex h-10 flex-1 items-center justify-center rounded-xl border border-[#2E2E2E] text-sm text-[#B8B9B6] transition-colors hover:bg-[#252525]"
        >
          Dismiss
        </button>
      </div>
    </div>
  );
}
