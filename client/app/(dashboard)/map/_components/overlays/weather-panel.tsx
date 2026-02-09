'use client';

import { useEffect } from 'react';
import { CloudSnow, Cloud, Sun, CloudRain, CloudDrizzle, CloudFog, CloudLightning, Loader2 } from 'lucide-react';
import { useMap } from '../map-provider';
import { fetchWeather } from '@/lib/actions/weather-action';
import { DEFAULT_CENTER } from '@/lib/constants/map-config';

const weatherIcons: Record<string, React.ElementType> = {
  Snow: CloudSnow,
  Rain: CloudRain,
  Cloudy: Cloud,
  Clear: Sun,
  Drizzle: CloudDrizzle,
  Fog: CloudFog,
  Thunderstorm: CloudLightning,
  Loading: Loader2,
};

const REFRESH_INTERVAL_MS = 10 * 60 * 1000; // 10 minutes

export function WeatherPanel() {
  const { state, dispatch } = useMap();
  const { weather } = state;
  const Icon = weatherIcons[weather.icon] ?? Cloud;
  const isLoading = weather.condition === 'Loading';

  useEffect(() => {
    let cancelled = false;

    async function loadWeather(lat: number, lng: number) {
      try {
        const info = await fetchWeather(lat, lng);
        if (!cancelled) {
          dispatch({ type: 'SET_WEATHER', payload: info });
        }
      } catch (err) {
        console.error('Failed to fetch weather:', err);
        if (!cancelled) {
          dispatch({
            type: 'SET_WEATHER',
            payload: { tempF: 0, condition: 'Unavailable', icon: 'Cloudy' },
          });
        }
      }
    }

    function startFetching(lat: number, lng: number) {
      loadWeather(lat, lng);
      const interval = setInterval(() => loadWeather(lat, lng), REFRESH_INTERVAL_MS);
      return interval;
    }

    let intervalId: ReturnType<typeof setInterval> | undefined;

    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          intervalId = startFetching(pos.coords.latitude, pos.coords.longitude);
        },
        () => {
          // Fallback to default center
          intervalId = startFetching(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng);
        },
      );
    } else {
      intervalId = startFetching(DEFAULT_CENTER.lat, DEFAULT_CENTER.lng);
    }

    return () => {
      cancelled = true;
      if (intervalId) clearInterval(intervalId);
    };
  }, [dispatch]);

  return (
    <div className="flex items-center gap-2 rounded-2xl border border-[#2E2E2E] bg-[#1A1A1A] px-3 py-2 shadow-[0_4px_12px_rgba(0,0,0,0.12)]">
      <Icon className={`size-5 text-[#4A90D9] ${isLoading ? 'animate-spin' : ''}`} />
      <div className="flex flex-col">
        <span className="font-mono text-xs font-semibold text-white">
          {isLoading ? 'Loading\u2026' : <>{weather.tempF}&deg;F &middot; {weather.condition}</>}
        </span>
        {weather.warning && <span className="text-[10px] text-[#B8B9B6]">{weather.warning}</span>}
      </div>
    </div>
  );
}
