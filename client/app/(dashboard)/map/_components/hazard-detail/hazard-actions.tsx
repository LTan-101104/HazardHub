'use client';

import { useState } from 'react';
import { RotateCcw, Check, X, Loader2 } from 'lucide-react';
import { useMap } from '../map-provider';
import { useAuth } from '@/context/AuthContext';
import { auth } from '@/lib/firebase';
import { createHazardVerification } from '@/lib/actions/hazard-action';

export function HazardActions() {
  const { state, dispatch } = useMap();
  const { user } = useAuth();
  const [isLoading, setIsLoading] = useState(false);

  const handleVerification = async (type: 'CONFIRM' | 'DISPUTE') => {
    if (!user || !state.selectedHazard) return;

    setIsLoading(true);
    try {
      const currentUser = auth?.currentUser;
      if (!currentUser) return;
      const idToken = await currentUser.getIdToken();

      await createHazardVerification(idToken, {
        hazardId: state.selectedHazard.id,
        userId: user.id,
        verificationType: type,
      });

      dispatch({ type: 'SELECT_HAZARD', payload: null });
    } catch (err) {
      console.error(`Failed to ${type.toLowerCase()} hazard:`, err);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col gap-2">
      <button className="flex h-12 items-center justify-center gap-2 rounded-2xl bg-[#FF8400] font-mono text-sm font-semibold text-black transition-colors hover:bg-[#e67700]">
        <RotateCcw className="size-4" />
        Avoid &amp; Reroute
      </button>
      <div className="flex gap-2">
        <button
          onClick={() => handleVerification('CONFIRM')}
          disabled={isLoading}
          className="flex h-10 flex-1 items-center justify-center gap-1.5 rounded-xl border border-[#2E2E2E] text-sm text-[#B8B9B6] transition-colors hover:bg-[#252525] disabled:opacity-50"
        >
          {isLoading ? <Loader2 className="size-3.5 animate-spin" /> : <Check className="size-3.5" />}
          Confirm
        </button>
        <button
          onClick={() => handleVerification('DISPUTE')}
          disabled={isLoading}
          className="flex h-10 flex-1 items-center justify-center gap-1.5 rounded-xl border border-[#2E2E2E] text-sm text-[#B8B9B6] transition-colors hover:bg-[#252525] disabled:opacity-50"
        >
          {isLoading ? <Loader2 className="size-3.5 animate-spin" /> : <X className="size-3.5" />}
          Dispute
        </button>
      </div>
    </div>
  );
}
