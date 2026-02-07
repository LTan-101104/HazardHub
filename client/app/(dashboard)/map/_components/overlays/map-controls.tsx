'use client';

import { Plus, Minus, Crosshair, AlertTriangle } from 'lucide-react';
import { useMap } from '../map-provider';
import { cn } from '@/lib/utils';

function ControlButton({
  children,
  onClick,
  active = false,
  variant = 'default'
}: {
  children: React.ReactNode;
  onClick?: () => void;
  active?: boolean;
  variant?: 'default' | 'sos';
}) {
  return (
    <button
      onClick={onClick}
      className={cn(
        "flex size-10 items-center justify-center rounded-xl border shadow-[0_2px_8px_rgba(0,0,0,0.12)] transition-all",
        variant === 'sos' && active
          ? "border-red-500 bg-red-500 animate-pulse"
          : variant === 'sos'
            ? "border-red-500/50 bg-[#1A1A1A] hover:bg-red-500/20"
            : "border-[#2E2E2E] bg-[#1A1A1A] hover:bg-[#252525]"
      )}
    >
      {children}
    </button>
  );
}

export function MapControls() {
  const { state, dispatch } = useMap();

  const toggleSOSPinMode = () => {
    dispatch({ type: 'TOGGLE_SOS_PIN_MODE', payload: !state.isSOSPinMode });
  };

  return (
    <div className="flex flex-col gap-2">
      {/* SOS Button */}
      <ControlButton onClick={toggleSOSPinMode} active={state.isSOSPinMode} variant="sos">
        <AlertTriangle className={cn(
          "size-[18px]",
          state.isSOSPinMode ? "text-white" : "text-red-500"
        )} />
      </ControlButton>

      <div className="mt-2 flex flex-col gap-2">
        <ControlButton>
          <Plus className="size-[18px] text-white" />
        </ControlButton>
        <ControlButton>
          <Minus className="size-[18px] text-white" />
        </ControlButton>
      </div>

      <div className="mt-2">
        <ControlButton>
          <Crosshair className="size-[18px] text-[#0066CC]" />
        </ControlButton>
      </div>
    </div>
  );
}
