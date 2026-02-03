'use client';

import { ChatHeader } from './chat-header';
import { ChatMessages } from './chat-messages';
import { QuickActions } from './quick-actions';
import { ChatInput } from './chat-input';

export function ChatPanel() {
  return (
    <div className="flex h-full flex-col">
      <ChatHeader />
      <ChatMessages />
      <QuickActions />
      <ChatInput />
    </div>
  );
}
