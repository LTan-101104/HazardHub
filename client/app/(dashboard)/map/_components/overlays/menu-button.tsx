'use client';

import { Menu } from 'lucide-react';
import { useMap } from '../map-provider';

export function MenuButton() {
  const { dispatch } = useMap();

  return (
    <button
      onClick={() => dispatch({ type: 'TOGGLE_DRAWER', payload: true })}
      className="flex size-10 items-center justify-center rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-[0_2px_8px_rgba(0,0,0,0.12)] transition-colors hover:bg-[#252525] lg:hidden"
    >
      <Menu className="size-5 text-white" />
    </button>
  );
}
