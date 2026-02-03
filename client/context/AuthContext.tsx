'use client';

import { createContext, useContext, useEffect, useState } from 'react';
import {
    signInWithEmailAndPassword,
    signOut as firebaseSignOut,
    onAuthStateChanged,
    GoogleAuthProvider,
    signInWithPopup,
    sendPasswordResetEmail,
    sendEmailVerification,
} from 'firebase/auth';
import axios from 'axios';
import { auth } from '@/lib/firebase';
import { User, AuthContextType, UserRegistration } from '@/types';

const API_URL = process.env.NEXT_PUBLIC_API_URL;

const api = axios.create({
    baseURL: API_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<User | null>(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!auth) {
            setLoading(false);
            return;
        }
        const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
            if (firebaseUser) {
                const idToken = await firebaseUser.getIdToken();

                // Fetch user data from backend
                try {
                    const response = await api.get('/auth/me', {
                        headers: {
                            Authorization: `Bearer ${idToken}`,
                        },
                    });
                    setUser(response.data);
                } catch (error) {
                    console.error('Error fetching user data:', error);
                    if (auth) await firebaseSignOut(auth);
                    setUser(null);
                }
            } else {
                setUser(null);
            }
            setLoading(false);
        });

        return () => unsubscribe();
    }, []);

    const requireAuth = () => {
        if (!auth) throw new Error('Firebase is not configured. Set NEXT_PUBLIC_FIREBASE_API_KEY in .env.local.');
        return auth;
    };

    const signIn = async (email: string, password: string) => {
        try {
            const firebaseAuth = requireAuth();
            const userCredential = await signInWithEmailAndPassword(firebaseAuth, email, password);

            // Check if email is verified
            if (!userCredential.user.emailVerified) {
                await firebaseSignOut(firebaseAuth);
                throw new Error('Please verify your email before signing in. Check your inbox for the verification link.');
            }

            const idToken = await userCredential.user.getIdToken();

            // Fetch user data from backend and update state before returning
            const response = await api.get('/auth/me', {
                headers: {
                    Authorization: `Bearer ${idToken}`,
                },
            });
            setUser(response.data);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Sign in failed';
            throw new Error(errorMessage);
        }
    };

    const signUp = async (data: UserRegistration) => {
        try {
            const firebaseAuth = requireAuth();
            // Register with backend first
            await api.post('/auth/register', data);

            // Sign in to get the user credential, then send verification email
            const userCredential = await signInWithEmailAndPassword(firebaseAuth, data.email, data.password);

            // Send email verification
            await sendEmailVerification(userCredential.user);

            // Sign out - user must verify email before they can sign in
            await firebaseSignOut(firebaseAuth);

            // Don't set user - they need to verify email first
        } catch (error: unknown) {
            if (axios.isAxiosError(error) && error.response) {
                throw new Error(error.response.data.message || 'Registration failed');
            }
            const errorMessage = error instanceof Error ? error.message : 'Registration failed';
            throw new Error(errorMessage);
        }
    };

    const signInWithGoogle = async () => {
        try {
            const firebaseAuth = requireAuth();
            const provider = new GoogleAuthProvider();
            const result = await signInWithPopup(firebaseAuth, provider);

            // Get token and fetch/create user in backend
            const idToken = await result.user.getIdToken();
            const response = await api.get('/auth/me', {
                headers: {
                    Authorization: `Bearer ${idToken}`,
                },
            });
            setUser(response.data);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Google sign in failed';
            throw new Error(errorMessage);
        }
    };

    const signOut = async () => {
        try {
            await firebaseSignOut(requireAuth());
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Sign out failed';
            throw new Error(errorMessage);
        }
    };

    const forgotPassword = async (email: string) => {
        try {
            await sendPasswordResetEmail(requireAuth(), email);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Failed to send reset email';
            throw new Error(errorMessage);
        }
    };

    const resendVerificationEmail = async (email: string, password: string) => {
        try {
            const firebaseAuth = requireAuth();
            // Sign in temporarily to get the user
            const userCredential = await signInWithEmailAndPassword(firebaseAuth, email, password);

            if (userCredential.user.emailVerified) {
                await firebaseSignOut(firebaseAuth);
                throw new Error('Email is already verified. You can sign in now.');
            }

            // Send verification email
            await sendEmailVerification(userCredential.user);

            // Sign out again
            await firebaseSignOut(firebaseAuth);
        } catch (error: unknown) {
            const errorMessage = error instanceof Error ? error.message : 'Failed to send verification email';
            throw new Error(errorMessage);
        }
    };

    const value: AuthContextType = {
        user,
        loading,
        signIn,
        signUp,
        signInWithGoogle,
        signOut,
        forgotPassword,
        resendVerificationEmail,
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
}

export function useAuth() {
    const context = useContext(AuthContext);
    if (context === undefined) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
}
