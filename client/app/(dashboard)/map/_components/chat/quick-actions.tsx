'use client';

const QUICK_ACTIONS = ['Avoid steep hills', 'Find gas station', 'Check road conditions', 'Alternative routes'];

export function QuickActions() {
  return (
    <div className="flex flex-wrap gap-2 px-4 py-2">
      {QUICK_ACTIONS.map((action) => (
        <button
          key={action}
          className="rounded-full border border-[#2E2E2E] bg-transparent px-3 py-1.5 text-xs text-[#B8B9B6] transition-colors hover:bg-[#252525]"
        >
          {action}
        </button>
      ))}
    </div>
  );
}
