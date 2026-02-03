'use client';

import { Search, Mic } from 'lucide-react';

export function SearchBar() {
  return (
    <div className="flex h-12 items-center gap-2.5 rounded-3xl border border-[#2E2E2E] bg-[#1A1A1A] px-4 shadow-[0_4px_16px_rgba(0,0,0,0.2)]">
      <Search className="size-5 shrink-0 text-[#B8B9B6]" />
      <span className="flex-1 text-sm text-[#B8B9B6]">Search destination...</span>
      <Mic className="size-5 shrink-0 text-[#FF8400]" />
    </div>
  );
}
