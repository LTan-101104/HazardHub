'use client';

import { useState } from 'react';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { AuthLayout } from '@/components/auth-component/AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export default function ForgotPassword() {
    const { forgotPassword } = useAuth();
    const [email, setEmail] = useState('');
    const [error, setError] = useState('');
    const [success, setSuccess] = useState(false);
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        setSuccess(false);
        setLoading(true);

        try {
            await forgotPassword(email);
            setSuccess(true);
        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : 'Failed to send reset email';
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayout heading="Reset password" subheading="Enter your email to receive a reset link">
            {success ? (
                <div className="space-y-6">
                    <div className="p-4 bg-green-500/20 border border-green-500 rounded-lg text-green-400 text-sm">
                        Password reset email sent! Check your inbox for a link to reset your password.
                    </div>
                    <Link href="/sign-in">
                        <Button
                            type="button"
                            className="w-full bg-[#ff8c00] hover:bg-[#ff8c00]/90 text-gray-900 font-semibold h-12 text-base cursor-pointer"
                        >
                            Back to Sign In
                        </Button>
                    </Link>
                </div>
            ) : (
                <>
                    <form onSubmit={handleSubmit} className="space-y-6">
                        <div className="space-y-2">
                            <Label htmlFor="email" className="text-gray-300">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                placeholder="Enter your email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                required
                                className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
                            />
                        </div>

                        {error && (
                            <div className="p-3 bg-red-500/20 border border-red-500 rounded-lg text-red-400 text-sm">
                                {error}
                            </div>
                        )}

                        <Button
                            type="submit"
                            disabled={loading}
                            className="w-full bg-[#ff8c00] hover:bg-[#ff8c00]/90 text-gray-900 font-semibold h-12 text-base cursor-pointer"
                        >
                            {loading ? 'Sending...' : 'Send Reset Link'}
                        </Button>
                    </form>

                    <p className="text-center text-gray-400">
                        Remember your password?{' '}
                        <Link href="/sign-in" className="text-[#ff8c00] hover:underline">
                            Sign in
                        </Link>
                    </p>
                </>
            )}
        </AuthLayout>
    );
}