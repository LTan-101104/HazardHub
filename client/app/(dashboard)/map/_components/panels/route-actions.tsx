'use client';

import { Navigation, MessageCircle } from 'lucide-react';
import { useMap } from '../map-provider';

export function RouteActions() {
  const { dispatch } = useMap();

  return (
    <div className="flex gap-2.5">
      <button
        onClick={() => dispatch({ type: 'START_NAVIGATION' })}
        className="flex h-12 flex-1 items-center justify-center gap-2 rounded-3xl bg-[#00AA66] text-white transition-colors hover:bg-[#009959]"
      >
        <Navigation className="size-[18px]" />
        <span className="font-mono text-sm font-semibold">Start</span>
      </button>
      <button
        onClick={() => dispatch({ type: 'TOGGLE_CHAT', payload: true })}
        className="flex size-12 items-center justify-center rounded-3xl bg-[#4285F4] transition-colors hover:bg-[#3574d4]"
      >
        <MessageCircle className="size-5 text-white" />
      </button>
    </div>
  );
}
