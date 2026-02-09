'use client';

const QUICK_ACTIONS = ['Avoid steep hills', 'Find gas station', 'Check road conditions', 'Alternative routes'];

interface QuickActionsProps {
  onSelect: (message: string) => void;
  disabled: boolean;
}

export function QuickActions({ onSelect, disabled }: QuickActionsProps) {
  return (
    <div className="flex flex-wrap gap-2 px-4 py-2">
      {QUICK_ACTIONS.map((action) => (
        <button
          key={action}
          onClick={() => onSelect(action)}
          disabled={disabled}
          className="rounded-full border border-[#2E2E2E] bg-transparent px-3 py-1.5 text-xs text-[#B8B9B6] transition-colors hover:bg-[#252525] disabled:cursor-not-allowed disabled:opacity-60"
        >
          {action}
        </button>
      ))}
    </div>
  );
}
