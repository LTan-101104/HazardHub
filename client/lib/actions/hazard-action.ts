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

export async function createHazardVerification(
  idToken: string,
  data: { hazardId: string; userId: string; verificationType: 'CONFIRM' | 'DISPUTE'; comment?: string },
): Promise<void> {
  await api.post('/api/v1/hazard-verifications', data, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
}

/**
 * Get the current user's verification for a specific hazard.
 * Returns the verification DTO if found, or null if the user hasn't verified yet (404).
 */
export async function getUserHazardVerification(
  idToken: string,
  hazardId: string,
  userId: string,
): Promise<{ id: string; verificationType: 'CONFIRM' | 'DISPUTE' } | null> {
  try {
    const response = await api.get(`/api/v1/hazard-verifications/hazard/${hazardId}/user/${userId}`, {
      headers: { Authorization: `Bearer ${idToken}` },
    });
    return response.data;
  } catch (err: unknown) {
    if (axios.isAxiosError(err) && err.response?.status === 404) {
      return null;
    }
    throw err;
  }
}

/**
 * Delete a hazard verification by its ID.
 */
export async function deleteHazardVerification(idToken: string, verificationId: string): Promise<void> {
  await api.delete(`/api/v1/hazard-verifications/${verificationId}`, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
}

/**
 * Analyze a hazard image using Gemini Vision AI.
 * Returns an AI-generated description of the hazard.
 */
export async function analyzeHazardImage(
  idToken: string,
  imageUrl: string,
): Promise<string> {
  const response = await api.post<{ description: string }>(
    '/api/v1/ai/analyze-hazard-image',
    { imageUrl },
    { headers: { Authorization: `Bearer ${idToken}` } },
  );
  return response.data.description;
}
