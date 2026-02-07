import axios from 'axios';
import { HazardDTO } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

export async function createHazardReport(idToken: string, data: Omit<HazardDTO, 'id'>): Promise<HazardDTO> {
  const response = await api.post('/api/v1/hazards', data, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

export async function getHazardById(idToken: string, id: string): Promise<HazardDTO> {
  const response = await api.get(`/api/v1/hazards/${id}`, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

export async function getNearbyHazards(
  idToken: string,
  longitude: number,
  latitude: number,
  maxDistanceMeters: number,
): Promise<HazardDTO[]> {
  const response = await api.get('/api/v1/hazards/nearby/active', {
    params: { longitude, latitude, maxDistanceMeters },
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}
