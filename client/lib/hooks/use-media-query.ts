'use client';

import { useEffect, useState } from 'react';

export function useMediaQuery(query: string): boolean {
  const [matches, setMatches] = useState<boolean | undefined>(undefined);

  useEffect(() => {
    const media = window.matchMedia(query);
    setMatches(media.matches);
    const listener = (e: MediaQueryListEvent) => setMatches(e.matches);
    media.addEventListener('change', listener);
    return () => media.removeEventListener('change', listener);
  }, [query]);

  // Return false during SSR and initial hydration to prevent mismatch
  return matches ?? false;
}

export function useIsDesktop(): boolean {
  return useMediaQuery('(min-width: 1024px)');
}
