import axios from 'axios';
import { EmergencyContactDTO, UpdateEmergencyContactDTO } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

/**
 * Get all emergency contacts for the current authenticated user
 */
export async function getEmergencyContacts(idToken: string): Promise<EmergencyContactDTO[]> {
  const response = await api.get('/api/v1/emergency-contacts', {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Get a specific emergency contact by ID for the current authenticated user
 */
export async function getEmergencyContactById(idToken: string, contactId: string): Promise<EmergencyContactDTO | null> {
  try {
    const response = await api.get(`/api/v1/emergency-contacts/${contactId}`, {
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
 * Create a new emergency contact for the current authenticated user
 * Note: userId is automatically set on the backend from the JWT token
 */
export async function createEmergencyContact(
  idToken: string,
  data: Omit<EmergencyContactDTO, 'id' | 'userId' | 'createdAt' | 'updatedAt'>,
): Promise<EmergencyContactDTO> {
  const response = await api.post('/api/v1/emergency-contacts', data, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Update an existing emergency contact for the current authenticated user
 */
export async function updateEmergencyContact(
  idToken: string,
  contactId: string,
  data: UpdateEmergencyContactDTO,
): Promise<EmergencyContactDTO> {
  const response = await api.put(`/api/v1/emergency-contacts/${contactId}`, data, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
  return response.data;
}

/**
 * Delete an emergency contact
 */
export async function deleteEmergencyContact(idToken: string, contactId: string): Promise<void> {
  await api.delete(`/api/v1/emergency-contacts/${contactId}`, {
    headers: { Authorization: `Bearer ${idToken}` },
  });
}

// ============================================
// HELPER FUNCTIONS
// ============================================

/**
 * Get emergency contacts sorted by priority (highest priority first)
 */
export async function getEmergencyContactsByPriority(idToken: string): Promise<EmergencyContactDTO[]> {
  const contacts = await getEmergencyContacts(idToken);
  return contacts.sort((a, b) => (b.priority || 1) - (a.priority || 1));
}

/**
 * Get the primary emergency contact (highest priority)
 */
export async function getPrimaryEmergencyContact(idToken: string): Promise<EmergencyContactDTO | null> {
  const contacts = await getEmergencyContactsByPriority(idToken);
  return contacts.length > 0 ? contacts[0] : null;
}

/**
 * Check if the user has any emergency contacts
 */
export async function hasEmergencyContacts(idToken: string): Promise<boolean> {
  const contacts = await getEmergencyContacts(idToken);
  return contacts.length > 0;
}

/**
 * Check if a phone number already exists in user's emergency contacts
 */
export async function checkPhoneNumberExists(idToken: string, phone: string, excludeContactId?: string): Promise<boolean> {
  const contacts = await getEmergencyContacts(idToken);
  return contacts.some((contact) => contact.phone === phone && (excludeContactId ? contact.id !== excludeContactId : true));
}

/**
 * Get emergency contacts by relationship type
 */
export async function getEmergencyContactsByRelationship(idToken: string, relationship: string): Promise<EmergencyContactDTO[]> {
  const contacts = await getEmergencyContacts(idToken);
  return contacts.filter((contact) => contact.relationship?.toLowerCase() === relationship.toLowerCase());
}

/**
 * Reorder emergency contacts by updating their priorities
 * Accepts an array of contact IDs in the desired order
 */
export async function reorderEmergencyContacts(idToken: string, orderedContactIds: string[]): Promise<EmergencyContactDTO[]> {
  const updatePromises = orderedContactIds.map((contactId, index) => {
    const priority = orderedContactIds.length - index; // Higher index = higher priority
    return updateEmergencyContact(idToken, contactId, { priority });
  });

  return Promise.all(updatePromises);
}

/**
 * Validate phone number format (basic validation)
 * Returns true if valid, false otherwise
 */
export function validatePhoneNumber(phone: string): boolean {
  // Basic phone validation: 10-20 digits, may include +, -, (, ), spaces
  const phoneRegex = /^[\+]?[(]?[0-9]{1,4}[)]?[-\s\.]?[(]?[0-9]{1,4}[)]?[-\s\.]?[0-9]{1,9}$/;
  return phoneRegex.test(phone.replace(/[\s\-\(\)]/g, ''));
}

/**
 * Format phone number for display
 * Example: "1234567890" -> "(123) 456-7890"
 */
export function formatPhoneNumber(phone: string): string {
  const cleaned = phone.replace(/\D/g, '');

  if (cleaned.length === 10) {
    return `(${cleaned.slice(0, 3)}) ${cleaned.slice(3, 6)}-${cleaned.slice(6)}`;
  } else if (cleaned.length === 11 && cleaned[0] === '1') {
    return `+1 (${cleaned.slice(1, 4)}) ${cleaned.slice(4, 7)}-${cleaned.slice(7)}`;
  }

  return phone; // Return original if doesn't match expected formats
}

// ============================================
// NOTIFICATION HELPERS
// ============================================

/**
 * Get formatted emergency contact information for notifications
 */
export function formatEmergencyContactForNotification(contact: EmergencyContactDTO): string {
  return `${contact.name} (${contact.relationship || 'Emergency Contact'}): ${contact.phone}`;
}

/**
 * Get all emergency contact phone numbers for batch notifications
 */
export async function getAllEmergencyPhoneNumbers(idToken: string): Promise<string[]> {
  const contacts = await getEmergencyContacts(idToken);
  return contacts.map((contact) => contact.phone);
}

/**
 * Get all emergency contact emails for batch notifications
 */
export async function getAllEmergencyEmails(idToken: string): Promise<string[]> {
  const contacts = await getEmergencyContacts(idToken);
  return contacts.filter((contact) => contact.email).map((contact) => contact.email!);
}
