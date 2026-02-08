'use client';

import { useEffect, useState } from 'react';
import { RotateCcw, Check, X, Loader2 } from 'lucide-react';
import { useMap } from '../map-provider';
import { useAuth } from '@/context/AuthContext';
import { auth } from '@/lib/firebase';
import {
  createHazardVerification,
  getUserHazardVerification,
  deleteHazardVerification,
  getHazardById,
} from '@/lib/actions/hazard-action';

type VerificationType = 'CONFIRM' | 'DISPUTE';

export function HazardActions() {
  const { state, dispatch } = useMap();
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [existingVerification, setExistingVerification] = useState<VerificationType | null>(null);
  const [existingVerificationId, setExistingVerificationId] = useState<string | null>(null);
  const [isCheckingStatus, setIsCheckingStatus] = useState(true);

  // Fetch the user's existing verification for this hazard
  useEffect(() => {
    if (!user || !state.selectedHazard) {
      setIsCheckingStatus(false);
      return;
    }

    let cancelled = false;

    async function checkVerificationStatus() {
      setIsCheckingStatus(true);
      try {
        const currentUser = auth?.currentUser;
        if (!currentUser) return;
        const idToken = await currentUser.getIdToken();

        const result = await getUserHazardVerification(idToken, state.selectedHazard!.id, user!.id);
        if (!cancelled) {
          setExistingVerification(result?.verificationType ?? null);
          setExistingVerificationId(result?.id ?? null);
        }
      } catch (err) {
        console.error('Failed to check verification status:', err);
      } finally {
        if (!cancelled) setIsCheckingStatus(false);
      }
    }

    checkVerificationStatus();
    return () => {
      cancelled = true;
    };
  }, [user, state.selectedHazard]);

  const handleVerification = async (type: VerificationType) => {
    if (!user || !state.selectedHazard) return;

    setIsLoading(true);
    try {
      const currentUser = auth?.currentUser;
      if (!currentUser) return;
      const idToken = await currentUser.getIdToken();

      // If clicking the same type again, remove the verification (toggle off)
      if (existingVerification === type && existingVerificationId) {
        await deleteHazardVerification(idToken, existingVerificationId);
        setExistingVerification(null);
        setExistingVerificationId(null);
      } else {
        // If switching from one type to another, delete old one first
        if (existingVerificationId) {
          await deleteHazardVerification(idToken, existingVerificationId);
        }
        await createHazardVerification(idToken, {
          hazardId: state.selectedHazard.id,
          userId: user.id,
          verificationType: type,
        });
        // Re-fetch to get the new verification id
        const result = await getUserHazardVerification(idToken, state.selectedHazard.id, user.id);
        setExistingVerification(result?.verificationType ?? type);
        setExistingVerificationId(result?.id ?? null);
      }

      // Refresh hazard counts so the UI shows the updated reportCount
      try {
        const updatedHazard = await getHazardById(idToken, state.selectedHazard.id);
        dispatch({
          type: 'SELECT_HAZARD',
          payload: {
            ...state.selectedHazard,
            reportCount: (updatedHazard.verificationCount ?? 0) + 1,
          },
        });
      } catch {
        // Non-critical — counts will refresh on next open
      }
    } catch (err) {
      console.error(`Failed to ${type.toLowerCase()} hazard:`, err);
    } finally {
      setIsLoading(false);
    }
  };

  const hasConfirmed = existingVerification === 'CONFIRM';
  const hasDisputed = existingVerification === 'DISPUTE';
  const hasVoted = hasConfirmed || hasDisputed;

  return (
    <div className="flex flex-col gap-2">
      <button className="flex h-12 items-center justify-center gap-2 rounded-2xl bg-[#FF8400] font-mono text-sm font-semibold text-black transition-colors hover:bg-[#e67700]">
        <RotateCcw className="size-4" />
        Avoid &amp; Reroute
      </button>

      {/* Status message when user has already voted */}
      {hasVoted && (
        <p className="text-center text-xs text-[#B8B9B6]">
          You {hasConfirmed ? 'confirmed' : 'disputed'} this hazard. Tap again to undo.
        </p>
      )}

      <div className="flex gap-2">
        <button
          onClick={() => handleVerification('CONFIRM')}
          disabled={isLoading || isCheckingStatus || hasDisputed}
          className={`flex h-10 flex-1 items-center justify-center gap-1.5 rounded-xl border text-sm transition-colors disabled:cursor-not-allowed ${
            hasConfirmed
              ? 'border-green-500/50 bg-green-500/10 text-green-400 hover:bg-green-500/20'
              : 'border-[#2E2E2E] text-[#B8B9B6] hover:bg-[#252525] disabled:opacity-50'
          }`}
        >
          {isLoading || isCheckingStatus ? <Loader2 className="size-3.5 animate-spin" /> : <Check className="size-3.5" />}
          {hasConfirmed ? 'Confirmed ✓' : 'Confirm'}
        </button>
        <button
          onClick={() => handleVerification('DISPUTE')}
          disabled={isLoading || isCheckingStatus || hasConfirmed}
          className={`flex h-10 flex-1 items-center justify-center gap-1.5 rounded-xl border text-sm transition-colors disabled:cursor-not-allowed ${
            hasDisputed
              ? 'border-red-500/50 bg-red-500/10 text-red-400 hover:bg-red-500/20'
              : 'border-[#2E2E2E] text-[#B8B9B6] hover:bg-[#252525] disabled:opacity-50'
          }`}
        >
          {isLoading || isCheckingStatus ? <Loader2 className="size-3.5 animate-spin" /> : <X className="size-3.5" />}
          {hasDisputed ? 'Disputed ✓' : 'Dispute'}
        </button>
      </div>
    </div>
  );
}
