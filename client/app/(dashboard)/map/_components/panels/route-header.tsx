'use client';

import { Navigation, Sparkles } from 'lucide-react';

export function RouteHeader() {
  return (
    <div className="flex items-center justify-between">
      <div className="flex items-center gap-2">
        <Navigation className="size-5 text-[#FF8400]" />
        <span className="font-mono text-base font-semibold text-white">Safe Route</span>
      </div>
      <div className="flex items-center gap-1 rounded-xl bg-[#FF8400] px-2 py-1">
        <Sparkles className="size-3 text-[#111]" />
        <span className="text-[11px] font-medium text-[#111]">Gemini</span>
      </div>
    </div>
  );
}
