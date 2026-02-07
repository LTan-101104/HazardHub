/* eslint-disable @typescript-eslint/no-explicit-any */
'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import Link from 'next/link';
import { useAuth } from '@/context/AuthContext';
import { AuthLayout } from '@/components/auth-component/AuthLayout';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';

export default function SignIn() {
  const router = useRouter();
  const { signIn, signInWithGoogle, resendVerificationEmail } = useAuth();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [showResendOption, setShowResendOption] = useState(false);
  const [resendSuccess, setResendSuccess] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setShowResendOption(false);
    setResendSuccess(false);
    setLoading(true);

    try {
      await signIn(email, password);
      router.push('/map');
    } catch (error: any) {
      const errorMessage = error.message || 'Failed to sign in';
      setError(errorMessage);
      // Check if error is about email verification
      if (errorMessage.toLowerCase().includes('verify your email')) {
        setShowResendOption(true);
      }
    } finally {
      setLoading(false);
    }
  };

  const handleResendVerification = async () => {
    if (!email || !password) {
      setError('Please enter your email and password to resend verification');
      return;
    }

    setLoading(true);
    setError('');

    try {
      await resendVerificationEmail(email, password);
      setResendSuccess(true);
      setShowResendOption(false);
    } catch (error: any) {
      setError(error.message || 'Failed to resend verification email');
    } finally {
      setLoading(false);
    }
  };

  const handleGoogleSignIn = async () => {
    setError('');
    setLoading(true);

    try {
      await signInWithGoogle();
      router.push('/map');
    } catch (error: any) {
      setError(error.message || 'Failed to sign in with Google');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout heading="Welcome back" subheading="Sign in to your account to continue">
      <form onSubmit={handleSubmit} className="space-y-6">
        <div className="space-y-2">
          <Label htmlFor="email" className="text-gray-300">
            Email
          </Label>
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

        <div className="space-y-2">
          <Label htmlFor="password" className="text-gray-300">
            Password
          </Label>
          <Input
            id="password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            className="bg-[#2a2a2a] border-gray-700 text-white placeholder:text-gray-500 h-12"
          />
        </div>

        <div className="flex justify-end">
          <Link href="/forgot-password" className="text-sm text-[#ff8c00] hover:underline">
            Forgot password?
          </Link>
        </div>

        {error && (
          <div className="p-3 bg-red-500/20 border border-red-500 rounded-lg text-red-400 text-sm">
            {error}
            {showResendOption && (
              <button
                type="button"
                onClick={handleResendVerification}
                disabled={loading}
                className="block mt-2 text-[#ff8c00] hover:underline cursor-pointer"
              >
                Resend verification email
              </button>
            )}
          </div>
        )}

        {resendSuccess && (
          <div className="p-3 bg-green-500/20 border border-green-500 rounded-lg text-green-400 text-sm">
            Verification email sent! Check your inbox and click the link to verify your account.
          </div>
        )}

        <Button
          type="submit"
          disabled={loading}
          className="w-full bg-[#ff8c00] hover:bg-[#ff8c00]/90 text-gray-900 font-semibold h-12 text-base cursor-pointer"
        >
          {loading ? 'Signing in...' : 'Sign In'}
        </Button>
      </form>

      <div className="relative">
        <div className="absolute inset-0 flex items-center">
          <div className="w-full border-t border-gray-700"></div>
        </div>
        <div className="relative flex justify-center text-sm">
          <span className="px-2 bg-[#1a1a1a] text-gray-400">or</span>
        </div>
      </div>

      <Button
        type="button"
        variant="outline"
        onClick={handleGoogleSignIn}
        disabled={loading}
        className="w-full border-[#ff8c00] bg-transparent text-white hover:bg-[#ff8c00]/10 h-12 text-base cursor-pointer"
      >
        Continue with Google
      </Button>

      <p className="text-center text-gray-400">
        Don&apos;t have an account?{' '}
        <Link href="/sign-up" className="text-[#ff8c00] hover:underline">
          Sign up
        </Link>
      </p>
    </AuthLayout>
  );
}
