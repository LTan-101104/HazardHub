import axios from 'axios';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export type ChatVehicleType = 'CAR' | 'BICYCLE' | 'WALKING';

export interface ChatRequestPayload {
  message: string;
  originLongitude?: number;
  originLatitude?: number;
  originAddress?: string;
  destinationLongitude?: number;
  destinationLatitude?: number;
  destinationAddress?: string;
  vehicleType?: ChatVehicleType;
}

export interface ChatRouteOption {
  name?: string;
  recommendationTier?: 'RECOMMENDED' | 'ALTERNATIVE' | 'RISKY' | string;
  safetyScore?: number;
  hazardCount?: number;
  summary?: string;
  distanceMeters?: number;
  durationSeconds?: number;
  polyline?: string;
}

export interface ChatResponsePayload {
  reply: string;
  routeOptions: ChatRouteOption[];
}

export async function sendChatMessage(idToken: string, payload: ChatRequestPayload): Promise<ChatResponsePayload> {
  const response = await api.post<ChatResponsePayload>('/api/v1/ai/chat', payload, {
    headers: {
      Authorization: `Bearer ${idToken}`,
    },
  });

  return response.data;
}
