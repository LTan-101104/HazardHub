'use client';

import { Sheet, SheetContent, SheetTitle } from '@/components/ui/sheet';
import { HazardDetailContent } from '../hazard-detail/hazard-detail-content';
import { useMap } from '../map-provider';
import { VisuallyHidden } from '../utils/visually-hidden';

export function HazardDetailSheet() {
  const { state, dispatch } = useMap();

  if (!state.selectedHazard) return null;

  return (
    <Sheet
      open={state.isHazardDetailOpen}
      onOpenChange={(open) => {
        if (!open) {
          dispatch({ type: 'SELECT_HAZARD', payload: null });
        }
      }}
    >
      <SheetContent side="bottom" className="rounded-t-3xl border-t border-[#2E2E2E] bg-[#1A1A1A] p-0 [&>button]:hidden">
        <VisuallyHidden>
          <SheetTitle>Hazard Details</SheetTitle>
        </VisuallyHidden>
        <div className="flex justify-center py-3">
          <div className="h-1 w-10 rounded-full bg-[#B8B9B6]" />
        </div>
        <HazardDetailContent hazard={state.selectedHazard} />
      </SheetContent>
    </Sheet>
  );
}
