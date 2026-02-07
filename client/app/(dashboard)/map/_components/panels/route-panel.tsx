'use client';

import { useMap } from '../map-provider';
import { RouteHeader } from './route-header';
import { LocationInputs } from './location-inputs';
import { RouteStats } from './route-stats';
import { RouteOptions } from './route-options';
import { RouteActions } from './route-actions';
import { HazardWarningCard } from './hazard-warning-card';

export function RoutePanel() {
  const { state } = useMap();

  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="border-b border-[#2E2E2E] px-4 py-4 lg:px-5">
        <RouteHeader />
      </div>

      {/* Route Section */}
      <div className="flex flex-col gap-4 border-b border-[#2E2E2E] px-4 py-4 lg:px-5">
        <LocationInputs />
        <RouteStats />
      </div>

      {/* Hazard Warning */}
      {state.isHazardAlertVisible && (
        <HazardWarningCard
          title="Deep Snow Drift Detected"
          description="Hazard reported on Oak Street. AI suggests safer alternate route via Main Ave."
        />
      )}

      {/* Route Options */}
      <div className="flex flex-col gap-3 px-4 py-4 lg:px-5">
        <span className="font-mono text-sm font-semibold text-white lg:block hidden">Route Options</span>
        <RouteOptions />
      </div>

      {/* Actions */}
      <div className="px-4 pb-5 pt-3 lg:px-5">
        <RouteActions />
      </div>
    </div>
  );
}
