'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/lib/context/AuthContext';
import { AuthLayout } from '@/components/auth-component/AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Checkbox } from '@/components/ui/checkbox';

export default function SignUpPage() {
    const router = useRouter();
    const { signUp, signInWithGoogle } = useAuth();
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const [agreedToTerms, setAgreedToTerms] = useState(false);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setFormData({
            ...formData,
            [e.target.id]: e.target.value,
        });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');

        if (formData.password !== formData.confirmPassword) {
            setError('Passwords do not match');
            return;
        }

        if (!agreedToTerms) {
            setError('You must agree to the Terms of Service and Privacy Policy');
            return;
        }

        setLoading(true);

        try {
            await signUp({
                email: formData.email,
                password: formData.password,
                displayName: `${formData.firstName} ${formData.lastName}`,
            });
            router.push('/map');
        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : 'Failed to create account';
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    const handleGoogleSignUp = async () => {
        if (!agreedToTerms) {
            setError('You must agree to the Terms of Service and Privacy Policy');
            return;
        }

        setError('');
        setLoading(true);

        try {
            await signInWithGoogle();
            router.push('/map');
        } catch (err) {
            const errorMessage = err instanceof Error ? err.message : 'Failed to sign up with Google';
            setError(errorMessage);
        } finally {
            setLoading(false);
        }
    };

    return (
        <AuthLayout heading="Create account" subheading="Get started with your free account">
            <form onSubmit={handleSubmit} className="space-y-4">
                {/* Name Fields */}
                <div className="grid grid-cols-2 gap-4">
                    <div className="space-y-2">
                        <Label htmlFor="firstName" className="text-gray-300">First Name</Label>
                        <Input
                            id="firstName"
                            type="text"
                            placeholder="John"
                            value={formData.firstName}
                            onChange={handleChange}
                            required
                            className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
                        />
                    </div>
                    <div className="space-y-2">
                        <Label htmlFor="lastName" className="text-gray-300">Last Name</Label>
                        <Input
                            id="lastName"
                            type="text"
                            placeholder="Doe"
                            value={formData.lastName}
                            onChange={handleChange}
                            required
                            className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
                        />
                    </div>
                </div>

                {/* Email Field */}
                <div className="space-y-2">
                    <Label htmlFor="email" className="text-gray-300">Email</Label>
                    <Input
                        id="email"
                        type="email"
                        placeholder="john@example.com"
                        value={formData.email}
                        onChange={handleChange}
                        required
                        className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
                    />
                </div>

                {/* Password Field */}
                <div className="space-y-2">
                    <Label htmlFor="password" className="text-gray-300">Password</Label>
                    <Input
                        id="password"
                        type="password"
                        placeholder="Create a password"
                        value={formData.password}
                        onChange={handleChange}
                        required
                        className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
                    />
                </div>

                {/* Confirm Password Field */}
                <div className="space-y-2">
                    <Label htmlFor="confirmPassword" className="text-gray-300">Confirm Password</Label>
                    <Input
                        id="confirmPassword"
                        type="password"
                        placeholder="Confirm your password"
                        value={formData.confirmPassword}
                        onChange={handleChange}
                        required
                        className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
                    />
                </div>

                {/* Terms Checkbox */}
                <div className="flex items-start gap-2">
                    <Checkbox
                        id="terms"
                        checked={agreedToTerms}
                        onCheckedChange={(checked) => setAgreedToTerms(checked as boolean)}
                        className="mt-0.5 border-gray-600 data-[state=checked]:bg-[#ff8c00] data-[state=checked]:border-[#ff8c00]"
                    />
                    <Label htmlFor="terms" className="text-sm text-gray-400">
                        I agree to the{' '}
                        <Link href="/terms" className="text-[#ff8c00] hover:underline">
                            Terms of Service
                        </Link>{' '}
                        and{' '}
                        <Link href="/privacy" className="text-[#ff8c00] hover:underline">
                            Privacy Policy
                        </Link>
                    </Label>
                </div>

                {/* Error Message */}
                {error && (
                    <div className="p-3 bg-red-500/20 border border-red-500 rounded-lg text-red-400 text-sm">
                        {error}
                    </div>
                )}

                {/* Create Account Button */}
                <Button
                    type="submit"
                    disabled={loading}
                    className="w-full bg-[#ff8c00] hover:bg-[#ff8c00]/90 text-gray-900 font-semibold h-12 text-base cursor-pointer"
                >
                    {loading ? 'Creating account...' : 'Create Account'}
                </Button>
            </form>

            {/* Divider */}
            <div className="relative">
                <div className="absolute inset-0 flex items-center">
                    <div className="w-full border-t border-gray-700"></div>
                </div>
                <div className="relative flex justify-center text-sm">
                    <span className="px-2 bg-[#1a1a1a] text-gray-400">or</span>
                </div>
            </div>

            {/* Google Sign Up */}
            <Button
                type="button"
                variant="outline"
                onClick={handleGoogleSignUp}
                disabled={loading}
                className="w-full border-[#ff8c00] bg-transparent text-white hover:bg-[#ff8c00]/10 h-12 text-base cursor-pointer"
            >
                Sign up with Google
            </Button>

            {/* Sign In Link */}
            <p className="text-center text-gray-400">
                Already have an account?{' '}
                <Link href="/sign-in" className="text-[#ff8c00] hover:underline">
                    Sign in
                </Link>
            </p>
        </AuthLayout>
    );
}