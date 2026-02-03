'use client';

import { Sparkles } from 'lucide-react';
import { useMap } from '../map-provider';

export function AskGeminiFab() {
  const { dispatch } = useMap();

  return (
    <button
      onClick={() => dispatch({ type: 'TOGGLE_CHAT', payload: true })}
      className="flex items-center gap-2 rounded-3xl bg-[#4285F4] px-4 py-3 shadow-[0_4px_16px_rgba(66,133,244,0.4)] transition-colors hover:bg-[#3574d4]"
    >
      <Sparkles className="size-[18px] text-white" />
      <span className="font-mono text-[13px] font-semibold text-white">Ask Gemini</span>
    </button>
  );
}
