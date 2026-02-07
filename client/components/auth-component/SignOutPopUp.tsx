import React from 'react';
import { LogOut, X } from 'lucide-react';

interface SignOutPopUpProps {
  isOpen: boolean;
  onClose: () => void;
  onSignOut: () => void;
}

const SignOutPopUp = ({ isOpen, onClose, onSignOut }: SignOutPopUpProps) => {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div className="absolute inset-0 bg-black/60" onClick={onClose} />

      {/* Modal */}
      <div className="relative bg-[#2a2a2a] rounded-2xl p-6 w-full max-w-sm mx-4 shadow-xl">
        {/* Header */}
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-white text-lg font-semibold">Sign Out</h2>
          <button onClick={onClose} className="text-gray-400 hover:text-white transition-colors cursor-pointer">
            <X size={20} />
          </button>
        </div>

        {/* Icon */}
        <div className="flex justify-center mb-6">
          <div className="w-16 h-16 rounded-full bg-[#ff8c00]/20 flex items-center justify-center">
            <LogOut size={28} className="text-[#ff8c00]" />
          </div>
        </div>

        {/* Content */}
        <div className="text-center mb-8">
          <h3 className="text-white text-lg font-medium mb-2">Are you sure you want to sign out?</h3>
          <p className="text-gray-400 text-sm">
            You will need to sign in again to access your safety profile and navigation history.
          </p>
        </div>

        {/* Buttons */}
        <div className="flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 py-3 px-4 rounded-full bg-[#3a3a3a] text-white font-medium hover:bg-[#4a4a4a] transition-colors cursor-pointer"
          >
            Cancel
          </button>
          <button
            onClick={onSignOut}
            className="flex-1 py-3 px-4 rounded-full bg-[#ff8c00] text-white font-medium hover:bg-[#e67e00] transition-colors cursor-pointer"
          >
            Sign Out
          </button>
        </div>
      </div>
    </div>
  );
};

export default SignOutPopUp;
