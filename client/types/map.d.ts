export interface LatLng {
  lat: number;
  lng: number;
}

export interface SOSPin extends LatLng {
  eventId?: string;
}

export interface PlaceResult {
  placeId: string;
  description: string;
  mainText: string;
  secondaryText: string;
  position: LatLng;
}

export interface DirectionStep {
  instruction: string;
  distance: string;
  duration: string;
  maneuver?: string;
  startLocation: LatLng;
  endLocation: LatLng;
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
  imageUrl?: string;
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
  path?: LatLng[];
  steps?: DirectionStep[];
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
  routeCards?: RouteCardData[];
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
  fromPosition: LatLng | null;
  toPosition: LatLng | null;
  isLoadingRoute: boolean;
  isChatOpen: boolean;
  isHazardDetailOpen: boolean;
  isHazardAlertVisible: boolean;
  isDrawerOpen: boolean;
  navigationEta: string;
  navigationDistance: string;
  navigationArrival: string;
  error: string | null;
  // SOS state
  isSOSPopupOpen: boolean;
  sosLocations: SOSPin[];
  selectedSOSIndex: number | null;
  isSOSPinMode: boolean;
}

export type MapAction =
  | { type: 'SET_VIEW_STATE'; payload: MapViewState }
  | { type: 'SELECT_HAZARD'; payload: HazardMarker | null }
  | { type: 'SET_ROUTE'; payload: { active: RouteInfo; alternate?: RouteInfo } }
  | { type: 'CLEAR_ROUTE' }
  | { type: 'SET_LOCATIONS'; payload: { from: string; to: string } }
  | { type: 'SET_FROM_LOCATION'; payload: { text: string; position: LatLng | null } }
  | { type: 'SET_TO_LOCATION'; payload: { text: string; position: LatLng | null } }
  | { type: 'SET_LOADING_ROUTE'; payload: boolean }
  | { type: 'START_NAVIGATION' }
  | { type: 'END_NAVIGATION' }
  | { type: 'TOGGLE_CHAT'; payload: boolean }
  | { type: 'ADD_CHAT_MESSAGE'; payload: ChatMessage }
  | { type: 'SET_HAZARD_ALERT'; payload: boolean }
  | { type: 'SHOW_HAZARD_DETAIL'; payload: boolean }
  | { type: 'TOGGLE_DRAWER'; payload: boolean }
  | { type: 'SET_ERROR'; payload: string | null }
  | { type: 'ADD_SOS_PIN'; payload: SOSPin }
  | { type: 'SET_SOS_PINS'; payload: SOSPin[] }
  | { type: 'SET_SOS_PIN_EVENT_ID'; payload: { index: number; eventId: string } }
  | { type: 'SELECT_SOS_PIN'; payload: number }
  | { type: 'REMOVE_SOS_PIN'; payload: number }
  | { type: 'CLEAR_ALL_SOS_PINS' }
  | { type: 'CLOSE_SOS_POPUP' }
  | { type: 'TOGGLE_SOS_PIN_MODE'; payload: boolean };
