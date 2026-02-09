'use client';

import { Sparkles, X } from 'lucide-react';
import { useMap } from '../map-provider';

export function ChatHeader() {
  const { dispatch } = useMap();

  return (
    <div className="shrink-0 flex items-center justify-between px-4 py-3">
      <div className="flex items-center gap-3">
        <div className="flex size-8 items-center justify-center rounded-full bg-[#4285F4]">
          <Sparkles className="size-4 text-white" />
        </div>
        <div className="flex flex-col">
          <span className="font-mono text-sm font-semibold text-white">Ask Gemini</span>
          <span className="text-xs text-[#B8B9B6]">AI Route Assistant</span>
        </div>
      </div>
      <button
        onClick={() => dispatch({ type: 'TOGGLE_CHAT', payload: false })}
        className="text-[#B8B9B6] transition-colors hover:text-white"
      >
        <X className="size-5" />
      </button>
    </div>
  );
}
