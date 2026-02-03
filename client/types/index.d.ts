export interface User {
  id: string;
  email: string;
  phone?: string | null;
  displayName: string;
  insuranceDispatchConfig?: Record | null;
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