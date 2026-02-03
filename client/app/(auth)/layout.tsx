import { AuthProvider } from '@/context/AuthContext';

export default function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-(--color-hazard-dark)">{children}</div>
    </AuthProvider>
  );
}
