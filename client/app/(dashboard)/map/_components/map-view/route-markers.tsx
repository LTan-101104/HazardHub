'use client';

import { useEffect } from 'react';
import { useMap } from '@vis.gl/react-google-maps';
import type { LatLng } from '@/types/map';

interface RouteMarkersProps {
  origin?: LatLng | null;
  destination?: LatLng | null;
}

export function RouteMarkers({ origin, destination }: RouteMarkersProps) {
  const map = useMap();

  useEffect(() => {
    if (!map) return;

    const markers: google.maps.Marker[] = [];

    if (origin) {
      const originMarker = new google.maps.Marker({
        position: { lat: origin.lat, lng: origin.lng },
        map,
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: 8,
          fillColor: '#FF8400',
          fillOpacity: 1,
          strokeColor: '#FFFFFF',
          strokeWeight: 2,
        },
        title: 'Origin',
        zIndex: 10,
      });
      markers.push(originMarker);
    }

    if (destination) {
      const destinationMarker = new google.maps.Marker({
        position: { lat: destination.lat, lng: destination.lng },
        map,
        icon: {
          path: google.maps.SymbolPath.CIRCLE,
          scale: 8,
          fillColor: '#22C55E',
          fillOpacity: 1,
          strokeColor: '#FFFFFF',
          strokeWeight: 2,
        },
        title: 'Destination',
        zIndex: 10,
      });
      markers.push(destinationMarker);
    }

    return () => {
      markers.forEach((marker) => marker.setMap(null));
    };
  }, [map, origin, destination]);

  useEffect(() => {
    if (!map || (!origin && !destination)) return;

    if (origin && destination) {
      const bounds = new google.maps.LatLngBounds();
      bounds.extend({ lat: origin.lat, lng: origin.lng });
      bounds.extend({ lat: destination.lat, lng: destination.lng });
      map.fitBounds(bounds, { top: 100, right: 420, bottom: 100, left: 50 });
    } else if (destination) {
      map.panTo({ lat: destination.lat, lng: destination.lng });
      map.setZoom(15);
    } else if (origin) {
      map.panTo({ lat: origin.lat, lng: origin.lng });
      map.setZoom(15);
    }
  }, [map, origin, destination]);

  return null;
}
