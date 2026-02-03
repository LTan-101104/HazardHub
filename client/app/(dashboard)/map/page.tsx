'use client';

import { useEffect, useState } from 'react';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';
import SignOutPopUp from '@/components/auth-component/SignOutPopUp';
import { LogOut } from 'lucide-react';

export default function Map() {
    const { user, loading, signOut } = useAuth();
    const router = useRouter();
    const [showSignOutPopup, setShowSignOutPopup] = useState(false);

    useEffect(() => {
        if (!loading && !user) {
            router.push('/sign-in');
        }
    }, [user, loading, router]);

    const handleSignOut = async () => {
        await signOut();
        router.push('/sign-in');
    };

    if (loading) {
        return <div className="min-h-screen bg-[#1a1a1a] flex items-center justify-center text-white">Loading...</div>;
    }

    if (!user) {
        return null;
    }

    return (
        <div className="min-h-screen bg-[#1a1a1a] text-white p-8">
            <div className="flex items-center justify-between mb-8">
                <div>
                    <h1 className="text-2xl font-bold">Map</h1>
                    <p className="text-gray-400">Welcome, {user.email}</p>
                </div>
                <button
                    onClick={() => setShowSignOutPopup(true)}
                    className="flex items-center gap-2 py-2 px-4 rounded-full bg-[#2a2a2a] text-white hover:bg-[#3a3a3a] transition-colors cursor-pointer"
                >
                    <LogOut size={18} />
                    Sign Out
                </button>
            </div>

            <SignOutPopUp
                isOpen={showSignOutPopup}
                onClose={() => setShowSignOutPopup(false)}
                onSignOut={handleSignOut}
            />
        </div>
    );
}