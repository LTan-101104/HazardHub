/* eslint-disable @typescript-eslint/no-explicit-any */
import axios from 'axios';
import { auth } from '@/lib/firebase';
import { UserRegistration, UserProfileUpdate } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Register a new user with the backend
 */
export async function registerUser(data: UserRegistration): Promise<void> {
  try {
    await api.post('/auth/register', data);
  } catch (error) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Registration failed');
    }
    throw new Error('Registration failed');
  }
}

/**
 * Fetch current user data from the backend
 */
export async function fetchCurrentUser(idToken: string) {
  try {
    const response = await api.get('/auth/me', {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    throw new Error(error.response.data.message || 'Failed to fetch user data');
  }
}

/**
 * Update current user profile
 */
export async function updateUserProfile(idToken: string, data: UserProfileUpdate) {
  try {
    const response = await api.put('/auth/me', data, {
      headers: {
        Authorization: `Bearer ${idToken}`,
      },
    });
    return response.data;
  } catch (error: any) {
    if (axios.isAxiosError(error) && error.response) {
      throw new Error(error.response.data.message || 'Failed to update profile');
    }
    throw new Error('Failed to update profile');
  }
}

/**
 * Get the current user's ID token
 */
export async function getIdToken(): Promise<string | null> {
  const currentUser = auth.currentUser;
  if (!currentUser) {
    return null;
  }
  return currentUser.getIdToken();
}
