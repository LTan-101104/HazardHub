'use client';

import { useRef, useEffect } from 'react';
import { ScrollArea } from '@/components/ui/scroll-area';
import { ChatBubble } from './chat-bubble';
import { useMap } from '../map-provider';
import { Sparkles } from 'lucide-react';
import type { RouteCardData } from '@/types/map';

interface ChatMessagesProps {
  isSending: boolean;
  onApplyRoute: (card: RouteCardData) => void;
}

export function ChatMessages({ isSending, onApplyRoute }: ChatMessagesProps) {
  const { state } = useMap();
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [isSending, state.chatMessages.length]);

  return (
    <ScrollArea className="flex-1 px-4">
      <div className="flex flex-col gap-4 py-3">
        {state.chatMessages.map((msg) => (
          <ChatBubble key={msg.id} message={msg} onApplyRoute={onApplyRoute} />
        ))}
        {isSending && (
          <div className="flex gap-2">
            <div className="flex size-7 shrink-0 items-center justify-center rounded-full bg-[#4285F4]">
              <Sparkles className="size-3.5 text-white" />
            </div>
            <div className="rounded-2xl rounded-bl-sm bg-[#242424] px-3 py-2">
              <span className="text-xs text-[#B8B9B6]">Thinking...</span>
            </div>
          </div>
        )}
        <div ref={bottomRef} />
      </div>
    </ScrollArea>
  );
}
