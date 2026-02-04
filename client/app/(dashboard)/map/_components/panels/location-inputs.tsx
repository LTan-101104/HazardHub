'use client';

import { useState, useCallback, useEffect } from 'react';
import { ArrowUpDown } from 'lucide-react';
import { useMap } from '../map-provider';
import { AutocompleteInput } from '../ui/autocomplete-input';
import type { LatLng } from '@/types/map';

export function LocationInputs() {
  const { state, dispatch } = useMap();
  const [fromInputValue, setFromInputValue] = useState(state.fromLocation);
  const [toInputValue, setToInputValue] = useState(state.toLocation);

  useEffect(() => {
    setFromInputValue(state.fromLocation);
  }, [state.fromLocation]);

  useEffect(() => {
    setToInputValue(state.toLocation);
  }, [state.toLocation]);

  const handleFromPlaceSelect = useCallback(
    (place: { text: string; position: LatLng }) => {
      dispatch({
        type: 'SET_FROM_LOCATION',
        payload: { text: place.text, position: place.position },
      });
      setFromInputValue(place.text);
    },
    [dispatch]
  );

  const handleToPlaceSelect = useCallback(
    (place: { text: string; position: LatLng }) => {
      dispatch({
        type: 'SET_TO_LOCATION',
        payload: { text: place.text, position: place.position },
      });
      setToInputValue(place.text);
    },
    [dispatch]
  );

  const handleCurrentLocationClick = useCallback(() => {
    if (navigator.geolocation) {
      navigator.geolocation.getCurrentPosition(
        (pos) => {
          dispatch({
            type: 'SET_FROM_LOCATION',
            payload: {
              text: 'Current Location',
              position: { lat: pos.coords.latitude, lng: pos.coords.longitude },
            },
          });
          setFromInputValue('Current Location');
        },
        () => {
          dispatch({
            type: 'SET_FROM_LOCATION',
            payload: { text: 'Current Location', position: null },
          });
          setFromInputValue('Current Location');
        }
      );
    }
  }, [dispatch]);

  const handleSwapLocations = useCallback(() => {
    const tempFromText = state.fromLocation;
    const tempFromPosition = state.fromPosition;

    dispatch({
      type: 'SET_FROM_LOCATION',
      payload: { text: state.toLocation, position: state.toPosition },
    });
    dispatch({
      type: 'SET_TO_LOCATION',
      payload: { text: tempFromText, position: tempFromPosition },
    });

    setFromInputValue(state.toLocation);
    setToInputValue(tempFromText);
  }, [state, dispatch]);

  return (
    <div className="flex gap-2.5">
      <div className="flex flex-col gap-2.5">
        <div className="flex size-10 items-center justify-center">
          <div className="size-2.5 rounded-full bg-[#FF8400]" />
        </div>
        <button
          onClick={handleSwapLocations}
          className="flex size-10 items-center justify-center rounded-lg bg-[#2E2E2E] transition-colors hover:bg-[#3E3E3E]"
          title="Swap locations"
        >
          <ArrowUpDown className="size-4 text-[#B8B9B6]" />
        </button>
        <div className="flex size-10 items-center justify-center">
          <div className="size-2.5 rounded-full bg-[#22C55E]" />
        </div>
      </div>

      <div className="flex flex-1 flex-col gap-2.5">
        <AutocompleteInput
          value={fromInputValue}
          placeholder="Choose starting point"
          onChange={setFromInputValue}
          onPlaceSelect={handleFromPlaceSelect}
          showCurrentLocation={true}
          onCurrentLocationClick={handleCurrentLocationClick}
        />

        <div className="h-10" />

        <AutocompleteInput
          value={toInputValue}
          placeholder="Choose destination"
          onChange={setToInputValue}
          onPlaceSelect={handleToPlaceSelect}
        />
      </div>
    </div>
  );
}
