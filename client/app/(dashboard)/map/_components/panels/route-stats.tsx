'use client';

import { useMap } from '../map-provider';

export function RouteStats() {
  const { state } = useMap();
  const route = state.activeRoute;

  const stats = [
    {
      value: route ? `${route.distanceMiles} mi` : '—',
      label: 'Distance',
      accent: false,
    },
    {
      value: route ? `${route.etaMinutes} min` : '—',
      label: 'ETA',
      accent: false,
    },
    {
      value: route ? (route.safetyPercent > 0 ? `${route.safetyPercent}%` : '...') : '—',
      label: 'Safety',
      accent: true,
    },
  ];

  return (
    <div className="flex gap-2">
      {stats.map((stat) => (
        <div
          key={stat.label}
          className={`flex flex-1 flex-col items-center gap-0.5 rounded-lg p-2.5 ${
            stat.accent ? 'bg-[#222924]' : 'bg-[#2E2E2E]'
          }`}
        >
          <span className={`font-mono text-sm font-semibold ${stat.accent ? 'text-[#B6FFCE]' : 'text-white'}`}>{stat.value}</span>
          <span className={`text-[11px] ${stat.accent ? 'text-[#B6FFCE]' : 'text-[#B8B9B6]'}`}>{stat.label}</span>
        </div>
      ))}
    </div>
  );
}
