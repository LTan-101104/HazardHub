export interface LatLng {
  lat: number;
  lng: number;
}

export type HazardType = 'snow' | 'ice' | 'flood' | 'construction' | 'accident' | 'tree' | 'power_line' | 'other';
export type HazardSeverity = 'critical' | 'high' | 'medium' | 'low';

export interface HazardMarker {
  id: string;
  position: LatLng;
  type: HazardType;
  severity: HazardSeverity;
  title: string;
  description: string;
  reportedAt: string;
  reportCount: number;
}

export interface RouteInfo {
  id: string;
  name: string;
  from: string;
  to: string;
  fromPosition: LatLng;
  toPosition: LatLng;
  distanceMiles: number;
  etaMinutes: number;
  safetyPercent: number;
  type: 'safest' | 'fastest';
  hazards: HazardMarker[];
  description: string;
}

export interface WeatherInfo {
  tempF: number;
  condition: string;
  warning?: string;
  icon: string;
}

export interface ChatMessage {
  id: string;
  role: 'user' | 'ai';
  content: string;
  timestamp: string;
  routeCard?: RouteCardData;
}

export interface RouteCardData {
  name: string;
  distanceMiles: number;
  etaMinutes: number;
  safetyBadge: 'safe' | 'caution' | 'danger';
  terrain: string;
  tags: string[];
}

export interface TurnInstruction {
  direction: 'left' | 'right' | 'straight' | 'u-turn' | 'arrive';
  distanceMiles: number;
  streetName: string;
}

export type MapViewState = 'browse' | 'routing' | 'navigating' | 'chat';

export interface MapContextState {
  viewState: MapViewState;
  selectedHazard: HazardMarker | null;
  activeRoute: RouteInfo | null;
  alternateRoute: RouteInfo | null;
  weather: WeatherInfo;
  chatMessages: ChatMessage[];
  currentInstruction: TurnInstruction | null;
  fromLocation: string;
  toLocation: string;
  isChatOpen: boolean;
  isHazardDetailOpen: boolean;
  isHazardAlertVisible: boolean;
  isDrawerOpen: boolean;
  navigationEta: string;
  navigationDistance: string;
  navigationArrival: string;
}

export type MapAction =
  | { type: 'SET_VIEW_STATE'; payload: MapViewState }
  | { type: 'SELECT_HAZARD'; payload: HazardMarker | null }
  | { type: 'SET_ROUTE'; payload: { active: RouteInfo; alternate?: RouteInfo } }
  | { type: 'CLEAR_ROUTE' }
  | { type: 'SET_LOCATIONS'; payload: { from: string; to: string } }
  | { type: 'START_NAVIGATION' }
  | { type: 'END_NAVIGATION' }
  | { type: 'TOGGLE_CHAT'; payload: boolean }
  | { type: 'ADD_CHAT_MESSAGE'; payload: ChatMessage }
  | { type: 'SET_HAZARD_ALERT'; payload: boolean }
  | { type: 'SHOW_HAZARD_DETAIL'; payload: boolean }
  | { type: 'TOGGLE_DRAWER'; payload: boolean };
