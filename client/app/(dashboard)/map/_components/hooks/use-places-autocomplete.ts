'use client';

import { useCallback, useState, useRef, useEffect } from 'react';
import { useMapsLibrary } from '@vis.gl/react-google-maps';
import type { PlaceResult, LatLng } from '@/types/map';

interface UsePlacesAutocompleteOptions {
  debounceMs?: number;
  minChars?: number;
}

interface UsePlacesAutocompleteReturn {
  predictions: PlaceResult[];
  isLoading: boolean;
  error: string | null;
  isReady: boolean;
  search: (input: string) => void;
  getPlaceDetails: (placeId: string) => Promise<LatLng | null>;
  clearPredictions: () => void;
}

export function usePlacesAutocomplete(
  options: UsePlacesAutocompleteOptions = {}
): UsePlacesAutocompleteReturn {
  const { debounceMs = 300, minChars = 2 } = options;

  const placesLib = useMapsLibrary('places');
  const geocodingLib = useMapsLibrary('geocoding');

  const [predictions, setPredictions] = useState<PlaceResult[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const autocompleteServiceRef = useRef<google.maps.places.AutocompleteService | null>(null);
  const geocoderRef = useRef<google.maps.Geocoder | null>(null);
  const debounceTimeoutRef = useRef<NodeJS.Timeout | null>(null);

  const isReady = !!placesLib && !!geocodingLib;

  useEffect(() => {
    if (placesLib) {
      autocompleteServiceRef.current = new placesLib.AutocompleteService();
    }
  }, [placesLib]);

  useEffect(() => {
    if (geocodingLib) {
      geocoderRef.current = new geocodingLib.Geocoder();
    }
  }, [geocodingLib]);

  const search = useCallback(
    (input: string) => {
      if (debounceTimeoutRef.current) {
        clearTimeout(debounceTimeoutRef.current);
      }

      if (!input || input.length < minChars) {
        setPredictions([]);
        setIsLoading(false);
        return;
      }

      if (!autocompleteServiceRef.current) {
        return;
      }

      setIsLoading(true);
      setError(null);

      debounceTimeoutRef.current = setTimeout(() => {
        if (!autocompleteServiceRef.current) {
          setError('Places service not available');
          setIsLoading(false);
          return;
        }

        autocompleteServiceRef.current.getPlacePredictions(
          {
            input,
            types: ['geocode', 'establishment'],
          },
          (results, status) => {
            setIsLoading(false);

            if (status === google.maps.places.PlacesServiceStatus.OK && results) {
              const formattedPredictions: PlaceResult[] = results.map((prediction) => ({
                placeId: prediction.place_id,
                description: prediction.description,
                mainText: prediction.structured_formatting.main_text,
                secondaryText: prediction.structured_formatting.secondary_text || '',
                position: { lat: 0, lng: 0 },
              }));
              setPredictions(formattedPredictions);
            } else if (status === google.maps.places.PlacesServiceStatus.ZERO_RESULTS) {
              setPredictions([]);
            } else {
              setError('Failed to fetch predictions');
              setPredictions([]);
            }
          }
        );
      }, debounceMs);
    },
    [debounceMs, minChars]
  );

  const getPlaceDetails = useCallback(async (placeId: string): Promise<LatLng | null> => {
    if (!geocoderRef.current) {
      return null;
    }

    return new Promise((resolve) => {
      geocoderRef.current!.geocode({ placeId }, (results, status) => {
        if (status === google.maps.GeocoderStatus.OK && results && results[0]) {
          const location = results[0].geometry.location;
          resolve({
            lat: location.lat(),
            lng: location.lng(),
          });
        } else {
          resolve(null);
        }
      });
    });
  }, []);

  const clearPredictions = useCallback(() => {
    setPredictions([]);
    setError(null);
  }, []);

  useEffect(() => {
    return () => {
      if (debounceTimeoutRef.current) {
        clearTimeout(debounceTimeoutRef.current);
      }
    };
  }, []);

  return {
    predictions,
    isLoading,
    error,
    isReady,
    search,
    getPlaceDetails,
    clearPredictions,
  };
}
