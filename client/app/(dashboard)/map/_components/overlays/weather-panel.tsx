'use client';

import { CloudSnow, Cloud, Sun, CloudRain } from 'lucide-react';
import { useMap } from '../map-provider';

const weatherIcons: Record<string, React.ElementType> = {
  Snow: CloudSnow,
  Rain: CloudRain,
  Cloudy: Cloud,
  Clear: Sun,
};

export function WeatherPanel() {
  const { state } = useMap();
  const { weather } = state;
  const Icon = weatherIcons[weather.condition] ?? Cloud;

  return (
    <div className="flex items-center gap-2 rounded-2xl border border-[#2E2E2E] bg-[#1A1A1A] px-3 py-2 shadow-[0_4px_12px_rgba(0,0,0,0.12)]">
      <Icon className="size-5 text-[#4A90D9]" />
      <div className="flex flex-col">
        <span className="font-mono text-xs font-semibold text-white">
          {weather.tempF}&deg;F &middot; {weather.condition}
        </span>
        {weather.warning && <span className="text-[10px] text-[#B8B9B6]">{weather.warning}</span>}
      </div>
    </div>
  );
}
