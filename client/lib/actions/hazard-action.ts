/* eslint-disable @typescript-eslint/no-explicit-any */
import axios from 'axios';
import { HazardDTO, HazardSeverity, HazardStatus, PagedResponse } from '@/types';

export type { HazardDTO };
export { HazardSeverity, HazardStatus };

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

/**
 * Create a new hazard report
 */
export async function createHazard(idToken: string, data: HazardDTO): Promise<HazardDTO> {
    try {
        const response = await api.post('/hazards', data, {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to create hazard');
        }
        throw new Error('Failed to create hazard');
    }
}

/**
 * Get a hazard by ID
 */
export async function getHazardById(idToken: string, id: string): Promise<HazardDTO | null> {
    try {
        const response = await api.get(`/hazards/${id}`, {
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
            throw new Error(error.response.data.message || 'Failed to fetch hazard');
        }
        throw new Error('Failed to fetch hazard');
    }
}

/**
 * Get all hazards
 */
export async function getAllHazards(idToken: string): Promise<HazardDTO[]> {
    try {
        const response = await api.get('/hazards', {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to fetch hazards');
        }
        throw new Error('Failed to fetch hazards');
    }
}

/**
 * Get all hazards with pagination
 */
export async function getAllHazardsPaged(
    idToken: string,
    page: number = 0,
    size: number = 10,
): Promise<PagedResponse<HazardDTO>> {
    try {
        const response = await api.get('/hazards/paged', {
            params: { page, size },
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to fetch hazards');
        }
        throw new Error('Failed to fetch hazards');
    }
}

/**
 * Update an existing hazard
 */
export async function updateHazard(idToken: string, id: string, data: HazardDTO): Promise<HazardDTO> {
    try {
        const response = await api.put(`/hazards/${id}`, data, {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to update hazard');
        }
        throw new Error('Failed to update hazard');
    }
}

/**
 * Delete a hazard
 */
export async function deleteHazard(idToken: string, id: string): Promise<void> {
    try {
        await api.delete(`/hazards/${id}`, {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to delete hazard');
        }
        throw new Error('Failed to delete hazard');
    }
}

/**
 * Get all hazards by reporter ID
 */
export async function getHazardsByReporterId(idToken: string, reporterId: string): Promise<HazardDTO[]> {
    try {
        const response = await api.get(`/hazards/reporter/${reporterId}`, {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to fetch hazards by reporter');
        }
        throw new Error('Failed to fetch hazards by reporter');
    }
}

/**
 * Get all hazards by status
 */
export async function getHazardsByStatus(idToken: string, status: HazardStatus): Promise<HazardDTO[]> {
    try {
        const response = await api.get(`/hazards/status/${status}`, {
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to fetch hazards by status');
        }
        throw new Error('Failed to fetch hazards by status');
    }
}

/**
 * Get hazards by status with pagination
 */
export async function getHazardsByStatusPaged(
    idToken: string,
    status: HazardStatus,
    page: number = 0,
    size: number = 10,
): Promise<PagedResponse<HazardDTO>> {
    try {
        const response = await api.get(`/hazards/status/${status}/paged`, {
            params: { page, size },
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to fetch hazards by status');
        }
        throw new Error('Failed to fetch hazards by status');
    }
}

/**
 * Find hazards near a location
 */
export async function findNearbyHazards(
    idToken: string,
    longitude: number,
    latitude: number,
    maxDistanceMeters: number,
): Promise<HazardDTO[]> {
    try {
        const response = await api.get('/hazards/nearby', {
            params: { longitude, latitude, maxDistanceMeters },
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to find nearby hazards');
        }
        throw new Error('Failed to find nearby hazards');
    }
}

/**
 * Find active hazards near a location
 */
export async function findNearbyActiveHazards(
    idToken: string,
    longitude: number,
    latitude: number,
    maxDistanceMeters: number,
): Promise<HazardDTO[]> {
    try {
        const response = await api.get('/hazards/nearby/active', {
            params: { longitude, latitude, maxDistanceMeters },
            headers: {
                Authorization: `Bearer ${idToken}`,
            },
        });
        return response.data;
    } catch (error: any) {
        if (axios.isAxiosError(error) && error.response) {
            throw new Error(error.response.data.message || 'Failed to find nearby active hazards');
        }
        throw new Error('Failed to find nearby active hazards');
    }
}
