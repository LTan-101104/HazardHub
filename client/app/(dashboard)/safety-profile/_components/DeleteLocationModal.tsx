// app/safety-profile/_components/DeleteLocationModal.tsx

'use client';

import { useState } from 'react';
import { auth } from '@/lib/firebase';
import { deleteSavedLocation } from '@/lib/actions/saved_location-actions';
import type { SavedLocationDTO } from '@/types';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Loader2, AlertTriangle } from 'lucide-react';

interface DeleteLocationModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  location: SavedLocationDTO | null;
  onLocationDeleted: (locationId: string) => void;
}

export default function DeleteLocationModal({ open, onOpenChange, location, onLocationDeleted }: DeleteLocationModalProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleDelete = async () => {
    if (!location?.id) return;

    setLoading(true);
    setError(null);

    try {
      const idToken = await auth?.currentUser?.getIdToken();
      if (!idToken) {
        throw new Error('Not authenticated');
      }

      await deleteSavedLocation(idToken, location.id);
      onLocationDeleted(location.id);
      onOpenChange(false);
    } catch (err: any) {
      console.error('Failed to delete location:', err);
      setError(err.message || 'Failed to delete location. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setError(null);
    onOpenChange(false);
  };

  if (!location) return null;

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="bg-zinc-900 border-zinc-800 text-white max-w-md">
        <DialogHeader>
          <div className="flex items-center gap-3 mb-2">
            <div className="rounded-full p-3 bg-red-500/20">
              <AlertTriangle className="w-6 h-6 text-red-500" />
            </div>
            <div>
              <DialogTitle className="text-2xl font-bold">Delete Location</DialogTitle>
            </div>
          </div>
          <DialogDescription className="text-zinc-400">
            Are you sure you want to delete this saved location? This action cannot be undone.
          </DialogDescription>
        </DialogHeader>

        <div className="mt-4 p-4 bg-zinc-800 rounded-lg border border-zinc-700">
          <h4 className="font-semibold text-white mb-2">{location.name}</h4>
          {location.address && <p className="text-sm text-zinc-400 mb-2">{location.address}</p>}
          <p className="text-xs text-zinc-500 font-mono">
            {location.latitude.toFixed(4)}° N, {location.longitude.toFixed(4)}° W
          </p>
        </div>

        {/* Error Message */}
        {error && <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm">{error}</div>}

        {/* Actions */}
        <div className="flex gap-3 mt-6">
          <Button
            type="button"
            // variant="outline"
            onClick={handleClose}
            disabled={loading}
            className="flex-1 border-zinc-600 text-zinc-200 hover:bg-zinc-800 hover:text-white"
          >
            Cancel
          </Button>
          <Button
            type="button"
            onClick={handleDelete}
            disabled={loading}
            className="flex-1 bg-red-600 hover:bg-red-700 text-white font-semibold"
          >
            {loading ? (
              <>
                <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                Deleting...
              </>
            ) : (
              'Delete Location'
            )}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
