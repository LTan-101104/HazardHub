'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import { auth } from '@/lib/firebase';
import { getSavedLocationsByUserId } from '@/lib/actions/saved_location-actions';
import { getEmergencyContactsByPriority, deleteEmergencyContact, formatPhoneNumber } from '@/lib/actions/emergency-actions';
import type { SavedLocationDTO, EmergencyContactDTO } from '@/types';
import { Home, Briefcase, Shield, Phone, Plus, MapPin, Loader2 } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';
import AddLocationModal from './_components/AddLocationModal';
import EditLocationModal from './_components/EditLocationModal';
import DeleteLocationModal from './_components/DeleteLocationModal';
import AddContactModal from './_components/AddContactModal';

// Icon mapping for location types
const LOCATION_ICONS = {
  home: {
    icon: Home,
    color: 'text-orange-500',
    bg: 'bg-orange-500/20',
    badge: 'Primary',
    badgeColor: 'bg-green-900 text-green-100',
  },
  work: {
    icon: Briefcase,
    color: 'text-violet-500',
    bg: 'bg-violet-500/20',
    badge: 'Secondary',
    badgeColor: 'bg-zinc-800 text-zinc-200',
  },
  emergency: {
    icon: Shield,
    color: 'text-red-500',
    bg: 'bg-red-500/20',
    badge: 'Emergency',
    badgeColor: 'bg-red-900 text-red-100',
  },
  default: {
    icon: MapPin,
    color: 'text-blue-500',
    bg: 'bg-blue-500/20',
    badge: 'Location',
    badgeColor: 'bg-zinc-800 text-zinc-200',
  },
};

function getLocationIcon(name: string) {
  const lowerName = name.toLowerCase();
  if (lowerName.includes('home') || lowerName.includes('house')) return LOCATION_ICONS.home;
  if (lowerName.includes('work') || lowerName.includes('office')) return LOCATION_ICONS.work;
  if (lowerName.includes('emergency') || lowerName.includes('shelter')) return LOCATION_ICONS.emergency;
  return LOCATION_ICONS.default;
}

function getInitials(name: string): string {
  return name
    .split(' ')
    .map((n) => n[0])
    .join('')
    .toUpperCase()
    .slice(0, 2);
}

