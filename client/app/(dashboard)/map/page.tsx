import { Suspense } from 'react';
import { MapClient } from './_components/map-client';

export default function MapPage() {
  return (
    <Suspense>
      <MapClient />
    </Suspense>
  );
}
