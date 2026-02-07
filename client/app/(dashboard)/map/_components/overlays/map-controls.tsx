'use client';

import { Plus, Minus, Crosshair } from 'lucide-react';

function ControlButton({ children, onClick }: { children: React.ReactNode; onClick?: () => void }) {
  return (
    <button
      onClick={onClick}
      className="flex size-10 items-center justify-center rounded-xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-[0_2px_8px_rgba(0,0,0,0.12)] transition-colors hover:bg-[#252525]"
    >
      {children}
    </button>
  );
}

export function MapControls() {
  return (
    <div className="flex flex-col gap-2">
      <ControlButton>
        <Plus className="size-[18px] text-white" />
      </ControlButton>
      <ControlButton>
        <Minus className="size-[18px] text-white" />
      </ControlButton>
      <div className="mt-2">
        <ControlButton>
          <Crosshair className="size-[18px] text-[#0066CC]" />
        </ControlButton>
      </div>
    </div>
  );
}
