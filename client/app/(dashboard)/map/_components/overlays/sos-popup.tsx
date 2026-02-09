'use client';

import React, { useState, useEffect } from 'react';
import { Button } from '@/components/ui/button';
import { Checkbox } from '@/components/ui/checkbox';
import { Sheet, SheetContent, SheetTitle, SheetDescription } from '@/components/ui/sheet';
import {
  MapPin,
  Phone,
  Truck,
  Shield,
  Sparkles,
  X,
  Clock,
  MapPinOff,
  Plus,
  Trash2,
  ChevronLeft,
  ChevronRight,
  User,
} from 'lucide-react';
import { cn } from '@/lib/utils';
import { SOSEventDTO, SOSEventStatus, EmergencyContactDTO } from '@/types';
import type { LatLng } from '@/types/map';
import { auth } from '@/lib/firebase';
import { getEmergencyContactsByPriority } from '@/lib/actions/emergency-actions';

interface SOSPopupProps {
  isOpen: boolean;
  onClose: () => void;
  sosEvent?: SOSEventDTO | null;
  onCreateSOS?: () => void;
  onAddPin?: () => void;
  onRemovePin?: () => void;
  onClearAll?: () => void;
  selectedLocation?: LatLng | null;
  allLocations?: LatLng[];
  selectedIndex?: number | null;
  onSelectPin?: (index: number) => void;
  contactsShared?: number;
  estimatedArrival?: number; // in minutes
}

interface SafetyCheckItem {
  id: string;
  label: string;
  checked: boolean;
}

const defaultSafetyChecklist: SafetyCheckItem[] = [
  { id: '1', label: 'Stay in vehicle with doors locked', checked: false },
  { id: '2', label: 'Keep exhaust pipe clear', checked: false },
  { id: '3', label: 'Turn on hazard lights', checked: false },
];

const emergencyServices = [
  {
    id: 'emergency',
    icon: Shield,
    title: 'Emergency Services',
    subtitle: '911 - Police, Fire, Medical',
    number: '911',
    color: 'bg-red-500',
  },
  {
    id: 'towing',
    icon: Truck,
    title: 'Towing Service',
    subtitle: 'AAA - Nearest available',
    number: '1-800-222-4357',
    color: 'bg-orange-500',
  },
];

