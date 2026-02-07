export interface User {
  id: string;
  email: string;
  phone?: string | null;
  displayName: string;
  insuranceDispatchConfig?: Record<string, unknown> | null;
  createdAt?: Date;
  updatedAt?: Date;
}

export interface UserRegistration {
  email: string;
  password: string;
  displayName: string;
  phone?: string;
}

export interface UserProfileUpdate {
  displayName?: string;
  phone?: string | null;
  insuranceDispatchConfig?: Record<string, unknown> | null;
}

export interface AuthResponse {
  idToken?: string;
  user: User;
  message?: string;
}

export interface AuthContextType {
  user: User | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise<void>;
  signUp: (data: UserRegistration) => Promise<void>;
  signInWithGoogle: () => Promise<void>;
  signOut: () => Promise<void>;
  forgotPassword: (email: string) => Promise<void>;
  resendVerificationEmail: (email: string, password: string) => Promise<void>;
}

// SOS Event Status enum
export enum SOSEventStatus {
  ACTIVE = 'ACTIVE',
  HELP_ARRIVING = 'HELP_ARRIVING',
  RESOLVED = 'RESOLVED',
}

// SOS Event DTO interface
export interface SOSEventDTO {
  id?: string;
  userId: string;
  tripId?: string;
  triggeredAt?: string;
  resolvedAt?: string;
  longitude: number;
  latitude: number;
  locationAccuracyMeters: number;
  status?: SOSEventStatus;
  dispatchNotified?: boolean;
  dispatchReference?: string;
}

// Paginated response interface
export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}
