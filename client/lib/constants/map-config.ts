import type { LatLng, HazardMarker, RouteInfo, WeatherInfo, ChatMessage } from '@/types/map';

export const DEFAULT_CENTER: LatLng = {
  lat: 40.7128,
  lng: -74.006,
};

export const DEFAULT_ZOOM = 14;

export const DARK_MAP_STYLES = [
  { elementType: 'geometry', stylers: [{ color: '#1a2633' }] },
  { elementType: 'labels.text.stroke', stylers: [{ color: '#1a2633' }] },
  { elementType: 'labels.text.fill', stylers: [{ color: '#746855' }] },
  { featureType: 'road', elementType: 'geometry', stylers: [{ color: '#38414e' }] },
  { featureType: 'road', elementType: 'geometry.stroke', stylers: [{ color: '#212a37' }] },
  { featureType: 'road.highway', elementType: 'geometry', stylers: [{ color: '#746855' }] },
  { featureType: 'road.highway', elementType: 'geometry.stroke', stylers: [{ color: '#1f2835' }] },
  { featureType: 'water', elementType: 'geometry', stylers: [{ color: '#17263c' }] },
  { featureType: 'poi', elementType: 'labels', stylers: [{ visibility: 'off' }] },
  { featureType: 'transit', stylers: [{ visibility: 'off' }] },
];

// Mock data for demo
export const MOCK_WEATHER: WeatherInfo = {
  tempF: 28,
  condition: 'Snow',
  warning: 'Slippery roads',
  icon: 'cloud-snow',
};

export const MOCK_HAZARDS: HazardMarker[] = [
  {
    id: 'h1',
    position: { lat: 40.715, lng: -74.009 },
    type: 'snow',
    severity: 'high',
    title: 'Deep Snow Drift',
    description:
      'Snow accumulation of 12+ inches blocking the right lane. Multiple vehicles have been getting stuck in this area. Reported by 3 community members in the last hour.',
    reportedAt: new Date(Date.now() - 45 * 60 * 1000).toISOString(),
    reportCount: 3,
  },
  {
    id: 'h2',
    position: { lat: 40.71, lng: -74.003 },
    type: 'ice',
    severity: 'medium',
    title: 'Black Ice',
    description: 'Black ice reported on bridge surface. Exercise caution.',
    reportedAt: new Date(Date.now() - 120 * 60 * 1000).toISOString(),
    reportCount: 5,
  },
];

export const MOCK_ROUTES: { active: RouteInfo; alternate: RouteInfo } = {
  active: {
    id: 'r1',
    name: 'Safest Route',
    from: 'Current Location',
    to: 'Memorial Hospital',
    fromPosition: { lat: 40.712, lng: -74.012 },
    toPosition: { lat: 40.718, lng: -73.998 },
    distanceMiles: 4.2,
    etaMinutes: 12,
    safetyPercent: 92,
    type: 'safest',
    hazards: [],
    description: 'Avoids 2 hazards · Plowed roads',
  },
  alternate: {
    id: 'r2',
    name: 'Fastest Route',
    from: 'Current Location',
    to: 'Memorial Hospital',
    fromPosition: { lat: 40.712, lng: -74.012 },
    toPosition: { lat: 40.718, lng: -73.998 },
    distanceMiles: 3.8,
    etaMinutes: 9,
    safetyPercent: 74,
    type: 'fastest',
    hazards: [],
    description: '3.8 mi · 1 hazard warning',
  },
};

export const MOCK_CHAT_MESSAGES: ChatMessage[] = [
  {
    id: 'c1',
    role: 'user',
    content: 'I need to get to the hospital, but I only have a FWD sedan. Find me the flattest, most plowed route.',
    timestamp: new Date(Date.now() - 5 * 60 * 1000).toISOString(),
  },
  {
    id: 'c2',
    role: 'ai',
    content: "I found a route optimized for your FWD sedan! Here's what I recommend:",
    timestamp: new Date(Date.now() - 4 * 60 * 1000).toISOString(),
    routeCard: {
      name: 'Via Main Avenue',
      distanceMiles: 4.2,
      etaMinutes: 14,
      safetyBadge: 'safe',
      terrain: 'Flat terrain',
      tags: ['Plowed', 'Low grade', 'FWD OK'],
    },
  },
];

export const MOCK_TURN_INSTRUCTION = {
  direction: 'straight' as const,
  distanceMiles: 0.8,
  streetName: 'Main Street',
};
