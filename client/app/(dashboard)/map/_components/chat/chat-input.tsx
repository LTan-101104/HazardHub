'use client';

import { Send } from 'lucide-react';
import { useState } from 'react';

interface ChatInputProps {
  onSend: (message: string) => Promise<void>;
  isSending: boolean;
}

export function ChatInput({ onSend, isSending }: ChatInputProps) {
  const [value, setValue] = useState('');

  const handleSend = async () => {
    const message = value.trim();
    if (!message || isSending) return;
    setValue('');
    await onSend(message);
  };

  return (
    <div className="shrink-0 flex items-center gap-2.5 px-4 pb-6 pt-2">
      <div className="flex h-11 flex-1 items-center gap-2.5 rounded-full border border-[#2E2E2E] bg-[#111] px-3.5">
        <input
          type="text"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={(e) => {
            if (e.key !== 'Enter') return;
            e.preventDefault();
            void handleSend();
          }}
          placeholder="Ask about routes, hazards..."
          disabled={isSending}
          className="flex-1 bg-transparent text-sm text-white placeholder:text-[#B8B9B6] focus:outline-none"
        />
        <button
          onClick={() => void handleSend()}
          disabled={isSending || !value.trim()}
          className="flex size-8 shrink-0 items-center justify-center rounded-full bg-[#FF8400] transition-colors hover:bg-[#e67700] disabled:cursor-not-allowed disabled:opacity-60"
        >
          <Send className="size-4 text-black" />
        </button>
      </div>
    </div>
  );
}