export default function SafetyProfile() {
  const router = useRouter();
  const { user, loading: authLoading } = useAuth();
  const [locations, setLocations] = useState<SavedLocationDTO[]>([]);
  const [contacts, setContacts] = useState<EmergencyContactDTO[]>([]);
  const [loadingLocations, setLoadingLocations] = useState(false);
  const [loadingContacts, setLoadingContacts] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [showAddLocationModal, setShowAddLocationModal] = useState(false);
  const [showAddContactModal, setShowAddContactModal] = useState(false);
  const [showEditLocationModal, setShowEditLocationModal] = useState(false);
  const [showDeleteLocationModal, setShowDeleteLocationModal] = useState(false);
  const [selectedLocation, setSelectedLocation] = useState<SavedLocationDTO | null>(null);

  // Fetch saved locations
  useEffect(() => {
    const fetchLocations = async () => {
      if (!user) return;
      setLoadingLocations(true);
      setError(null);
      try {
        const idToken = await auth?.currentUser?.getIdToken();
        if (!idToken) throw new Error('Not authenticated');

        const data = await getSavedLocationsByUserId(idToken, user.id);
        setLocations(data);
      } catch (e: any) {
        console.error('Failed to load locations:', e);
        setError(e.message || 'Failed to load locations');
      } finally {
        setLoadingLocations(false);
      }
    };

    if (user && !authLoading) {
      fetchLocations();
    }
  }, [user, authLoading]);

  // Fetch emergency contacts
  useEffect(() => {
    const fetchContacts = async () => {
      if (!user) return;
      setLoadingContacts(true);
      try {
        const idToken = await auth?.currentUser?.getIdToken();
        if (!idToken) throw new Error('Not authenticated');

        const data = await getEmergencyContactsByPriority(idToken);
        setContacts(data);
      } catch (e: any) {
        console.error('Failed to load contacts:', e);
      } finally {
        setLoadingContacts(false);
      }
    };

    if (user && !authLoading) {
      fetchContacts();
    }
  }, [user, authLoading]);

  const handleDeleteContact = async (contactId: string) => {
    if (!confirm('Are you sure you want to delete this contact?')) return;

    try {
      const idToken = await auth?.currentUser?.getIdToken();
      if (!idToken) return;

      await deleteEmergencyContact(idToken, contactId);
      setContacts(contacts.filter((c) => c.id !== contactId));
    } catch (e: any) {
      console.error('Failed to delete contact:', e);
      alert('Failed to delete contact');
    }
  };

  const handleCallContact = (phone: string) => {
    window.location.href = `tel:${phone}`;
  };

  const handleEditLocation = (location: SavedLocationDTO) => {
    setSelectedLocation(location);
    setShowEditLocationModal(true);
  };

  const handleNavigateToLocation = (location: SavedLocationDTO) => {
    const params = new URLSearchParams({
      lat: location.latitude.toString(),
      lng: location.longitude.toString(),
    });
    if (location.name) {
      params.set('name', location.name);
    }
    router.push(`/map?${params.toString()}`);
  };

  const handleDeleteLocation = (location: SavedLocationDTO) => {
    setSelectedLocation(location);
    setShowDeleteLocationModal(true);
  };

  const handleLocationAdded = (newLocation: SavedLocationDTO) => {
    setLocations((prev) => [...prev, newLocation]);
    setShowAddLocationModal(false);
  };

  const handleLocationUpdated = (updatedLocation: SavedLocationDTO) => {
    setLocations((prev) => prev.map((loc) => (loc.id === updatedLocation.id ? updatedLocation : loc)));
    setShowEditLocationModal(false);
    setSelectedLocation(null);
  };

  const handleLocationDeleted = (locationId: string) => {
    setLocations((prev) => prev.filter((loc) => loc.id !== locationId));
    setShowDeleteLocationModal(false);
    setSelectedLocation(null);
  };

  const handleContactAdded = (newContact: EmergencyContactDTO) => {
    setContacts((prev) => [...prev, newContact].sort((a, b) => (b.priority || 1) - (a.priority || 1)));
    setShowAddContactModal(false);
  };

  if (authLoading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <Loader2 className="w-8 h-8 animate-spin text-[#ff8400]" />
      </div>
    );
  }

  if (!user) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <p className="text-zinc-400">Please sign in to view your safety profile</p>
      </div>
    );
  }

  return (
    <div className="max-w-screen-2xl mx-auto px-4 sm:px-6 py-6">
      {/* Header */}
      <div className="flex flex-col gap-4 sm:gap-0 sm:flex-row sm:items-center sm:justify-between mb-8">
        <div>
          <h1 className="font-mono text-3xl font-bold tracking-tight text-white">Safety Profile</h1>
          <p className="text-zinc-400 text-sm mt-1">Manage your saved locations for quick access during emergencies</p>
        </div>
        <Button
          onClick={() => setShowAddLocationModal(true)}
          className="bg-[#ff8400] hover:bg-[#ff8400]/90 text-black font-semibold rounded-full px-6 py-2"
          size="lg"
        >
          <Plus className="w-5 h-5 mr-2" />
          Add Location
        </Button>
      </div>

      {/* Error Message */}
      {error && <div className="mb-6 p-4 bg-red-500/10 border border-red-500/50 rounded-lg text-red-400">{error}</div>}

      {/* Saved Locations */}
      <div className="mb-12">
        <h2 className="font-mono text-xl font-semibold mb-6 text-white">Saved Locations</h2>

        {loadingLocations ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-[#ff8400]" />
          </div>
        ) : locations.length === 0 ? (
          <div className="text-center py-12 bg-zinc-900 rounded-xl border border-zinc-800">
            <MapPin className="w-12 h-12 text-zinc-600 mx-auto mb-4" />
            <p className="text-zinc-400 mb-4">No saved locations yet</p>
            <Button
              onClick={() => setShowAddLocationModal(true)}
              className="bg-[#ff8400] hover:bg-[#ff8400]/90 text-black font-semibold rounded-full"
              size="lg"
            >
              <Plus className="w-5 h-5 mr-2" />
              Add Your First Location
            </Button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {locations.map((location) => {
              const iconConfig = getLocationIcon(location.name);
              const Icon = iconConfig.icon;

              return (
                <Card key={location.id} className="bg-zinc-900 border-zinc-800 hover:border-zinc-700 transition-colors">
                  <CardContent className="p-6">
                    {/* Icon and Title */}
                    <div className="flex items-start gap-4 mb-4">
                      <div className={`rounded-xl p-3 ${iconConfig.bg}`}>
                        <Icon className={`w-6 h-6 ${iconConfig.color}`} />
                      </div>
                      <div className="flex-1 min-w-0">
                        <h3 className="font-mono text-lg font-semibold text-white mb-1">{location.name}</h3>
                        <Badge className={`${iconConfig.badgeColor} text-xs`}>{iconConfig.badge}</Badge>
                      </div>
                      {/* Delete Icon */}
                      <Button
                        size="icon"
                        variant="ghost"
                        onClick={() => handleDeleteLocation(location)}
                        className="text-zinc-500 hover:text-red-400 hover:bg-red-500/10 rounded-full h-8 w-8"
                      >
                        <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path
                            strokeLinecap="round"
                            strokeLinejoin="round"
                            strokeWidth={2}
                            d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                          />
                        </svg>
                      </Button>
                    </div>

                    {/* Address */}
                    <div className="flex items-start gap-2 text-zinc-400 text-sm mb-4">
                      <MapPin className="w-4 h-4 mt-0.5 flex-shrink-0" />
                      <span className="line-clamp-2">{location.address || 'No address provided'}</span>
                    </div>

                    {/* Coordinates */}
                    <div className="text-xs text-zinc-500 mb-4 font-mono">
                      {location.latitude.toFixed(4)}° N, {location.longitude.toFixed(4)}° W
                    </div>

                    {/* Actions */}
                    <div className="flex gap-2">
                      <Button
                        onClick={() => handleEditLocation(location)}
                        // variant="ghost"
                        className="flex-1 border-zinc-600 text-zinc-200 hover:bg-zinc-800 hover:text-white hover:border-zinc-500"
                        size="sm"
                      >
                        Edit
                      </Button>
                      <Button
                        onClick={() => handleNavigateToLocation(location)}
                        className="flex-1 bg-[#ff8400] hover:bg-[#ff8400]/90 text-black font-semibold"
                        size="sm"
                      >
                        Navigate
                      </Button>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        )}
      </div>

      {/* Emergency Contacts */}
      <div>
        <div className="flex items-center justify-between mb-6">
          <h2 className="font-mono text-xl font-semibold text-white">Emergency Contacts</h2>
          <Button
            onClick={() => setShowAddContactModal(true)}
            // variant="outline"
            className="rounded-full px-4 py-2 border-zinc-600 text-zinc-200 hover:bg-zinc-800 hover:text-white hover:border-zinc-500"
          >
            <Plus className="w-4 h-4 mr-2" />
            Add Contact
          </Button>
        </div>

        {loadingContacts ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-8 h-8 animate-spin text-[#ff8400]" />
          </div>
        ) : contacts.length === 0 ? (
          <div className="text-center py-12 bg-zinc-900 rounded-xl border border-zinc-800">
            <Phone className="w-12 h-12 text-zinc-600 mx-auto mb-4" />
            <p className="text-zinc-400 mb-4">No emergency contacts yet</p>
            <Button
              onClick={() => setShowAddContactModal(true)}
              className="bg-[#ff8400] hover:bg-[#ff8400]/90 text-black font-semibold rounded-full"
              size="lg"
            >
              <Plus className="w-5 h-5 mr-2" />
              Add Your First Contact
            </Button>
          </div>
        ) : (
          <div className="space-y-3">
            {contacts.map((contact, index) => {
              const initials = getInitials(contact.name);
              const isPrimary = index === 0;

              return (
                <div key={contact.id} className="flex items-center bg-zinc-900 rounded-xl px-4 py-4">
                  {/* Avatar */}
                  <Avatar className="h-12 w-12 mr-4">
                    <AvatarFallback
                      className={`font-bold text-sm ${isPrimary ? 'bg-[#ff8400] text-black' : 'bg-zinc-700 text-white'}`}
                    >
                      {initials}
                    </AvatarFallback>
                  </Avatar>

                  {/* Contact Info */}
                  <div className="flex-1 min-w-0">
                    <div className="flex items-center gap-2 mb-1">
                      <h3 className="font-mono font-semibold text-white">{contact.name}</h3>
                      {isPrimary && <Badge className="bg-[#ff8400]/20 text-[#ff8400] text-xs border-0">Primary</Badge>}
                    </div>
                    <p className="text-zinc-400 text-sm truncate">
                      {contact.relationship && `${contact.relationship} • `}
                      {formatPhoneNumber(contact.phone)}
                    </p>
                  </div>

                  {/* Actions */}
                  <div className="flex items-center gap-2 ml-4">
                    <Button
                      size="icon"
                      className={`rounded-full ${
                        isPrimary ? 'bg-[#ff8400] hover:bg-[#ff8400]/90 text-black' : 'bg-zinc-700 hover:bg-zinc-600 text-white'
                      }`}
                      onClick={() => handleCallContact(contact.phone)}
                    >
                      <Phone className="w-4 h-4" />
                    </Button>

                    <Button
                      size="icon"
                      variant="ghost"
                      className="text-zinc-400 hover:text-red-400 hover:bg-red-500/10 rounded-full"
                      onClick={() => handleDeleteContact(contact.id!)}
                    >
                      <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16"
                        />
                      </svg>
                    </Button>
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>

      {/* Modals */}
      <AddLocationModal
        open={showAddLocationModal}
        onOpenChange={setShowAddLocationModal}
        onLocationAdded={handleLocationAdded}
      />

      <EditLocationModal
        open={showEditLocationModal}
        onOpenChange={setShowEditLocationModal}
        location={selectedLocation}
        onLocationUpdated={handleLocationUpdated}
      />

      <DeleteLocationModal
        open={showDeleteLocationModal}
        onOpenChange={setShowDeleteLocationModal}
        location={selectedLocation}
        onLocationDeleted={handleLocationDeleted}
      />

      <AddContactModal open={showAddContactModal} onOpenChange={setShowAddContactModal} onContactAdded={handleContactAdded} />
    </div>
  );
}
