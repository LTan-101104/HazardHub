import axios from 'axios';
import { SavedLocationDTO } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Create a new saved location for a user
 */
export async function createSavedLocation(idToken: string, data: Omit<SavedLocationDTO, 'id'>): Promise<SavedLocationDTO> {
  const response = await api.post('/api/v1/saved-locations', data, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Get a saved location by ID
 */
export async function getSavedLocationById(idToken: string, id: string): Promise<SavedLocationDTO | null> {
  try {
    const response = await api.get(`/api/v1/saved-locations/${id}`, {
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
 * Get all saved locations
 */
export async function getAllSavedLocations(idToken: string): Promise<SavedLocationDTO[]> {
  const response = await api.get('/api/v1/saved-locations', {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Get all saved locations for a specific user
 */
export async function getSavedLocationsByUserId(idToken: string, userId: string): Promise<SavedLocationDTO[]> {
  const response = await api.get(`/api/v1/saved-locations/user/${userId}`, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Update an existing saved location
 */
export async function updateSavedLocation(
  idToken: string,
  id: string,
  data: Omit<SavedLocationDTO, 'id'>,
): Promise<SavedLocationDTO> {
  const response = await api.put(`/api/v1/saved-locations/${id}`, data, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Delete a saved location
 */
export async function deleteSavedLocation(idToken: string, id: string): Promise<void> {
  await api.delete(`/api/v1/saved-locations/${id}`, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
}

// ============================================
// HELPER FUNCTIONS
// ============================================

/**
 * Check if a location name already exists for a user
 */
export async function checkLocationNameExists(idToken: string, userId: string, name: string): Promise<boolean> {
  const locations = await getSavedLocationsByUserId(idToken, userId);
  return locations.some((location) => location.name.toLowerCase() === name.toLowerCase());
}

/**
 * Get saved locations sorted by distance from a point
 */
export async function getSavedLocationsSortedByDistance(
  idToken: string,
  userId: string,
  fromLatitude: number,
  fromLongitude: number,
): Promise<SavedLocationDTO[]> {
  const locations = await getSavedLocationsByUserId(idToken, userId);

  // Calculate distance for each location
  const locationsWithDistance = locations.map((location) => ({
    ...location,
    distance: calculateDistance(fromLatitude, fromLongitude, location.latitude, location.longitude),
  }));

  // Sort by distance (closest first)
  return locationsWithDistance.sort((a, b) => a.distance - b.distance).map(({ distance, ...location }) => location);
}

/**
 * Calculate distance between two coordinates using Haversine formula
 * Returns distance in kilometers
 */
function calculateDistance(lat1: number, lon1: number, lat2: number, lon2: number): number {
  const R = 6371; // Earth's radius in km
  const dLat = toRadians(lat2 - lat1);
  const dLon = toRadians(lon2 - lon1);

  const a =
    Math.sin(dLat / 2) * Math.sin(dLat / 2) +
    Math.cos(toRadians(lat1)) * Math.cos(toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
  return R * c;
}

function toRadians(degrees: number): number {
  return degrees * (Math.PI / 180);
}
