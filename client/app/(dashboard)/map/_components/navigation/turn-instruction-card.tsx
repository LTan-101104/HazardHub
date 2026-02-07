'use client';

import { ArrowUp, CornerDownRight, CornerDownLeft, CornerUpLeft } from 'lucide-react';

interface TurnInstructionCardProps {
  direction: 'left' | 'right' | 'straight' | 'u-turn' | 'arrive';
  distanceMiles: number;
  streetName: string;
}

const directionIcons: Record<string, React.ElementType> = {
  straight: ArrowUp,
  right: CornerDownRight,
  left: CornerDownLeft,
  'u-turn': CornerUpLeft,
  arrive: ArrowUp,
};

export function TurnInstructionCard({ direction, distanceMiles, streetName }: TurnInstructionCardProps) {
  const Icon = directionIcons[direction] ?? ArrowUp;

  return (
    <div className="flex items-center gap-3 rounded-2xl border border-[#2E2E2E] bg-[#1A1A1A] p-3 shadow-[0_4px_16px_rgba(0,0,0,0.2)]">
      <div className="flex size-10 items-center justify-center rounded-xl bg-[#4285F4]">
        <Icon className="size-5 text-white" />
      </div>
      <div className="flex flex-col">
        <span className="font-mono text-lg font-semibold text-white">{distanceMiles} mi</span>
        <span className="text-xs text-[#B8B9B6]">Continue on {streetName}</span>
      </div>
    </div>
  );
}
