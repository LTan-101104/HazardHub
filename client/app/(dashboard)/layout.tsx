'use client';

import { AuthProvider } from '@/context/AuthContext';
import { Navbar } from '@/components/navbar/Navbar';

export default function DashboardLayout({
    children,
}: {
    children: React.ReactNode;
}) {
    return (
        <AuthProvider>
            <Navbar>{children}</Navbar>
        </AuthProvider>
    );
}
