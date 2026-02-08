"use client";

import { useEffect, useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { getSOSEventsByUserId } from '@/lib/actions/sos-action';
import type { SOSEventDTO } from '@/types';
import { LucideHome, LucideBriefcase, LucideShield, LucidePhone, LucidePlus } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Avatar, AvatarFallback } from '@/components/ui/avatar';


const contacts = [
    {
        initials: 'JS',
        name: 'Jane Smith',
        relation: 'Spouse',
        phone: '(555) 123-4567',
        icon: <LucidePhone className="text-zinc-900" />,
        iconBg: 'bg-orange-500',
        action: 'call',
    },
    {
        initials: 'MD',
        name: 'Mike Doe',
        relation: 'Brother',
        phone: '(555) 987-6543',
        icon: <LucidePlus className="text-zinc-900" />,
        iconBg: 'bg-zinc-800',
        action: 'add',
    },
];


export default function SafetyProfile() {
    const { user, loading } = useAuth();
    const [locations, setLocations] = useState<SOSEventDTO[]>([]);
    const [fetching, setFetching] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchLocations = async () => {
            if (!user) return;
            setFetching(true);
            setError(null);
            try {
                // Assume user.id is the userId for SOS events
                const idToken = (window as any).auth?.currentUser
                    ? await (window as any).auth.currentUser.getIdToken()
                    : undefined;
                // If not using window.auth, you may need to get idToken from your auth context
                if (!idToken) throw new Error('Not authenticated');
                const data = await getSOSEventsByUserId(idToken, user.id);
                setLocations(data);
            } catch (e: any) {
                setError(e.message || 'Failed to load locations');
            } finally {
                setFetching(false);
            }
        };
        if (user && !loading) fetchLocations();
    }, [user, loading]);

    return (
        <div className="max-w-screen-2xl mx-auto px-2 sm:px-6 py-6">
            <div className="flex flex-col gap-2 sm:gap-0 sm:flex-row sm:items-center sm:justify-between mb-6">
                <div>
                    <h1 className="font-mono text-3xl font-bold tracking-tight">Safety Profile</h1>
                    <p className="text-muted-foreground text-sm mt-1">Manage your saved locations for quick access during emergencies</p>
                </div>
                <Button className="bg-orange-500 hover:bg-orange-600 text-black font-semibold rounded-full px-6 py-2 mt-4 sm:mt-0" size="lg">
                    + Add Location
                </Button>
            </div>

            {/* Saved Locations */}
            <div className="mb-8">
                <h2 className="font-mono text-lg font-semibold mb-4">Saved Locations</h2>
                {fetching ? (
                    <div className="text-zinc-400">Loading...</div>
                ) : error ? (
                    <div className="text-red-500">{error}</div>
                ) : (
                    <div className="flex flex-col gap-4 sm:flex-row sm:gap-6">
                        {locations.length === 0 ? (
                            <div className="text-zinc-400">No saved locations found.</div>
                        ) : (
                            locations.map((loc, i) => {
                                // Pick icon and badge based on status/type if needed
                                let icon = <LucideHome className="text-orange-500" />;
                                let badge = 'Primary';
                                let badgeColor = 'bg-green-900 text-green-100';
                                let iconBg = 'bg-orange-500/20';
                                if (loc.status === 'RESOLVED') {
                                    icon = <LucideBriefcase className="text-violet-500" />;
                                    badge = 'Secondary';
                                    badgeColor = 'bg-zinc-800 text-zinc-200';
                                    iconBg = 'bg-violet-500/20';
                                } else if (loc.status === 'HELP_ARRIVING') {
                                    icon = <LucideShield className="text-orange-900" />;
                                    badge = 'Emergency';
                                    badgeColor = 'bg-orange-900 text-orange-100';
                                    iconBg = 'bg-orange-900/20';
                                }
                                return (
                                    <Card key={loc.id || i} className="flex-1 min-w-0 bg-zinc-900 border-zinc-800">
                                        <CardContent className="flex flex-col gap-2">
                                            <div className="flex items-center gap-3 mb-1">
                                                <div className={`rounded-xl p-2 ${iconBg}`}>{icon}</div>
                                                <div className="flex flex-col gap-0.5">
                                                    <span className="font-mono text-lg font-semibold text-white leading-tight">{loc.status || 'Location'}</span>
                                                    <Badge className={`mt-0.5 ${badgeColor}`}>{badge}</Badge>
                                                </div>
                                            </div>
                                            <div className="flex items-center gap-2 text-zinc-300 text-sm">
                                                <span className="inline-block"><svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24"><path d="M17.657 16.657L13.414 20.9a2 2 0 0 1-2.828 0l-4.243-4.243a8 8 0 1 1 11.314 0z" /><circle cx="12" cy="11" r="3" /></svg></span>
                                                <span>
                                                    {loc.latitude && loc.longitude
                                                        ? `${loc.latitude.toFixed(4)}° N, ${loc.longitude.toFixed(4)}° W`
                                                        : 'No address'}
                                                </span>
                                            </div>
                                            <div className="flex gap-2 mt-2">
                                                <Button variant="outline" className="flex-1 border-zinc-700 text-white">Edit</Button>
                                                <Button className="flex-1 bg-orange-500 hover:bg-orange-600 text-black font-semibold">Navigate</Button>
                                            </div>
                                        </CardContent>
                                    </Card>
                                );
                            })
                        )}
                    </div>
                )}
            </div>

            {/* Emergency Contacts */}
            <div>
                <div className="flex items-center justify-between mb-2">
                    <h2 className="font-mono text-lg font-semibold">Emergency Contacts</h2>
                    <Button variant="outline" className="rounded-lg px-4 py-1 border-zinc-700 text-white">Add Contact</Button>
                </div>
                <div className="flex flex-col gap-2">
                    {contacts.map((c, i) => (
                        <div key={i} className="flex items-center bg-zinc-900 rounded-xl px-4 py-3">
                            <Avatar className="mr-3" size="default">
                                <AvatarFallback className={`font-bold ${i === 0 ? 'bg-orange-500 text-black' : 'bg-zinc-800 text-white'}`}>{c.initials}</AvatarFallback>
                            </Avatar>
                            <div className="flex-1 min-w-0">
                                <div className="font-mono font-semibold text-white leading-tight">{c.name}</div>
                                <div className="text-zinc-400 text-sm truncate">{c.relation} • {c.phone}</div>
                            </div>
                            <Button size="icon" className={`ml-2 ${i === 0 ? 'bg-orange-500 hover:bg-orange-600 text-black' : 'bg-zinc-800 text-white'}`}>
                                {c.icon}
                            </Button>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}
