'use client';

import { ShieldCheck, Route } from 'lucide-react';
import { useMap } from '../map-provider';

export function RouteOptions() {
  const { state } = useMap();

  const options = [
    {
      route: state.activeRoute,
      icon: ShieldCheck,
      selected: true,
    },
    {
      route: state.alternateRoute,
      icon: Route,
      selected: false,
    },
  ];

  return (
    <div className="flex flex-col gap-2">
      {options.map((opt) => {
        if (!opt.route) return null;
        const Icon = opt.icon;
        return (
          <button
            key={opt.route.id}
            className={`flex items-center gap-2.5 rounded-[10px] p-3 text-left transition-colors ${
              opt.selected ? 'border-2 border-[#FF8400] bg-[#FF8400]' : 'border border-[#2E2E2E] bg-[#1A1A1A] hover:bg-[#222]'
            }`}
          >
            <Icon className={`size-[18px] shrink-0 ${opt.selected ? 'text-[#111]' : 'text-[#B8B9B6]'}`} />
            <div className="flex flex-1 flex-col">
              <span className={`text-sm font-semibold ${opt.selected ? 'text-[#111]' : 'text-white'}`}>{opt.route.name}</span>
              <span className={`text-xs ${opt.selected ? 'text-[#111]/70' : 'text-[#B8B9B6]'}`}>{opt.route.description}</span>
            </div>
          </button>
        );
      })}
    </div>
  );
}
