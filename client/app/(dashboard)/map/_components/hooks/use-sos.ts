'use client';

import { useState, useCallback } from 'react';
import { SOSEventDTO, SOSEventStatus } from '@/types';
import { createSOSEvent, updateSOSEvent } from '@/lib/actions/sos-action';

interface UseSOSOptions {
  onSOSCreated?: (sosEvent: SOSEventDTO) => void;
  onSOSResolved?: (sosEvent: SOSEventDTO) => void;
  onError?: (error: Error) => void;
}

interface UseSOSReturn {
  isSOSActive: boolean;
  sosEvent: SOSEventDTO | null;
  isLoading: boolean;
  error: string | null;
  triggerSOS: (
    idToken: string,
    latitude: number,
    longitude: number,
    userId: string,
    accuracy?: number,
  ) => Promise<SOSEventDTO | null>;
  resolveSOS: (idToken: string) => Promise<void>;
  clearError: () => void;
}

export function useSOS(options: UseSOSOptions = {}): UseSOSReturn {
  const [isSOSActive, setIsSOSActive] = useState(false);
  const [sosEvent, setSOSEvent] = useState<SOSEventDTO | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const { onSOSCreated, onSOSResolved, onError } = options;

  const triggerSOS = useCallback(
    async (
      idToken: string,
      latitude: number,
      longitude: number,
      userId: string,
      accuracy: number = 10,
    ): Promise<SOSEventDTO | null> => {
      setIsLoading(true);
      setError(null);

      try {
        const sosData: SOSEventDTO = {
          userId,
          latitude,
          longitude,
          locationAccuracyMeters: accuracy,
          status: SOSEventStatus.ACTIVE,
        };

        const createdEvent = await createSOSEvent(idToken, sosData);
        setSOSEvent(createdEvent);
        setIsSOSActive(true);
        onSOSCreated?.(createdEvent);
        return createdEvent;
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to trigger SOS';
        setError(errorMessage);
        onError?.(err instanceof Error ? err : new Error(errorMessage));
        return null;
      } finally {
        setIsLoading(false);
      }
    },
    [onSOSCreated, onError],
  );

  const resolveSOS = useCallback(
    async (idToken: string): Promise<void> => {
      if (!sosEvent?.id) {
        setError('No active SOS event to resolve');
        return;
      }

      setIsLoading(true);
      setError(null);

      try {
        const updatedEvent = await updateSOSEvent(idToken, sosEvent.id, {
          ...sosEvent,
          status: SOSEventStatus.RESOLVED,
          resolvedAt: new Date().toISOString(),
        });
        setSOSEvent(updatedEvent);
        setIsSOSActive(false);
        onSOSResolved?.(updatedEvent);
      } catch (err) {
        const errorMessage = err instanceof Error ? err.message : 'Failed to resolve SOS';
        setError(errorMessage);
        onError?.(err instanceof Error ? err : new Error(errorMessage));
      } finally {
        setIsLoading(false);
      }
    },
    [sosEvent, onSOSResolved, onError],
  );

  const clearError = useCallback(() => {
    setError(null);
  }, []);

  return {
    isSOSActive,
    sosEvent,
    isLoading,
    error,
    triggerSOS,
    resolveSOS,
    clearError,
  };
}
