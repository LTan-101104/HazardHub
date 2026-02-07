'use client';

import { useMap } from '../map-provider';
import { RoutePanel } from '../panels/route-panel';
import { ChatPanel } from '../chat/chat-panel';
import { HazardDetailContent } from '../hazard-detail/hazard-detail-content';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Separator } from '@/components/ui/separator';

export function SidePanel() {
  const { state } = useMap();

  return (
    <aside className="absolute right-0 top-0 z-10 hidden h-full w-[380px] flex-col border-l border-[#2E2E2E] bg-[#1A1A1A] lg:flex">
      {/* Hazard detail overlay */}
      {state.isHazardDetailOpen && state.selectedHazard ? (
        <ScrollArea className="flex-1">
          <HazardDetailContent hazard={state.selectedHazard} />
        </ScrollArea>
      ) : state.viewState === 'routing' || state.viewState === 'browse' ? (
        <ScrollArea className="flex-1">
          <RoutePanel />
          {state.isChatOpen && (
            <>
              <Separator className="bg-[#2E2E2E]" />
              <ChatPanel />
            </>
          )}
        </ScrollArea>
      ) : state.viewState === 'chat' ? (
        <ChatPanel />
      ) : null}
    </aside>
  );
}
