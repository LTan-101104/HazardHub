'use client';

import { useState, useRef, useEffect, useCallback, type KeyboardEvent } from 'react';
import { Search, Mic, MapPin, Loader2, X } from 'lucide-react';
import { usePlacesAutocomplete } from '../hooks/use-places-autocomplete';
import { useMap } from '../map-provider';
import type { PlaceResult } from '@/types/map';

export function SearchBar() {
  const { dispatch } = useMap();
  const [isExpanded, setIsExpanded] = useState(false);
  const [inputValue, setInputValue] = useState('');
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const inputRef = useRef<HTMLInputElement>(null);
  const containerRef = useRef<HTMLDivElement>(null);

  const { predictions, isLoading, search, getPlaceDetails, clearPredictions } =
    usePlacesAutocomplete();

  const showDropdown = isExpanded && predictions.length > 0;

  const handleInputChange = useCallback(
    (value: string) => {
      setInputValue(value);
      setHighlightedIndex(-1);
      search(value);
    },
    [search]
  );

  const handlePlaceSelect = useCallback(
    async (prediction: PlaceResult) => {
      const position = await getPlaceDetails(prediction.placeId);
      if (position) {
        dispatch({
          type: 'SET_TO_LOCATION',
          payload: { text: prediction.description, position },
        });

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
            },
            () => {
              dispatch({
                type: 'SET_FROM_LOCATION',
                payload: { text: 'Current Location', position: null },
              });
            }
          );
        }

        setInputValue('');
        clearPredictions();
        setIsExpanded(false);
      }
    },
    [getPlaceDetails, dispatch, clearPredictions]
  );

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setHighlightedIndex((prev) => (prev < predictions.length - 1 ? prev + 1 : 0));
        break;
      case 'ArrowUp':
        e.preventDefault();
        setHighlightedIndex((prev) => (prev > 0 ? prev - 1 : predictions.length - 1));
        break;
      case 'Enter':
        e.preventDefault();
        if (highlightedIndex >= 0 && predictions[highlightedIndex]) {
          handlePlaceSelect(predictions[highlightedIndex]);
        }
        break;
      case 'Escape':
        setIsExpanded(false);
        clearPredictions();
        setInputValue('');
        break;
    }
  };

  const handleExpand = () => {
    setIsExpanded(true);
    setTimeout(() => inputRef.current?.focus(), 100);
  };

  const handleClear = () => {
    setInputValue('');
    clearPredictions();
    inputRef.current?.focus();
  };

  const handleClose = () => {
    setIsExpanded(false);
    setInputValue('');
    clearPredictions();
  };

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (containerRef.current && !containerRef.current.contains(e.target as Node)) {
        setIsExpanded(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  if (!isExpanded) {
    return (
      <button
        onClick={handleExpand}
        className="flex h-12 items-center gap-2.5 rounded-3xl border border-[#2E2E2E] bg-[#1A1A1A] px-4 shadow-[0_4px_16px_rgba(0,0,0,0.2)] transition-all hover:border-[#3E3E3E]"
      >
        <Search className="size-5 shrink-0 text-[#B8B9B6]" />
        <span className="text-sm text-[#B8B9B6]">Search destination...</span>
        <Mic className="size-5 shrink-0 text-[#FF8400]" />
      </button>
    );
  }

  return (
    <div ref={containerRef} className="relative w-80">
      <div className="flex h-12 items-center gap-2.5 rounded-3xl border border-[#4285F4] bg-[#1A1A1A] px-4 shadow-[0_4px_16px_rgba(0,0,0,0.2)]">
        <Search className="size-5 shrink-0 text-[#4285F4]" />
        <input
          ref={inputRef}
          type="text"
          value={inputValue}
          onChange={(e) => handleInputChange(e.target.value)}
          onKeyDown={handleKeyDown}
          placeholder="Search destination..."
          className="flex-1 bg-transparent text-sm text-white placeholder:text-[#B8B9B6] focus:outline-none"
        />
        {isLoading ? (
          <Loader2 className="size-5 shrink-0 animate-spin text-[#B8B9B6]" />
        ) : inputValue ? (
          <button onClick={handleClear} className="rounded p-0.5 hover:bg-[#2E2E2E]">
            <X className="size-5 text-[#B8B9B6]" />
          </button>
        ) : (
          <button onClick={handleClose} className="rounded p-0.5 hover:bg-[#2E2E2E]">
            <X className="size-5 text-[#B8B9B6]" />
          </button>
        )}
      </div>

      {showDropdown && (
        <div className="absolute left-0 right-0 top-full z-50 mt-2 max-h-64 overflow-y-auto rounded-2xl border border-[#2E2E2E] bg-[#1A1A1A] shadow-lg">
          {predictions.map((prediction, index) => (
            <button
              key={prediction.placeId}
              onClick={() => handlePlaceSelect(prediction)}
              className={`flex w-full items-center gap-3 px-4 py-3 text-left transition-colors ${
                highlightedIndex === index ? 'bg-[#2E2E2E]' : 'hover:bg-[#2E2E2E]'
              } ${index === 0 ? 'rounded-t-2xl' : ''} ${
                index === predictions.length - 1 ? 'rounded-b-2xl' : ''
              }`}
            >
              <div className="flex size-8 shrink-0 items-center justify-center rounded-full bg-[#3E3E3E]">
                <MapPin className="size-4 text-[#B8B9B6]" />
              </div>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-medium text-white">{prediction.mainText}</p>
                <p className="truncate text-xs text-[#B8B9B6]">{prediction.secondaryText}</p>
              </div>
            </button>
          ))}
        </div>
      )}
    </div>
  );
}
