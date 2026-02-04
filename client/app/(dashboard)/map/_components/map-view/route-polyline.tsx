'use client';

import { useEffect, useMemo } from 'react';
import { useMap } from '@vis.gl/react-google-maps';
import type { LatLng } from '@/types/map';

interface RoutePolylineProps {
  path: LatLng[];
  isActive?: boolean;
  strokeColor?: string;
  strokeWeight?: number;
  strokeOpacity?: number;
}

export function RoutePolyline({
  path,
  isActive = true,
  strokeColor,
  strokeWeight,
  strokeOpacity,
}: RoutePolylineProps) {
  const map = useMap();

  const polylineOptions = useMemo(() => {
    const defaultActiveColor = '#4285F4';
    const defaultInactiveColor = '#9CA3AF';

    return {
      strokeColor: strokeColor || (isActive ? defaultActiveColor : defaultInactiveColor),
      strokeWeight: strokeWeight || (isActive ? 5 : 4),
      strokeOpacity: strokeOpacity || (isActive ? 1 : 0.5),
      geodesic: true,
      zIndex: isActive ? 2 : 1,
    };
  }, [isActive, strokeColor, strokeWeight, strokeOpacity]);

  useEffect(() => {
    if (!map || path.length === 0) return;

    const googlePath = path.map((point) => new google.maps.LatLng(point.lat, point.lng));

    const polyline = new google.maps.Polyline({
      path: googlePath,
      ...polylineOptions,
      map,
    });

    return () => {
      polyline.setMap(null);
    };
  }, [map, path, polylineOptions]);

  return null;
}

interface RoutePolylinesProps {
  activePath?: LatLng[];
  alternatePath?: LatLng[];
}

export function RoutePolylines({ activePath, alternatePath }: RoutePolylinesProps) {
  return (
    <>
      {alternatePath && alternatePath.length > 0 && (
        <RoutePolyline path={alternatePath} isActive={false} />
      )}
      {activePath && activePath.length > 0 && <RoutePolyline path={activePath} isActive={true} />}
    </>
  );
}
