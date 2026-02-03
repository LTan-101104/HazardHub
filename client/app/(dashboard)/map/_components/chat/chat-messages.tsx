'use client';

import { useRef, useEffect } from 'react';
import { ScrollArea } from '@/components/ui/scroll-area';
import { ChatBubble } from './chat-bubble';
import { useMap } from '../map-provider';

export function ChatMessages() {
  const { state } = useMap();
  const bottomRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [state.chatMessages.length]);

  return (
    <ScrollArea className="flex-1 px-4">
      <div className="flex flex-col gap-4 py-3">
        {state.chatMessages.map((msg) => (
          <ChatBubble key={msg.id} message={msg} />
        ))}
        <div ref={bottomRef} />
      </div>
    </ScrollArea>
  );
}
