'use client';

import { Sparkles } from 'lucide-react';

export function GeminiSuggestion() {
  return (
    <div className="flex gap-3 rounded-xl border border-[#2E2E2E] bg-[#242424] p-3">
      <div className="flex size-8 shrink-0 items-center justify-center rounded-full bg-[#4285F4]">
        <Sparkles className="size-4 text-white" />
      </div>
      <div className="flex flex-col gap-1">
        <span className="text-sm font-medium text-[#FF8400]">Gemini Suggestion</span>
        <p className="text-xs leading-relaxed text-[#B8B9B6]">
          An alternate route via Main Ave adds only 2 minutes but avoids this hazard entirely.
        </p>
      </div>
    </div>
  );
}
