import axios from 'axios';
import type { WeatherInfo } from '@/types/map';

/**
 * WMO Weather interpretation codes mapped to condition labels and icons.
 * Reference: https://open-meteo.com/en/docs#weathervariables
 */
const WMO_CODE_MAP: Record<number, { condition: string; icon: string }> = {
  0: { condition: 'Clear', icon: 'Clear' },
  1: { condition: 'Mostly Clear', icon: 'Clear' },
  2: { condition: 'Partly Cloudy', icon: 'Cloudy' },
  3: { condition: 'Overcast', icon: 'Cloudy' },
  45: { condition: 'Foggy', icon: 'Fog' },
  48: { condition: 'Icy Fog', icon: 'Fog' },
  51: { condition: 'Light Drizzle', icon: 'Drizzle' },
  53: { condition: 'Drizzle', icon: 'Drizzle' },
  55: { condition: 'Heavy Drizzle', icon: 'Drizzle' },
  56: { condition: 'Freezing Drizzle', icon: 'Drizzle' },
  57: { condition: 'Heavy Freezing Drizzle', icon: 'Drizzle' },
  61: { condition: 'Light Rain', icon: 'Rain' },
  63: { condition: 'Rain', icon: 'Rain' },
  65: { condition: 'Heavy Rain', icon: 'Rain' },
  66: { condition: 'Freezing Rain', icon: 'Rain' },
  67: { condition: 'Heavy Freezing Rain', icon: 'Rain' },
  71: { condition: 'Light Snow', icon: 'Snow' },
  73: { condition: 'Snow', icon: 'Snow' },
  75: { condition: 'Heavy Snow', icon: 'Snow' },
  77: { condition: 'Snow Grains', icon: 'Snow' },
  80: { condition: 'Light Showers', icon: 'Rain' },
  81: { condition: 'Showers', icon: 'Rain' },
  82: { condition: 'Heavy Showers', icon: 'Rain' },
  85: { condition: 'Light Snow Showers', icon: 'Snow' },
  86: { condition: 'Heavy Snow Showers', icon: 'Snow' },
  95: { condition: 'Thunderstorm', icon: 'Thunderstorm' },
  96: { condition: 'Thunderstorm w/ Hail', icon: 'Thunderstorm' },
  99: { condition: 'Thunderstorm w/ Heavy Hail', icon: 'Thunderstorm' },
};

function celsiusToFahrenheit(c: number): number {
  return Math.round(c * 9 / 5 + 32);
}

function deriveWarning(weatherCode: number, windSpeed: number): string | undefined {
  if (weatherCode >= 95) return 'Severe thunderstorm';
  if (weatherCode >= 66 && weatherCode <= 67) return 'Freezing rain — slippery roads';
  if (weatherCode >= 71 && weatherCode <= 77) return 'Slippery roads';
  if (weatherCode >= 85 && weatherCode <= 86) return 'Snow showers — reduced visibility';
  if (weatherCode === 45 || weatherCode === 48) return 'Low visibility';
  if (windSpeed > 50) return 'High wind advisory';
  return undefined;
}

interface OpenMeteoCurrentResponse {
  current: {
    temperature_2m: number;
    weather_code: number;
    wind_speed_10m: number;
  };
}

/**
 * Fetches current weather for given coordinates using the Open-Meteo API.
 * No API key required.
 */
export async function fetchWeather(lat: number, lng: number): Promise<WeatherInfo> {
  const { data } = await axios.get<OpenMeteoCurrentResponse>(
    'https://api.open-meteo.com/v1/forecast',
    {
      params: {
        latitude: lat,
        longitude: lng,
        current: 'temperature_2m,weather_code,wind_speed_10m',
        temperature_unit: 'celsius',
        wind_speed_unit: 'kmh',
      },
    },
  );

  const { temperature_2m, weather_code, wind_speed_10m } = data.current;
  const mapped = WMO_CODE_MAP[weather_code] ?? { condition: 'Unknown', icon: 'Cloudy' };

  return {
    tempF: celsiusToFahrenheit(temperature_2m),
    condition: mapped.condition,
    icon: mapped.icon,
    warning: deriveWarning(weather_code, wind_speed_10m),
  };
}
