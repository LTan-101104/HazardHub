'use client';

import { createContext, useContext, useReducer, type Dispatch, type ReactNode } from 'react';
import { HazardSeverity } from '@/types';

export interface ReportHazardState {
  // Step 1 — location
  latitude: number | null;
  longitude: number | null;
  address: string;
  locationAccuracyMeters: number | null;

  // Step 2 — details
  severity: HazardSeverity;
  description: string;
  imageUrl: string;
}

export type ReportHazardAction =
  | { type: 'SET_LOCATION'; payload: { latitude: number; longitude: number; address?: string; accuracy?: number } }
  | { type: 'SET_SEVERITY'; payload: HazardSeverity }
  | { type: 'SET_DESCRIPTION'; payload: string }
  | { type: 'SET_IMAGE_URL'; payload: string }
  | { type: 'RESET' };

const initialState: ReportHazardState = {
  latitude: null,
  longitude: null,
  address: '',
  locationAccuracyMeters: null,
  severity: HazardSeverity.MEDIUM,
  description: '',
  imageUrl: '',
};

function reportHazardReducer(state: ReportHazardState, action: ReportHazardAction): ReportHazardState {
  switch (action.type) {
    case 'SET_LOCATION':
      return {
        ...state,
        latitude: action.payload.latitude,
        longitude: action.payload.longitude,
        address: action.payload.address ?? state.address,
        locationAccuracyMeters: action.payload.accuracy ?? state.locationAccuracyMeters,
      };
    case 'SET_SEVERITY':
      return { ...state, severity: action.payload };
    case 'SET_DESCRIPTION':
      return { ...state, description: action.payload };
    case 'SET_IMAGE_URL':
      return { ...state, imageUrl: action.payload };
    case 'RESET':
      return initialState;
    default:
      return state;
  }
}

const ReportHazardContext = createContext<{ state: ReportHazardState; dispatch: Dispatch<ReportHazardAction> } | undefined>(
  undefined,
);

export function ReportHazardProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(reportHazardReducer, initialState);
  return <ReportHazardContext.Provider value={{ state, dispatch }}>{children}</ReportHazardContext.Provider>;
}

export function useReportHazard() {
  const context = useContext(ReportHazardContext);
  if (!context) throw new Error('useReportHazard must be used within ReportHazardProvider');
  return context;
}
