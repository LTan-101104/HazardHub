'use client';

import { useState } from 'react';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import {
  Map,
  MessageSquare,
  AlertTriangle,
  Shield,
  Activity,
  Clock,
  Settings,
  HelpCircle,
  LogOut,
  User,
  X,
  Menu,
} from 'lucide-react';

import { cn } from '@/lib/utils';
import { useAuth } from '@/context/AuthContext';
import { Button } from '@/components/ui/button';
import { Separator } from '@/components/ui/separator';
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Sheet, SheetContent, SheetHeader, SheetTitle } from '@/components/ui/sheet';

// Navigation items configuration - public routes
const navigationItems = [
  {
    title: 'Navigation',
    icon: Map,
    href: '/map',
    requiresAuth: false,
  },
  // {
  //   title: 'AI Assistant',
  //   icon: MessageSquare,
  //   href: '/assistant',
  //   requiresAuth: true,
  // },
  {
    title: 'Report Hazard',
    icon: AlertTriangle,
    href: '/report-hazard',
    requiresAuth: true,
  },
];

// Dashboard items - all require auth
const dashboardItems = [
  {
    title: 'Safety Profile',
    icon: Shield,
    href: '/safety-profile',
    requiresAuth: true,
  },
  // {
  //   title: 'Hazard Tracker',
  //   icon: Activity,
  //   href: '/hazard-tracker',
  //   requiresAuth: true,
  // },
  // {
  //   title: 'Trip History',
  //   icon: Clock,
  //   href: '/trip-history',
  //   requiresAuth: true,
  // },
];

// Settings items
// const settingsItems = [
//   {
//     title: 'Settings',
//     icon: Settings,
//     href: '/settings',
//     requiresAuth: true,
//   },
//   {
//     title: 'Help & Support',
//     icon: HelpCircle,
//     href: '/help',
//     requiresAuth: false,
//   },
// ];

// Logo component
function SafeRouteLogo() {
  return (
    <div className="flex items-center gap-3">
      <Image src="/logo.png" alt="HazardHub logo" width={40} height={40} className="rounded-lg" />
      <span className="text-xl font-semibold">HazardHub</span>
    </div>
  );
}

// Navigation content shared between desktop and mobile
function NavbarContent({ onNavClick }: { onNavClick?: () => void }) {
  const pathname = usePathname();
  const router = useRouter();
  const { user, signOut } = useAuth();

  const handleSignOut = async () => {
    if (signOut) {
      await signOut();
      onNavClick?.();
    }
  };

  const handleNavigation = (href: string, requiresAuth: boolean) => {
    onNavClick?.();
    if (requiresAuth && !user) {
      router.push(`/sign-in?redirect=${encodeURIComponent(href)}`);
    } else {
      router.push(href);
    }
  };

  const getInitials = (name: string) => {
    return name
      .split(' ')
      .map((n) => n[0])
      .join('')
      .toUpperCase()
      .slice(0, 2);
  };

  return (
    <div className="flex h-full flex-col">
      <ScrollArea className="flex-1 px-3">
        {/* Navigation Section */}
        <div className="py-2">
          <h3 className="mb-2 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">Navigation</h3>
          <div className="space-y-1">
            {navigationItems.map((item) => (
              <button
                key={item.href}
                onClick={() => handleNavigation(item.href, item.requiresAuth)}
                className={cn(
                  'flex w-full items-center gap-3 rounded-md px-2 py-2.5 text-sm font-medium transition-colors',
                  'hover:bg-accent hover:text-accent-foreground',
                  pathname === item.href ? 'bg-accent text-accent-foreground' : 'text-muted-foreground',
                )}
              >
                <item.icon className="h-5 w-5" />
                <span>{item.title}</span>
              </button>
            ))}
          </div>
        </div>

        <Separator className="my-2" />

        {/* Dashboard Section */}
        <div className="py-2">
          <h3 className="mb-2 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">Dashboard</h3>
          <div className="space-y-1">
            {dashboardItems.map((item) => (
              <button
                key={item.href}
                onClick={() => handleNavigation(item.href, item.requiresAuth)}
                className={cn(
                  'flex w-full items-center gap-3 rounded-md px-2 py-2.5 text-sm font-medium transition-colors',
                  'hover:bg-accent hover:text-accent-foreground',
                  pathname === item.href ? 'bg-accent text-accent-foreground' : 'text-muted-foreground',
                )}
              >
                <item.icon className="h-5 w-5" />
                <span>{item.title}</span>
              </button>
            ))}
          </div>
        </div>

        <Separator className="my-2" />

        {/* Settings Section */}
        {/*}
        <div className="py-2">
          <h3 className="mb-2 px-2 text-xs font-medium text-muted-foreground uppercase tracking-wider">Settings</h3>
          <div className="space-y-1">
            {settingsItems.map((item) => (
              <button
                key={item.href}
                onClick={() => handleNavigation(item.href, item.requiresAuth)}
                className={cn(
                  'flex w-full items-center gap-3 rounded-md px-2 py-2.5 text-sm font-medium transition-colors',
                  'hover:bg-accent hover:text-accent-foreground',
                  pathname === item.href ? 'bg-accent text-accent-foreground' : 'text-muted-foreground',
                )}
              >
                <item.icon className="h-5 w-5" />
                <span>{item.title}</span>
              </button>
            ))}
          </div>
        </div> */}
      </ScrollArea>

      {/* Footer with user info */}
      <div className="border-t p-4">
        {user ? (
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Avatar>
                <AvatarImage src={undefined} alt={user.displayName} />
                <AvatarFallback className="bg-muted">
                  {user.displayName ? getInitials(user.displayName) : <User className="h-4 w-4" />}
                </AvatarFallback>
              </Avatar>
              <div className="flex flex-col">
                <span className="text-sm font-medium">{user.displayName}</span>
                <span className="text-xs text-muted-foreground">{user.email}</span>
              </div>
            </div>
            <Button variant="ghost" size="icon" onClick={handleSignOut} className="h-9 w-9">
              <LogOut className="h-5 w-5" />
              <span className="sr-only">Sign out</span>
            </Button>
          </div>
        ) : (
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <Avatar>
                <AvatarFallback className="bg-muted">
                  <User className="h-4 w-4" />
                </AvatarFallback>
              </Avatar>
              <div className="flex flex-col">
                <span className="text-sm font-medium">Guest</span>
                <span className="text-xs text-muted-foreground">Not signed in</span>
              </div>
            </div>
            <Button variant="ghost" size="sm" asChild>
              <Link href="/sign-in">Sign In</Link>
            </Button>
          </div>
        )}
      </div>
    </div>
  );
}

