'use client';

import { Check, ShieldCheck } from 'lucide-react';
import type { RouteCardData } from '@/types/map';

const badgeColors = {
  safe: { bg: 'bg-[#22C55E]/15', text: 'text-[#22C55E]' },
  caution: { bg: 'bg-[#FFB300]/15', text: 'text-[#FFB300]' },
  danger: { bg: 'bg-[#FF5252]/15', text: 'text-[#FF5252]' },
};

export function RouteSuggestionCard({ card }: { card: RouteCardData }) {
  const badge = badgeColors[card.safetyBadge];

  return (
    <div className="flex flex-col gap-3 rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] p-3">
      {/* Route name + safety badge */}
      <div className="flex items-center justify-between">
        <span className="font-mono text-sm font-semibold text-white">{card.name}</span>
        <div className={`flex items-center gap-1 rounded-full px-2 py-0.5 ${badge.bg}`}>
          <ShieldCheck className={`size-3 ${badge.text}`} />
          <span className={`text-[10px] font-medium capitalize ${badge.text}`}>{card.safetyBadge}</span>
        </div>
      </div>

      {/* Stats */}
      <p className="text-xs text-[#B8B9B6]">
        {card.distanceMiles} mi · {card.etaMinutes} min &nbsp;&nbsp; {card.terrain}
      </p>

      {/* Tags */}
      <div className="flex flex-wrap gap-1.5">
        {card.tags.map((tag) => (
          <span key={tag} className="rounded-full bg-[#2E2E2E] px-2.5 py-1 text-[11px] text-[#B8B9B6]">
            {tag}
          </span>
        ))}
      </div>

      {/* Use route button */}
      <button className="flex h-10 items-center justify-center gap-2 rounded-xl bg-[#FF8400] font-mono text-sm font-semibold text-black transition-colors hover:bg-[#e67700]">
        <Check className="size-4" />
        Use This Route
      </button>
    </div>
  );
}
