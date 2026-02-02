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

export interface AuthResponse {
  idToken?: string;
  user: User;
  message?: string;
}

export interface AuthContextType {
  user: User | null;
  loading: boolean;
  signIn: (email: string, password: string) => Promise;
  signUp: (data: UserRegistration) => Promise;
  signInWithGoogle: () => Promise;
  signOut: () => Promise;
  forgotPassword: (email: string) => Promise;
}