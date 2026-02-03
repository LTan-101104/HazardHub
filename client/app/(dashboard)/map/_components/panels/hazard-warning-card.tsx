'use client';

import { Megaphone } from 'lucide-react';

interface HazardWarningCardProps {
  title: string;
  description: string;
}

export function HazardWarningCard({ title, description }: HazardWarningCardProps) {
  return (
    <div className="flex gap-3 bg-[#291C0F] px-5 py-4">
      <Megaphone className="mt-0.5 size-6 shrink-0 text-[#FF8400]" />
      <div className="flex flex-col gap-1">
        <p className="text-base font-medium leading-snug text-[#FF8400]">{title}</p>
        <p className="text-sm leading-snug text-[#FF8400]">{description}</p>
      </div>
    </div>
  );
}
