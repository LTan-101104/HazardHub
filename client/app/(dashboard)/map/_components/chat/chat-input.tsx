'use client';

import { Send } from 'lucide-react';
import { useState } from 'react';
import { useMap } from '../map-provider';

export function ChatInput() {
  const [value, setValue] = useState('');
  const { dispatch } = useMap();

  const handleSend = () => {
    if (!value.trim()) return;
    dispatch({
      type: 'ADD_CHAT_MESSAGE',
      payload: {
        id: crypto.randomUUID(),
        role: 'user',
        content: value.trim(),
        timestamp: new Date().toISOString(),
      },
    });
    setValue('');
  };

  return (
    <div className="flex items-center gap-2.5 px-4 pb-6 pt-2">
      <div className="flex h-11 flex-1 items-center gap-2.5 rounded-full border border-[#2E2E2E] bg-[#111] px-3.5">
        <input
          type="text"
          value={value}
          onChange={(e) => setValue(e.target.value)}
          onKeyDown={(e) => e.key === 'Enter' && handleSend()}
          placeholder="Ask about routes, hazards..."
          className="flex-1 bg-transparent text-sm text-white placeholder:text-[#B8B9B6] focus:outline-none"
        />
        <button
          onClick={handleSend}
          className="flex size-8 shrink-0 items-center justify-center rounded-full bg-[#FF8400] transition-colors hover:bg-[#e67700]"
        >
          <Send className="size-4 text-black" />
        </button>
      </div>
    </div>
  );
}