// Mobile Sheet Navbar
function MobileNavbar({ open, onOpenChange }: { open: boolean; onOpenChange: (open: boolean) => void }) {
  return (
    <Sheet open={open} onOpenChange={onOpenChange}>
      <SheetContent side="left" className="w-72 p-0" showCloseButton={false}>
        <SheetHeader className="p-4 border-b">
          <div className="flex items-center justify-between">
            <SafeRouteLogo />
            <Button variant="ghost" size="icon" onClick={() => onOpenChange(false)}>
              <X className="h-5 w-5" />
              <span className="sr-only">Close</span>
            </Button>
          </div>
          <SheetTitle className="sr-only">Navigation Menu</SheetTitle>
        </SheetHeader>
        <NavbarContent onNavClick={() => onOpenChange(false)} />
      </SheetContent>
    </Sheet>
  );
}

// Desktop Sidebar - always visible
function DesktopSidebar() {
  return (
    <aside className="hidden md:flex h-screen w-64 flex-col border-r bg-sidebar">
      <div className="p-4 border-b">
        <SafeRouteLogo />
      </div>
      <NavbarContent />
    </aside>
  );
}

// Mobile header with menu trigger
function MobileHeader({ onMenuClick }: { onMenuClick: () => void }) {
  return (
    <header className="flex md:hidden h-14 items-center gap-4 border-b px-4 bg-background">
      <Button variant="ghost" size="icon" onClick={onMenuClick}>
        <Menu className="h-5 w-5" />
        <span className="sr-only">Open menu</span>
      </Button>
      <span className="font-semibold">HazardHub</span>
    </header>
  );
}

// Main Navbar component
export function Navbar({ children }: { children: React.ReactNode }) {
  const [mobileOpen, setMobileOpen] = useState(false);

  return (
    <div className="flex h-screen w-full">
      {/* Desktop sidebar - always visible on md+ screens */}
      <DesktopSidebar />

      {/* Mobile sheet navbar */}
      <MobileNavbar open={mobileOpen} onOpenChange={setMobileOpen} />

      {/* Main content area */}
      <div className="flex flex-1 flex-col overflow-hidden">
        {/* Mobile header with menu trigger */}
        <MobileHeader onMenuClick={() => setMobileOpen(true)} />

        {/* Page content */}
        <main className="flex-1 overflow-auto">{children}</main>
      </div>
    </div>
  );
}
