'use client';

import { useState, useRef, useEffect, useCallback, type KeyboardEvent } from 'react';
import { MapPin, Loader2, X } from 'lucide-react';
import { usePlacesAutocomplete } from '../hooks/use-places-autocomplete';
import type { PlaceResult, LatLng } from '@/types/map';

interface AutocompleteInputProps {
  value: string;
  placeholder?: string;
  onChange: (value: string) => void;
  onPlaceSelect: (place: { text: string; position: LatLng }) => void;
  showCurrentLocation?: boolean;
  onCurrentLocationClick?: () => void;
  className?: string;
  inputClassName?: string;
  disabled?: boolean;
}

export function AutocompleteInput({
  value,
  placeholder = 'Search location...',
  onChange,
  onPlaceSelect,
  showCurrentLocation = false,
  onCurrentLocationClick,
  className = '',
  inputClassName = '',
  disabled = false,
}: AutocompleteInputProps) {
  const [isFocused, setIsFocused] = useState(false);
  const [highlightedIndex, setHighlightedIndex] = useState(-1);
  const inputRef = useRef<HTMLInputElement>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const { predictions, isLoading, search, getPlaceDetails, clearPredictions } = usePlacesAutocomplete();

  const showDropdown = isFocused && (predictions.length > 0 || showCurrentLocation);

  const handleValueChange = useCallback(
    (newValue: string) => {
      onChange(newValue);
      setHighlightedIndex(-1);
      search(newValue);
    },
    [onChange, search],
  );

  const handlePlaceSelect = useCallback(
    async (prediction: PlaceResult) => {
      const position = await getPlaceDetails(prediction.placeId);
      if (position) {
        onPlaceSelect({
          text: prediction.description,
          position,
        });
        clearPredictions();
        setIsFocused(false);
      }
    },
    [getPlaceDetails, onPlaceSelect, clearPredictions],
  );

  const handleCurrentLocation = useCallback(() => {
    if (onCurrentLocationClick) {
      onCurrentLocationClick();
      clearPredictions();
      setIsFocused(false);
    }
  }, [onCurrentLocationClick, clearPredictions]);

  const handleKeyDown = (e: KeyboardEvent<HTMLInputElement>) => {
    const totalItems = predictions.length + (showCurrentLocation ? 1 : 0);

    switch (e.key) {
      case 'ArrowDown':
        e.preventDefault();
        setHighlightedIndex((prev) => (prev < totalItems - 1 ? prev + 1 : 0));
        break;
      case 'ArrowUp':
        e.preventDefault();
        setHighlightedIndex((prev) => (prev > 0 ? prev - 1 : totalItems - 1));
        break;
      case 'Enter':
        e.preventDefault();
        if (highlightedIndex >= 0) {
          if (showCurrentLocation && highlightedIndex === 0) {
            handleCurrentLocation();
          } else {
            const predictionIndex = showCurrentLocation ? highlightedIndex - 1 : highlightedIndex;
            if (predictions[predictionIndex]) {
              handlePlaceSelect(predictions[predictionIndex]);
            }
          }
        }
        break;
      case 'Escape':
        setIsFocused(false);
        clearPredictions();
        break;
    }
  };

  const handleClear = () => {
    onChange('');
    clearPredictions();
    inputRef.current?.focus();
  };

  useEffect(() => {
    const handleClickOutside = (e: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target as Node) &&
        inputRef.current &&
        !inputRef.current.contains(e.target as Node)
      ) {
        setIsFocused(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  return (
    <div className={`relative ${className}`}>
      <div className="relative">
        <input
          ref={inputRef}
          type="text"
          value={value}
          onChange={(e) => handleValueChange(e.target.value)}
          onFocus={() => setIsFocused(true)}
          onKeyDown={handleKeyDown}
          placeholder={placeholder}
          disabled={disabled}
          className={`h-10 w-full rounded-lg bg-[#2E2E2E] px-3 pr-10 text-sm text-white placeholder:text-[#B8B9B6] focus:outline-none focus:ring-1 focus:ring-[#4285F4] disabled:cursor-not-allowed disabled:opacity-50 ${inputClassName}`}
        />
        <div className="absolute right-2 top-1/2 -translate-y-1/2">
          {isLoading ? (
            <Loader2 className="size-4 animate-spin text-[#B8B9B6]" />
          ) : value ? (
            <button type="button" onClick={handleClear} className="rounded p-0.5 hover:bg-[#3E3E3E]">
              <X className="size-4 text-[#B8B9B6]" />
            </button>
          ) : null}
        </div>
      </div>

      {showDropdown && (
        <div
          ref={dropdownRef}
          className="absolute left-0 right-0 top-full z-50 mt-1 max-h-64 overflow-y-auto rounded-lg border border-[#2E2E2E] bg-[#1A1A1A] shadow-lg"
        >
          {showCurrentLocation && (
            <button
              type="button"
              onClick={handleCurrentLocation}
              className={`flex w-full items-center gap-3 px-3 py-2.5 text-left transition-colors ${
                highlightedIndex === 0 ? 'bg-[#2E2E2E]' : 'hover:bg-[#2E2E2E]'
              }`}
            >
              <div className="flex size-8 shrink-0 items-center justify-center rounded-full bg-[#4285F4]/20">
                <MapPin className="size-4 text-[#4285F4]" />
              </div>
              <div>
                <p className="text-sm font-medium text-white">Current Location</p>
                <p className="text-xs text-[#B8B9B6]">Use your current location</p>
              </div>
            </button>
          )}

          {predictions.map((prediction, index) => {
            const itemIndex = showCurrentLocation ? index + 1 : index;
            return (
              <button
                key={prediction.placeId}
                type="button"
                onClick={() => handlePlaceSelect(prediction)}
                className={`flex w-full items-center gap-3 px-3 py-2.5 text-left transition-colors ${
                  highlightedIndex === itemIndex ? 'bg-[#2E2E2E]' : 'hover:bg-[#2E2E2E]'
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
            );
          })}

          {predictions.length === 0 && !showCurrentLocation && value.length >= 2 && !isLoading && (
            <div className="px-3 py-4 text-center text-sm text-[#B8B9B6]">No results found</div>
          )}
        </div>
      )}
    </div>
  );
}
