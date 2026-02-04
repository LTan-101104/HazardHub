'use client';

import { createContext, useContext, useReducer, type Dispatch, type ReactNode } from 'react';
import type { MapContextState, MapAction } from '@/types/map';
import { MOCK_WEATHER } from '@/lib/constants/map-config';

const initialState: MapContextState = {
  viewState: 'browse',
  selectedHazard: null,
  activeRoute: null,
  alternateRoute: null,
  weather: MOCK_WEATHER,
  chatMessages: [],
  currentInstruction: null,
  fromLocation: '',
  toLocation: '',
  fromPosition: null,
  toPosition: null,
  isLoadingRoute: false,
  isChatOpen: false,
  isHazardDetailOpen: false,
  isHazardAlertVisible: false,
  isDrawerOpen: false,
  navigationEta: '',
  navigationDistance: '',
  navigationArrival: '',
  error: null,
};

function mapReducer(state: MapContextState, action: MapAction): MapContextState {
  switch (action.type) {
    case 'SET_VIEW_STATE':
      return { ...state, viewState: action.payload };
    case 'SELECT_HAZARD':
      return {
        ...state,
        selectedHazard: action.payload,
        isHazardDetailOpen: !!action.payload,
      };
    case 'SET_ROUTE':
      return {
        ...state,
        activeRoute: action.payload.active,
        alternateRoute: action.payload.alternate ?? null,
        viewState: 'routing',
      };
    case 'CLEAR_ROUTE':
      return {
        ...state,
        activeRoute: null,
        alternateRoute: null,
        viewState: 'browse',
      };
    case 'SET_LOCATIONS':
      return {
        ...state,
        fromLocation: action.payload.from,
        toLocation: action.payload.to,
      };
    case 'SET_FROM_LOCATION':
      return {
        ...state,
        fromLocation: action.payload.text,
        fromPosition: action.payload.position,
      };
    case 'SET_TO_LOCATION':
      return {
        ...state,
        toLocation: action.payload.text,
        toPosition: action.payload.position,
        viewState: action.payload.position ? 'routing' : state.viewState,
      };
    case 'SET_LOADING_ROUTE':
      return {
        ...state,
        isLoadingRoute: action.payload,
      };
    case 'START_NAVIGATION':
      return {
        ...state,
        viewState: 'navigating',
        isChatOpen: false,
        isHazardDetailOpen: false,
      };
    case 'END_NAVIGATION':
      return {
        ...state,
        viewState: 'routing',
        currentInstruction: null,
        isHazardAlertVisible: false,
      };
    case 'TOGGLE_CHAT':
      return {
        ...state,
        isChatOpen: action.payload,
        viewState: action.payload ? 'chat' : state.activeRoute ? 'routing' : 'browse',
      };
    case 'ADD_CHAT_MESSAGE':
      return {
        ...state,
        chatMessages: [...state.chatMessages, action.payload],
      };
    case 'SET_HAZARD_ALERT':
      return { ...state, isHazardAlertVisible: action.payload };
    case 'SHOW_HAZARD_DETAIL':
      return { ...state, isHazardDetailOpen: action.payload };
    case 'TOGGLE_DRAWER':
      return { ...state, isDrawerOpen: action.payload };
    case 'SET_ERROR':
      return { ...state, error: action.payload };
    default:
      return state;
  }
}

const MapContext = createContext<{ state: MapContextState; dispatch: Dispatch<MapAction> } | undefined>(undefined);

export function MapProvider({ children }: { children: ReactNode }) {
  const [state, dispatch] = useReducer(mapReducer, initialState);
  return <MapContext.Provider value={{ state, dispatch }}>{children}</MapContext.Provider>;
}

export function useMap() {
  const context = useContext(MapContext);
  if (!context) throw new Error('useMap must be used within MapProvider');
  return context;
}
