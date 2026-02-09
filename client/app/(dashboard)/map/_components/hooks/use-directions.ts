'use client';

import { useCallback, useRef, useEffect } from 'react';
import { useMapsLibrary } from '@vis.gl/react-google-maps';
import type { LatLng, RouteInfo, DirectionStep } from '@/types/map';

interface DirectionsResult {
  routes: RouteInfo[];
  rawResponse: google.maps.DirectionsResult | null;
}

interface UseDirectionsReturn {
  calculateRoute: (
    origin: LatLng,
    destination: LatLng,
    options?: { provideAlternatives?: boolean },
  ) => Promise<DirectionsResult | null>;
  isReady: boolean;
}

function generateRouteId(): string {
  if (typeof crypto !== 'undefined' && crypto.randomUUID) {
    return `route-${crypto.randomUUID()}`;
  }
  return `route-${Date.now()}-${Math.random().toString(36).substr(2, 9)}`;
}

export function metersToMiles(meters: number): number {
  return Number((meters * 0.000621371).toFixed(1));
}

export function secondsToMinutes(seconds: number): number {
  return Math.round(seconds / 60);
}

function parseDirectionSteps(legs: google.maps.DirectionsLeg[]): DirectionStep[] {
  const steps: DirectionStep[] = [];

  for (const leg of legs) {
    for (const step of leg.steps) {
      steps.push({
        instruction: step.instructions.replace(/<[^>]*>/g, ''),
        distance: step.distance?.text || '',
        duration: step.duration?.text || '',
        maneuver: step.maneuver || undefined,
        startLocation: {
          lat: step.start_location.lat(),
          lng: step.start_location.lng(),
        },
        endLocation: {
          lat: step.end_location.lat(),
          lng: step.end_location.lng(),
        },
      });
    }
  }

  return steps;
}

function extractPath(route: google.maps.DirectionsRoute): LatLng[] {
  const path: LatLng[] = [];

  for (const leg of route.legs) {
    for (const step of leg.steps) {
      for (const point of step.path) {
        path.push({
          lat: point.lat(),
          lng: point.lng(),
        });
      }
    }
  }

  return path;
}

export function useDirections(): UseDirectionsReturn {
  const routesLib = useMapsLibrary('routes');
  const directionsServiceRef = useRef<google.maps.DirectionsService | null>(null);

  useEffect(() => {
    if (!routesLib) return;
    directionsServiceRef.current = new routesLib.DirectionsService();
  }, [routesLib]);

  const calculateRoute = useCallback(
    async (
      origin: LatLng,
      destination: LatLng,
      options: { provideAlternatives?: boolean } = {},
    ): Promise<DirectionsResult | null> => {
      const { provideAlternatives = true } = options;

      if (!directionsServiceRef.current) {
        return null;
      }

      try {
        const result = await directionsServiceRef.current.route({
          origin: { lat: origin.lat, lng: origin.lng },
          destination: { lat: destination.lat, lng: destination.lng },
          travelMode: google.maps.TravelMode.DRIVING,
          provideRouteAlternatives: provideAlternatives,
        });

        if (result.routes.length === 0) {
          return null;
        }

        const routes: RouteInfo[] = result.routes.map((route, index) => {
          const leg = route.legs[0];
          const distanceMeters = leg.distance?.value || 0;
          const durationSeconds = leg.duration?.value || 0;

          const isSafest = index === 0;

          return {
            id: generateRouteId(),
            name: isSafest ? 'Safest Route' : 'Fastest Route',
            from: leg.start_address || 'Origin',
            to: leg.end_address || 'Destination',
            fromPosition: origin,
            toPosition: destination,
            distanceMiles: metersToMiles(distanceMeters),
            etaMinutes: secondsToMinutes(durationSeconds),
            safetyPercent: 0,
            type: isSafest ? 'safest' : 'fastest',
            hazards: [],
            description: route.summary || '',
            path: extractPath(route),
            steps: parseDirectionSteps(route.legs),
          };
        });

        return {
          routes,
          rawResponse: result,
        };
      } catch (error) {
        console.error('Failed to calculate route:', error);
        return null;
      }
    },
    [],
  );

  return {
    calculateRoute,
    isReady: !!routesLib,
  };
}
