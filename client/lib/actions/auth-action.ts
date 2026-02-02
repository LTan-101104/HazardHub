import { auth } from '@/lib/firebase';
import { UserRegistration } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

/**
 * Register a new user with the backend
 */
export async function registerUser(data: UserRegistration): Promise<void> {
  const response = await fetch(`${API_URL}/auth/register`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Registration failed');
  }
}

/**
 * Fetch current user data from the backend
 */
export async function fetchCurrentUser(idToken: string) {
  const response = await fetch(`${API_URL}/auth/me`, {
    headers: {
      'Authorization': `Bearer ${idToken}`,
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch user data');
  }

  return response.json();
}

/**
 * Update current user profile
 */
export async function updateUserProfile(idToken: string, data: Partial<UserRegistration>) {
  const response = await fetch(`${API_URL}/auth/me`, {
    method: 'PUT',
    headers: {
      'Authorization': `Bearer ${idToken}`,
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });

  if (!response.ok) {
    const error = await response.json();
    throw new Error(error.message || 'Failed to update profile');
  }

  return response.json();
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
