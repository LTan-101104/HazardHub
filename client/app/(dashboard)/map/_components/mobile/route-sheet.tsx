'use client';

import { Sheet, SheetContent, SheetTitle } from '@/components/ui/sheet';
import { RoutePanel } from '../panels/route-panel';
import { useMap } from '../map-provider';
import { VisuallyHidden } from '../utils/visually-hidden';

export function RouteSheet() {
  const { state, dispatch } = useMap();

  return (
    <Sheet
      open={state.viewState === 'routing'}
      onOpenChange={(open) => {
        if (!open) dispatch({ type: 'SET_VIEW_STATE', payload: 'browse' });
      }}
    >
      <SheetContent side="bottom" className="rounded-t-3xl border-t border-[#2E2E2E] bg-[#1A1A1A] p-0 [&>button]:hidden">
        <VisuallyHidden>
          <SheetTitle>Route Information</SheetTitle>
        </VisuallyHidden>
        <div className="flex justify-center py-3">
          <div className="h-1 w-10 rounded-full bg-[#B8B9B6]" />
        </div>
        <RoutePanel />
      </SheetContent>
    </Sheet>
  );
}
