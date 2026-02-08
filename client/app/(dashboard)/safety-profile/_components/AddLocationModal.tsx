'use client';

import { useState } from 'react';
import { auth } from '@/lib/firebase';
import { useAuth } from '@/context/AuthContext';
import { createSavedLocation } from '@/lib/actions/saved_location-actions';
import type { SavedLocationDTO } from '@/types';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Textarea } from '@/components/ui/textarea';
import { Loader2, MapPin, Navigation } from 'lucide-react';

interface AddLocationModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onLocationAdded: (location: SavedLocationDTO) => void;
}

export default function AddLocationModal({ open, onOpenChange, onLocationAdded }: AddLocationModalProps) {
  const { user } = useAuth();
  const [loading, setLoading] = useState(false);
  const [gettingLocation, setGettingLocation] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    address: '',
    latitude: '',
    longitude: '',
  });

  const [validationErrors, setValidationErrors] = useState<{
    name?: string;
    latitude?: string;
    longitude?: string;
  }>({});

  const handleChange = (field: string, value: string) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    // Clear validation error for this field
    setValidationErrors((prev) => ({ ...prev, [field]: undefined }));
    setError(null);
  };

  const getCurrentLocation = () => {
    if (!navigator.geolocation) {
      setError('Geolocation is not supported by your browser');
      return;
    }

    setGettingLocation(true);
    setError(null);

    navigator.geolocation.getCurrentPosition(
      (position) => {
        setFormData((prev) => ({
          ...prev,
          latitude: position.coords.latitude.toFixed(6),
          longitude: position.coords.longitude.toFixed(6),
        }));
        setGettingLocation(false);

        // Optionally, reverse geocode to get address
        reverseGeocode(position.coords.latitude, position.coords.longitude);
      },
      (error) => {
        console.error('Error getting location:', error);
        setError('Unable to get your location. Please enter coordinates manually.');
        setGettingLocation(false);
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0,
      },
    );
  };

  const reverseGeocode = async (lat: number, lng: number) => {
    try {
      // Using OpenStreetMap Nominatim API (free, no API key needed)
      const response = await fetch(`https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}`);
      const data = await response.json();

      if (data.display_name) {
        setFormData((prev) => ({
          ...prev,
          address: data.display_name,
        }));
      }
    } catch (error) {
      console.error('Reverse geocoding failed:', error);
      // Don't show error to user, address is optional
    }
  };

  const validateForm = (): boolean => {
    const errors: { name?: string; latitude?: string; longitude?: string } = {};

    if (!formData.name.trim()) {
      errors.name = 'Location name is required';
    }

    const lat = parseFloat(formData.latitude);
    if (!formData.latitude || isNaN(lat)) {
      errors.latitude = 'Valid latitude is required';
    } else if (lat < -90 || lat > 90) {
      errors.latitude = 'Latitude must be between -90 and 90';
    }

    const lng = parseFloat(formData.longitude);
    if (!formData.longitude || isNaN(lng)) {
      errors.longitude = 'Valid longitude is required';
    } else if (lng < -180 || lng > 180) {
      errors.longitude = 'Longitude must be between -180 and 180';
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm() || !user) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const idToken = await auth?.currentUser?.getIdToken();
      if (!idToken) {
        throw new Error('Not authenticated');
      }

      const newLocation = await createSavedLocation(idToken, {
        userId: user.id,
        name: formData.name.trim(),
        address: formData.address.trim() || undefined,
        latitude: parseFloat(formData.latitude),
        longitude: parseFloat(formData.longitude),
      });

      onLocationAdded(newLocation);
      handleClose();
    } catch (err: any) {
      console.error('Failed to create location:', err);
      setError(err.message || 'Failed to save location. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData({
      name: '',
      address: '',
      latitude: '',
      longitude: '',
    });
    setValidationErrors({});
    setError(null);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="bg-zinc-900 border-zinc-800 text-white max-w-md max-h-[90vh] overflow-y-auto">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold">Add Saved Location</DialogTitle>
          <DialogDescription className="text-zinc-400">
            Save important locations for quick access during emergencies.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-5 mt-4">
          {/* Name Field */}
          <div className="space-y-2">
            <Label htmlFor="name" className="text-sm text-zinc-300">
              Location Name <span className="text-red-500">*</span>
            </Label>
            <Input
              id="name"
              type="text"
              placeholder="Home, Work, Emergency Shelter..."
              value={formData.name}
              onChange={(e) => handleChange('name', e.target.value)}
              className={`bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] ${
                validationErrors.name ? 'border-red-500' : ''
              }`}
              disabled={loading}
            />
            {validationErrors.name && <p className="text-xs text-red-400">{validationErrors.name}</p>}
          </div>

          {/* Address Field */}
          <div className="space-y-2">
            <Label htmlFor="address" className="text-sm text-zinc-300">
              Address <span className="text-zinc-500 text-xs">(Optional)</span>
            </Label>
            <Textarea
              id="address"
              placeholder="123 Main St, City, State, ZIP"
              value={formData.address}
              onChange={(e) => handleChange('address', e.target.value)}
              className="bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] resize-none"
              rows={2}
              disabled={loading}
            />
          </div>

          {/* Current Location Button */}
          <div className="flex items-center gap-2">
            <Button
              type="button"
              onClick={getCurrentLocation}
              disabled={loading || gettingLocation}
              // variant="outline"
              className="flex-1 border-zinc-600 text-zinc-200 hover:bg-zinc-800 hover:text-white"
            >
              {gettingLocation ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Getting Location...
                </>
              ) : (
                <>
                  <Navigation className="w-4 h-4 mr-2" />
                  Use Current Location
                </>
              )}
            </Button>
          </div>

          {/* Coordinates */}
          <div className="grid grid-cols-2 gap-4">
            {/* Latitude */}
            <div className="space-y-2">
              <Label htmlFor="latitude" className="text-sm text-zinc-300">
                Latitude <span className="text-red-500">*</span>
              </Label>
              <Input
                id="latitude"
                type="text"
                placeholder="42.3601"
                value={formData.latitude}
                onChange={(e) => handleChange('latitude', e.target.value)}
                className={`bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] ${
                  validationErrors.latitude ? 'border-red-500' : ''
                }`}
                disabled={loading}
              />
              {validationErrors.latitude && <p className="text-xs text-red-400">{validationErrors.latitude}</p>}
            </div>

            {/* Longitude */}
            <div className="space-y-2">
              <Label htmlFor="longitude" className="text-sm text-zinc-300">
                Longitude <span className="text-red-500">*</span>
              </Label>
              <Input
                id="longitude"
                type="text"
                placeholder="-71.0589"
                value={formData.longitude}
                onChange={(e) => handleChange('longitude', e.target.value)}
                className={`bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] ${
                  validationErrors.longitude ? 'border-red-500' : ''
                }`}
                disabled={loading}
              />
              {validationErrors.longitude && <p className="text-xs text-red-400">{validationErrors.longitude}</p>}
            </div>
          </div>

          {/* Helper Text */}
          <div className="flex items-start gap-2 p-3 bg-zinc-800/50 rounded-lg">
            <MapPin className="w-4 h-4 text-zinc-400 mt-0.5 flex-shrink-0" />
            <p className="text-xs text-zinc-400">
              You can use your current location or enter coordinates manually. To find coordinates, search the location on Google
              Maps and right-click to copy coordinates.
            </p>
          </div>

          {/* Error Message */}
          {error && <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm">{error}</div>}

          {/* Actions */}
          <div className="flex gap-3 pt-2">
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
              type="submit"
              disabled={loading || gettingLocation}
              className="flex-1 bg-[#ff8400] hover:bg-[#ff8400]/90 text-black font-semibold"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Saving...
                </>
              ) : (
                'Save Location'
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
