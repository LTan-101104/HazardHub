'use client';

import { Sheet, SheetContent, SheetTitle } from '@/components/ui/sheet';
import { ChatPanel } from '../chat/chat-panel';
import { useMap } from '../map-provider';
import { VisuallyHidden } from '../utils/visually-hidden';

export function ChatSheet() {
  const { state, dispatch } = useMap();

  return (
    <Sheet
      open={state.isChatOpen && state.viewState === 'chat'}
      onOpenChange={(open) => {
        if (!open) dispatch({ type: 'TOGGLE_CHAT', payload: false });
      }}
    >
      <SheetContent
        side="bottom"
        className="h-[75dvh] rounded-t-3xl border-t border-[#2E2E2E] bg-[#1A1A1A] p-0 [&>button]:hidden"
      >
        <VisuallyHidden>
          <SheetTitle>AI Chat</SheetTitle>
        </VisuallyHidden>
        <div className="flex justify-center py-3">
          <div className="h-1 w-10 rounded-full bg-[#B8B9B6]" />
        </div>
        <div className="flex h-[calc(75dvh-28px)] min-h-0 flex-col">
          <ChatPanel />
        </div>
      </SheetContent>
    </Sheet>
  );
}
