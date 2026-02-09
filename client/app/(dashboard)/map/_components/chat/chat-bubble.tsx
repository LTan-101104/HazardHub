'use client';

import { Sparkles } from 'lucide-react';
import { RouteSuggestionCard } from './route-suggestion-card';
import type { ChatMessage, RouteCardData } from '@/types/map';

interface ChatBubbleProps {
  message: ChatMessage;
  onApplyRoute: (card: RouteCardData) => void;
}

export function ChatBubble({ message, onApplyRoute }: ChatBubbleProps) {
  const routeCards = message.routeCards ?? (message.routeCard ? [message.routeCard] : []);

  if (message.role === 'user') {
    return (
      <div className="flex justify-end">
        <div className="max-w-[80%] rounded-2xl rounded-br-sm bg-[rgba(255,132,0,0.15)] px-4 py-3">
          <p className="text-sm leading-relaxed text-white">{message.content}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="flex gap-2">
      <div className="flex size-7 shrink-0 items-center justify-center rounded-full bg-[#4285F4]">
        <Sparkles className="size-3.5 text-white" />
      </div>
      <div className="flex max-w-[85%] flex-col gap-3">
        <p className="text-sm leading-relaxed text-[#B8B9B6]">{message.content}</p>
        {routeCards.map((card, index) => (
          <RouteSuggestionCard key={`${message.id}-route-${index}`} card={card} onApply={() => onApplyRoute(card)} />
        ))}
      </div>
    </div>
  );
}