export function SOSPopup({
  isOpen,
  onClose,
  sosEvent,
  onAddPin,
  onRemovePin,
  onClearAll,
  selectedLocation,
  allLocations = [],
  selectedIndex,
  onSelectPin,
  contactsShared = 2,
  estimatedArrival = 8,
}: SOSPopupProps) {
  const [safetyChecklist, setSafetyChecklist] = useState<SafetyCheckItem[]>(defaultSafetyChecklist);
  const [emergencyContacts, setEmergencyContacts] = useState<EmergencyContactDTO[]>([]);
  const [loadingContacts, setLoadingContacts] = useState(false);

  // Fetch emergency contacts from safety profile when popup opens
  useEffect(() => {
    if (!isOpen) return;

    const fetchContacts = async () => {
      setLoadingContacts(true);
      try {
        const idToken = await auth?.currentUser?.getIdToken();
        if (!idToken) return;
        const contacts = await getEmergencyContactsByPriority(idToken);
        setEmergencyContacts(contacts);
      } catch (err) {
        console.error('Failed to load emergency contacts:', err);
      } finally {
        setLoadingContacts(false);
      }
    };

    fetchContacts();
  }, [isOpen]);

  // Format coordinates for display
  const formatCoordinate = (value: number, isLatitude: boolean): string => {
    const direction = isLatitude ? (value >= 0 ? 'N' : 'S') : value >= 0 ? 'E' : 'W';
    return `${Math.abs(value).toFixed(4)}° ${direction}`;
  };

  const latitude = selectedLocation?.lat ?? sosEvent?.latitude ?? 40.7128;
  const longitude = selectedLocation?.lng ?? sosEvent?.longitude ?? -74.006;
  const totalPins = allLocations.length;
  const currentPinNumber = selectedIndex !== null && selectedIndex !== undefined ? selectedIndex + 1 : 0;

  const handlePrevPin = () => {
    if (selectedIndex !== null && selectedIndex !== undefined && selectedIndex > 0 && onSelectPin) {
      onSelectPin(selectedIndex - 1);
    }
  };

  const handleNextPin = () => {
    if (selectedIndex !== null && selectedIndex !== undefined && selectedIndex < totalPins - 1 && onSelectPin) {
      onSelectPin(selectedIndex + 1);
    }
  };

  const toggleCheckItem = (id: string) => {
    setSafetyChecklist((prev) => prev.map((item) => (item.id === id ? { ...item, checked: !item.checked } : item)));
  };

  const handleCall = (number: string) => {
    window.open(`tel:${number}`, '_self');
  };

  // Derive broadcasting state from SOS status
  const isBroadcasting = !!sosEvent && sosEvent.status !== SOSEventStatus.RESOLVED;

  return (
    <Sheet open={isOpen} onOpenChange={(open) => !open && onClose()}>
      <SheetContent
        side="bottom"
        showCloseButton={false}
        className="h-[90vh] rounded-t-3xl border-t-0 bg-[#1A1A1A] p-0"
        onPointerDownOutside={(e) => e.preventDefault()}
        onInteractOutside={(e) => e.preventDefault()}
      >
        {/* Visually hidden title for accessibility */}
        <SheetTitle className="sr-only">SOS Emergency</SheetTitle>
        <SheetDescription className="sr-only">Emergency SOS panel with location sharing and emergency services</SheetDescription>

        {/* Close button */}
        <button
          onClick={onClose}
          className="absolute right-4 top-4 z-10 rounded-full p-2 text-white/70 transition-colors hover:bg-white/10 hover:text-white"
          aria-label="Close SOS popup"
        >
          <X className="size-5" />
        </button>

        {/* Scrollable content */}
        <div className="flex h-full flex-col overflow-y-auto">
          {/* Broadcasting Status Header */}
          <div className="flex flex-col items-center px-6 pt-6">
            <div
              className={cn(
                'flex items-center gap-2 rounded-full px-6 py-3 text-sm font-semibold tracking-wider',
                isBroadcasting ? 'bg-orange-600 text-white' : 'bg-gray-600 text-gray-300',
              )}
            >
              <span className={cn('size-2.5 rounded-full', isBroadcasting ? 'animate-pulse bg-green-400' : 'bg-gray-400')} />
              <span>{isBroadcasting ? 'BROADCASTING LOCATION' : 'BROADCAST ENDED'}</span>
            </div>

            {/* Help Arriving ETA */}
            {isBroadcasting && (
              <div className="mt-4 flex items-center gap-2 rounded-lg bg-[#2A2A2A] px-4 py-2">
                <Clock className="size-4 text-gray-400" />
                <span className="text-sm text-gray-400">Help Arriving</span>
                <span className="text-2xl font-bold text-white">~{estimatedArrival} min</span>
              </div>
            )}
          </div>

          {/* Pulsing Location Marker */}
          <div className="relative flex items-center justify-center py-12">
            {/* Outer pulse rings - animated */}
            <div className="absolute size-52 animate-sos-pulse rounded-full bg-orange-500/20" />
            <div className="absolute size-44 rounded-full bg-orange-500/25 animate-sos-ring" style={{ animationDelay: '0.2s' }} />
            <div className="absolute size-36 rounded-full bg-orange-500/30 animate-sos-ring" style={{ animationDelay: '0.4s' }} />

            {/* Static gradient rings */}
            <div className="absolute size-32 rounded-full bg-linear-to-b from-orange-500/40 to-orange-600/30" />
            <div className="absolute size-24 rounded-full bg-linear-to-b from-orange-500/50 to-orange-600/40" />
            <div className="absolute size-16 rounded-full bg-linear-to-b from-orange-500/60 to-orange-600/50" />

            {/* Center marker */}
            <div className="relative z-10 flex size-14 items-center justify-center rounded-full border-4 border-white/90 bg-transparent shadow-lg shadow-orange-500/20">
              <MapPin className="size-6 text-white drop-shadow-md" />
            </div>
          </div>

          {/* SOS Active Banner */}
          <div className="mx-4 flex items-center justify-between rounded-t-2xl bg-orange-600 px-4 py-4">
            <div className="flex items-center gap-3">
              <Shield className="size-6 text-white" />
              <span className="text-lg font-bold tracking-wider text-white">SOS ACTIVE</span>
            </div>
            <Button variant="secondary" size="sm" className="gap-2 rounded-full bg-[#2A2A2A] text-white hover:bg-[#3A3A3A]">
              <Sparkles className="size-4 text-blue-400" />
              AI Assistant
            </Button>
          </div>

          {/* Location Info */}
          <div className="mx-4 bg-[#2A2A2A] p-4">
            {/* Pin navigation header */}
            {totalPins > 0 && (
              <div className="mb-3 flex items-center justify-between">
                <div className="flex items-center gap-2">
                  <span className="text-xs font-semibold tracking-wider text-gray-400">SOS LOCATIONS</span>
                  <span className="rounded-full bg-red-500/20 px-2 py-0.5 text-xs font-medium text-red-400">
                    {totalPins} pin{totalPins !== 1 ? 's' : ''}
                  </span>
                </div>
                {totalPins > 1 && (
                  <div className="flex items-center gap-1">
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        handlePrevPin();
                      }}
                      disabled={selectedIndex === 0}
                      className="size-7 rounded-full p-0 text-gray-400 hover:bg-[#3A3A3A] hover:text-white disabled:opacity-30"
                    >
                      <ChevronLeft className="size-4" />
                    </Button>
                    <span className="min-w-12 text-center text-sm text-gray-300">
                      {currentPinNumber} / {totalPins}
                    </span>
                    <Button
                      variant="ghost"
                      size="sm"
                      onClick={(e) => {
                        e.preventDefault();
                        e.stopPropagation();
                        handleNextPin();
                      }}
                      disabled={selectedIndex === totalPins - 1}
                      className="size-7 rounded-full p-0 text-gray-400 hover:bg-[#3A3A3A] hover:text-white disabled:opacity-30"
                    >
                      <ChevronRight className="size-4" />
                    </Button>
                  </div>
                )}
              </div>
            )}

            <div className="flex items-center gap-2">
              <span className="size-2 rounded-full bg-green-500" />
              <span className="text-sm text-green-400">Location shared with {contactsShared} contacts</span>
            </div>
            <div className="mt-2 flex items-center justify-between">
              <div className="flex items-center gap-2 text-sm text-gray-400">
                <MapPin className="size-4" />
                <span className="font-mono">
                  {formatCoordinate(latitude, true)}, {formatCoordinate(longitude, false)}
                </span>
              </div>
            </div>

            {/* Pin action buttons */}
            <div className="mt-3 flex items-center gap-2">
              <Button
                variant="ghost"
                size="sm"
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  if (onAddPin) {
                    onAddPin();
                  }
                }}
                className="h-8 flex-1 gap-1.5 rounded-full bg-[#1A1A1A] text-xs text-gray-300 hover:bg-green-900/50 hover:text-green-400"
              >
                <Plus className="size-3.5" />
                Add Pin
              </Button>
              {totalPins > 0 && (
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (onRemovePin) {
                      onRemovePin();
                    }
                  }}
                  className="h-8 flex-1 gap-1.5 rounded-full bg-[#1A1A1A] text-xs text-gray-300 hover:bg-red-900/50 hover:text-red-400"
                >
                  <MapPinOff className="size-3.5" />
                  Remove Pin
                </Button>
              )}
              {totalPins > 1 && (
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={(e) => {
                    e.preventDefault();
                    e.stopPropagation();
                    if (onClearAll) {
                      onClearAll();
                    }
                  }}
                  className="h-8 flex-1 gap-1.5 rounded-full bg-[#1A1A1A] text-xs text-gray-300 hover:bg-red-900/50 hover:text-red-400"
                >
                  <Trash2 className="size-3.5" />
                  Clear All
                </Button>
              )}
            </div>
          </div>

          {/* AI Safety Checklist */}
          <div className="mx-4 mt-4 rounded-2xl bg-[#2A2A2A] p-4">
            <div className="mb-4 flex items-center justify-between">
              <span className="text-xs font-semibold tracking-wider text-gray-400">AI SAFETY CHECKLIST</span>
              <div className="flex items-center gap-1.5 rounded-full bg-[#1A1A1A] px-3 py-1">
                <Sparkles className="size-3 text-blue-400" />
                <span className="text-xs text-gray-400">Gemini</span>
              </div>
            </div>

            <div className="space-y-3">
              {safetyChecklist.map((item) => (
                <div
                  key={item.id}
                  className={cn(
                    'flex items-center gap-3 rounded-lg p-3 transition-colors',
                    item.checked ? 'bg-[#1A1A1A]' : 'bg-[#232323] hover:bg-[#282828]',
                  )}
                  onClick={() => toggleCheckItem(item.id)}
                  role="button"
                  tabIndex={0}
                  onKeyDown={(e) => e.key === 'Enter' && toggleCheckItem(item.id)}
                >
                  <Checkbox
                    checked={item.checked}
                    className={cn(
                      'size-5 rounded border-2 pointer-events-none',
                      item.checked ? 'border-green-500 bg-green-500 data-[state=checked]:bg-green-500' : 'border-gray-500',
                    )}
                  />
                  <span className={cn('text-sm', item.checked ? 'text-gray-400' : 'text-white')}>{item.label}</span>
                </div>
              ))}
            </div>
          </div>

          {/* Emergency Services */}
          <div className="mx-4 mt-4">
            <span className="mb-4 block text-xs font-semibold tracking-wider text-gray-400">EMERGENCY SERVICES</span>

            <div className="space-y-3">
              {emergencyServices.map((service) => (
                <div key={service.id} className="flex items-center justify-between rounded-2xl bg-[#2A2A2A] p-4">
                  <div className="flex items-center gap-3">
                    <div
                      className={cn(
                        'flex size-12 items-center justify-center rounded-xl',
                        service.id === 'emergency' ? 'bg-red-900/50' : 'bg-orange-900/50',
                      )}
                    >
                      <service.icon className={cn('size-6', service.id === 'emergency' ? 'text-red-400' : 'text-orange-400')} />
                    </div>
                    <div>
                      <span className="block font-medium text-white">{service.title}</span>
                      <span className="text-sm text-gray-400">{service.subtitle}</span>
                    </div>
                  </div>
                  <Button
                    onClick={() => handleCall(service.number)}
                    className={cn(
                      'size-12 rounded-full p-0',
                      service.id === 'emergency' ? 'bg-red-500 hover:bg-red-600' : 'bg-orange-500 hover:bg-orange-600',
                    )}
                  >
                    <Phone className="size-5 text-white" />
                  </Button>
                </div>
              ))}
            </div>
          </div>

          {/* Emergency Contacts from Safety Profile */}
          <div className="mx-4 mt-4 mb-8">
            <span className="mb-4 block text-xs font-semibold tracking-wider text-gray-400">YOUR EMERGENCY CONTACTS</span>

            {loadingContacts ? (
              <div className="flex items-center justify-center rounded-2xl bg-[#2A2A2A] p-6">
                <span className="text-sm text-gray-400">Loading contacts...</span>
              </div>
            ) : emergencyContacts.length === 0 ? (
              <div className="flex flex-col items-center gap-2 rounded-2xl bg-[#2A2A2A] p-6">
                <User className="size-8 text-gray-500" />
                <span className="text-sm text-gray-400">No emergency contacts added</span>
                <span className="text-xs text-gray-500">Add contacts in your Safety Profile</span>
              </div>
            ) : (
              <div className="space-y-3">
                {emergencyContacts.map((contact) => (
                  <div key={contact.id} className="flex items-center justify-between rounded-2xl bg-[#2A2A2A] p-4">
                    <div className="flex items-center gap-3">
                      <div className="flex size-12 items-center justify-center rounded-xl bg-blue-900/50">
                        <span className="text-sm font-bold text-blue-400">
                          {contact.name
                            .split(' ')
                            .map((n) => n[0])
                            .join('')
                            .toUpperCase()
                            .slice(0, 2)}
                        </span>
                      </div>
                      <div>
                        <span className="block font-medium text-white">{contact.name}</span>
                        <span className="text-sm text-gray-400">
                          {contact.relationship || 'Emergency Contact'} {contact.priority && contact.priority >= 100 ? ' · Primary' : ''}
                        </span>
                      </div>
                    </div>
                    <Button
                      onClick={() => handleCall(contact.phone)}
                      className="size-12 rounded-full bg-blue-500 p-0 hover:bg-blue-600"
                    >
                      <Phone className="size-5 text-white" />
                    </Button>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      </SheetContent>
    </Sheet>
  );
}
