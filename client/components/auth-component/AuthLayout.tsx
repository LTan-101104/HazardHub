'use client';

import { NavigationIcon, ShieldCheckIcon, MapPinIcon, RouteIcon } from 'lucide-react';

interface AuthLayoutProps {
  children: React.ReactNode;
  heading: string;
  subheading: string;
}

export function AuthLayout({ children, heading, subheading }: AuthLayoutProps) {
  return (
    <div className="min-h-screen flex">
      {/* Left Side - Branding (Orange) - Desktop Only */}
      <div className="hidden lg:flex lg:w-1/2 bg-[#ff8c00] p-12 flex-col justify-between">
        <div className="flex items-center gap-2">
          <div className="p-2 bg-black rounded-lg">
            <NavigationIcon className="h-8 w-8 text-[#ff8c00]" />
          </div>
          <span className="text-2xl font-bold text-gray-900 tracking-wide">SAFE ROUTE</span>
        </div>

        <div className="space-y-6">
          <h1 className="text-4xl font-mono font-bold text-gray-900">
            Navigate Safely,
            <br />
            Anywhere You Go
          </h1>
          <p className="text-gray-800 text-lg">
            AI-powered safety navigation that helps you avoid hazards and find the safest routes to your destination.
          </p>
        </div>

        <div className="space-y-4">
          <div className="flex items-center gap-3">
            <ShieldCheckIcon className="h-5 w-5 text-gray-900" />
            <span className="text-gray-900">Real-time hazard alerts</span>
          </div>
          <div className="flex items-center gap-3">
            <MapPinIcon className="h-5 w-5 text-gray-900" />
            <span className="text-gray-900">Save your important locations</span>
          </div>
          <div className="flex items-center gap-3">
            <RouteIcon className="h-5 w-5 text-gray-900" />
            <span className="text-gray-900">AI-powered route optimization</span>
          </div>
        </div>
      </div>

      {/* Right Side - Form */}
      <div className="w-full lg:w-1/2 flex flex-col items-center justify-center p-8 bg-[#1a1a1a]">
        {/* Mobile Header - Only visible on mobile */}
        <div className="flex flex-col items-center mb-8 lg:hidden">
          <div className="p-4 bg-[#ff8c00] rounded-xl mb-4">
            <NavigationIcon className="h-12 w-12 text-gray-900" />
          </div>
          <span className="text-2xl font-bold text-[#ff8c00] tracking-wide mb-2">SAFE ROUTE</span>
          <p className="text-gray-400 text-sm">Navigate Safely, Anywhere You Go</p>
        </div>

        <div className="w-full max-w-md space-y-6">
          <div className="text-left">
            <h2 className="text-3xl font-mono font-bold text-white">{heading}</h2>
            <p className="text-gray-400 mt-2">{subheading}</p>
          </div>
          {children}
        </div>
      </div>
    </div>
  );
}
