'use client';

import { useState } from 'react';
import { auth } from '@/lib/firebase';
import { createEmergencyContact, validatePhoneNumber } from '@/lib/actions/emergency-actions';
import type { EmergencyContactDTO } from '@/types';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Loader2, Phone, Mail, User, Users } from 'lucide-react';

interface AddContactModalProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  onContactAdded: (contact: EmergencyContactDTO) => void;
}

export default function AddContactModal({ open, onOpenChange, onContactAdded }: AddContactModalProps) {
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    relationship: '',
    priority: 1,
  });

  const [validationErrors, setValidationErrors] = useState<{
    name?: string;
    phone?: string;
    email?: string;
  }>({});

  const handleChange = (field: string, value: string | number) => {
    setFormData((prev) => ({ ...prev, [field]: value }));
    // Clear validation error for this field
    setValidationErrors((prev) => ({ ...prev, [field]: undefined }));
    setError(null);
  };

  const validateForm = (): boolean => {
    const errors: { name?: string; phone?: string; email?: string } = {};

    if (!formData.name.trim()) {
      errors.name = 'Name is required';
    }

    if (!formData.phone.trim()) {
      errors.phone = 'Phone number is required';
    } else if (!validatePhoneNumber(formData.phone)) {
      errors.phone = 'Invalid phone number format';
    }

    if (formData.email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
      errors.email = 'Invalid email format';
    }

    setValidationErrors(errors);
    return Object.keys(errors).length === 0;
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      return;
    }

    setLoading(true);
    setError(null);

    try {
      const idToken = await auth?.currentUser?.getIdToken();
      if (!idToken) {
        throw new Error('Not authenticated');
      }

      const newContact = await createEmergencyContact(idToken, {
        name: formData.name.trim(),
        phone: formData.phone.trim(),
        email: formData.email.trim() || undefined,
        relationship: formData.relationship.trim() || undefined,
        priority: formData.priority,
      });

      onContactAdded(newContact);
      handleClose();
    } catch (err: any) {
      console.error('Failed to create contact:', err);
      setError(err.message || 'Failed to create contact. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  const handleClose = () => {
    setFormData({
      name: '',
      phone: '',
      email: '',
      relationship: '',
      priority: 1,
    });
    setValidationErrors({});
    setError(null);
    onOpenChange(false);
  };

  return (
    <Dialog open={open} onOpenChange={handleClose}>
      <DialogContent className="bg-zinc-900 border-zinc-800 text-white max-w-md">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold">Add Emergency Contact</DialogTitle>
          <DialogDescription className="text-zinc-400">
            Add someone you trust to contact in case of an emergency.
          </DialogDescription>
        </DialogHeader>

        <form onSubmit={handleSubmit} className="space-y-5 mt-4">
          {/* Name Field */}
          <div className="space-y-2">
            <Label htmlFor="name" className="text-sm text-zinc-300">
              Name <span className="text-red-500">*</span>
            </Label>
            <div className="relative">
              <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
              <Input
                id="name"
                type="text"
                placeholder="John Doe"
                value={formData.name}
                onChange={(e) => handleChange('name', e.target.value)}
                className={`pl-10 bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] ${
                  validationErrors.name ? 'border-red-500' : ''
                }`}
                disabled={loading}
              />
            </div>
            {validationErrors.name && <p className="text-xs text-red-400">{validationErrors.name}</p>}
          </div>

          {/* Phone Field */}
          <div className="space-y-2">
            <Label htmlFor="phone" className="text-sm text-zinc-300">
              Phone Number <span className="text-red-500">*</span>
            </Label>
            <div className="relative">
              <Phone className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
              <Input
                id="phone"
                type="tel"
                placeholder="(555) 123-4567"
                value={formData.phone}
                onChange={(e) => handleChange('phone', e.target.value)}
                className={`pl-10 bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] ${
                  validationErrors.phone ? 'border-red-500' : ''
                }`}
                disabled={loading}
              />
            </div>
            {validationErrors.phone && <p className="text-xs text-red-400">{validationErrors.phone}</p>}
          </div>

          {/* Email Field (Optional) */}
          <div className="space-y-2">
            <Label htmlFor="email" className="text-sm text-zinc-300">
              Email <span className="text-zinc-500 text-xs">(Optional)</span>
            </Label>
            <div className="relative">
              <Mail className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
              <Input
                id="email"
                type="email"
                placeholder="john@example.com"
                value={formData.email}
                onChange={(e) => handleChange('email', e.target.value)}
                className={`pl-10 bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400] ${
                  validationErrors.email ? 'border-red-500' : ''
                }`}
                disabled={loading}
              />
            </div>
            {validationErrors.email && <p className="text-xs text-red-400">{validationErrors.email}</p>}
          </div>

          {/* Relationship Field (Optional) */}
          <div className="space-y-2">
            <Label htmlFor="relationship" className="text-sm text-zinc-300">
              Relationship <span className="text-zinc-500 text-xs">(Optional)</span>
            </Label>
            <div className="relative">
              <Users className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-zinc-500" />
              <Input
                id="relationship"
                type="text"
                placeholder="Spouse, Parent, Friend..."
                value={formData.relationship}
                onChange={(e) => handleChange('relationship', e.target.value)}
                className="pl-10 bg-zinc-800 border-zinc-700 text-white placeholder:text-zinc-500 focus:border-[#ff8400] focus:ring-[#ff8400]"
                disabled={loading}
              />
            </div>
          </div>

          {/* Priority Field */}
          <div className="space-y-2">
            <Label htmlFor="priority" className="text-sm text-zinc-300">
              Priority (1-100)
            </Label>
            <Input
              id="priority"
              type="number"
              min="1"
              max="100"
              value={formData.priority}
              onChange={(e) => handleChange('priority', parseInt(e.target.value) || 1)}
              className="bg-zinc-800 border-zinc-700 text-white focus:border-[#ff8400] focus:ring-[#ff8400]"
              disabled={loading}
            />
            <p className="text-xs text-zinc-500">Higher priority contacts appear first (100 = highest priority)</p>
          </div>

          {/* Error Message */}
          {error && <div className="p-3 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400 text-sm">{error}</div>}

          {/* Actions */}
          <div className="flex gap-3 pt-2">
            <Button
              type="button"
              onClick={handleClose}
              disabled={loading}
              className="flex-1 border-zinc-600 text-zinc-200 hover:bg-zinc-800 hover:text-white"
            >
              Cancel
            </Button>
            <Button
              type="submit"
              disabled={loading}
              className="flex-1 bg-[#ff8400] hover:bg-[#ff8400]/90 text-black font-semibold"
            >
              {loading ? (
                <>
                  <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                  Adding...
                </>
              ) : (
                'Add Contact'
              )}
            </Button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  );
}
