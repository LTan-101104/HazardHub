/* eslint-disable @typescript-eslint/no-explicit-any */
import axios from 'axios';
import { SOSEventDTO, SOSEventStatus, PagedResponse } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Create a new SOS event
 */
export async function createSOSEvent(idToken: string, data: SOSEventDTO): Promise<SOSEventDTO> {
  try {
    const response = await api.post('/sos-events', data, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to create SOS event');
    }
    throw new Error('Failed to create SOS event');
  }
}

/**
 * Get an SOS event by ID
 */
export async function getSOSEventById(idToken: string, id: string): Promise<SOSEventDTO | null> {
  try {
    const response = await api.get(`/sos-events/${id}`, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return null;
    }
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS event');
    }
    throw new Error('Failed to fetch SOS event');
  }
}

/**
 * Get all SOS events
 */
export async function getAllSOSEvents(idToken: string): Promise<SOSEventDTO[]> {
  try {
    const response = await api.get('/sos-events', {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS events');
    }
    throw new Error('Failed to fetch SOS events');
  }
}

/**
 * Get all SOS events with pagination
 */
export async function getAllSOSEventsPaged(
  idToken: string,
  page: number = 0,
  size: number = 10,
): Promise<PagedResponse<SOSEventDTO>> {
  try {
    const response = await api.get('/sos-events/paged', {
      params: { page, size },
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS events');
    }
    throw new Error('Failed to fetch SOS events');
  }
}

/**
 * Update an existing SOS event
 */
export async function updateSOSEvent(idToken: string, id: string, data: SOSEventDTO): Promise<SOSEventDTO> {
  try {
    const response = await api.put(`/sos-events/${id}`, data, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to update SOS event');
    }
    throw new Error('Failed to update SOS event');
  }
}

/**
 * Delete an SOS event
 */
export async function deleteSOSEvent(idToken: string, id: string): Promise<void> {
  try {
    await api.delete(`/sos-events/${id}`, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to delete SOS event');
    }
    throw new Error('Failed to delete SOS event');
  }
}

/**
 * Get all SOS events by user ID
 */
export async function getSOSEventsByUserId(idToken: string, userId: string): Promise<SOSEventDTO[]> {
  try {
    const response = await api.get(`/sos-events/user/${userId}`, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS events by user');
    }
    throw new Error('Failed to fetch SOS events by user');
  }
}

/**
 * Get all SOS events by trip ID
 */
export async function getSOSEventsByTripId(idToken: string, tripId: string): Promise<SOSEventDTO[]> {
  try {
    const response = await api.get(`/sos-events/trip/${tripId}`, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS events by trip');
    }
    throw new Error('Failed to fetch SOS events by trip');
  }
}

/**
 * Get all SOS events by status
 */
export async function getSOSEventsByStatus(idToken: string, status: SOSEventStatus): Promise<SOSEventDTO[]> {
  try {
    const response = await api.get(`/sos-events/status/${status}`, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS events by status');
    }
    throw new Error('Failed to fetch SOS events by status');
  }
}

/**
 * Get SOS events by status with pagination
 */
export async function getSOSEventsByStatusPaged(
  idToken: string,
  status: SOSEventStatus,
  page: number = 0,
  size: number = 10,
): Promise<PagedResponse<SOSEventDTO>> {
  try {
    const response = await api.get(`/sos-events/status/${status}/paged`, {
      params: { page, size },
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to fetch SOS events by status');
    }
    throw new Error('Failed to fetch SOS events by status');
  }
}

/**
 * Find SOS events near a location
 */
export async function findNearbySOSEvents(
  idToken: string,
  longitude: number,
  latitude: number,
  maxDistanceMeters: number,
): Promise<SOSEventDTO[]> {
  try {
    const response = await api.get('/sos-events/nearby', {
      params: { longitude, latitude, maxDistanceMeters },
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to find nearby SOS events');
    }
    throw new Error('Failed to find nearby SOS events');
  }
}

/**
 * Find active SOS events near a location
 */
export async function findNearbyActiveSOSEvents(
  idToken: string,
  longitude: number,
  latitude: number,
  maxDistanceMeters: number,
): Promise<SOSEventDTO[]> {
  try {
    const response = await api.get('/sos-events/nearby/active', {
      params: { longitude, latitude, maxDistanceMeters },
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to find nearby active SOS events');
    }
    throw new Error('Failed to find nearby active SOS events');
  }
}
