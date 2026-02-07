'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import {
  ArrowLeft,
  Sparkles,
  ImagePlus,
  Camera,
  Upload,
  CircleCheck,
  Zap,
  TriangleAlert,
  MapPin,
  Clock3,
  Send,
  Loader2,
} from 'lucide-react';
import { HazardSeverity } from '@/types';
import { useReportHazard } from '../_context/report-hazard-context';
import { useAuth } from '@/context/AuthContext';
import { createHazardReport } from '@/lib/actions/hazard-action';
import { auth } from '@/lib/firebase';

const SEVERITY_MAP = {
  LOW: HazardSeverity.LOW,
  MEDIUM: HazardSeverity.MEDIUM,
  HIGH: HazardSeverity.HIGH,
  CRITICAL: HazardSeverity.CRITICAL,
} as const;

type SeverityKey = keyof typeof SEVERITY_MAP;

export default function ReportHazardDetailsPage() {
  const router = useRouter();
  const { user } = useAuth();
  const { state: hazardState, dispatch: hazardDispatch } = useReportHazard();

  const [severity, setSeverity] = useState<SeverityKey>('CRITICAL');
  const [notes, setNotes] = useState('');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const gpsDisplay =
    hazardState.latitude !== null && hazardState.longitude !== null
      ? `${hazardState.latitude.toFixed(4)}° N, ${hazardState.longitude.toFixed(4)}° W`
      : 'No location set';

  const timestamp =
    new Date().toLocaleDateString('en-US', {
      month: 'short',
      day: 'numeric',
    }) +
    ', ' +
    new Date().toLocaleTimeString('en-US', {
      hour: 'numeric',
      minute: '2-digit',
      hour12: true,
    });

  const handleSeverityChange = (key: SeverityKey) => {
    setSeverity(key);
    hazardDispatch({ type: 'SET_SEVERITY', payload: SEVERITY_MAP[key] });
  };

  const handleSubmit = async () => {
    if (!user) {
      setError('You must be logged in to report a hazard.');
      return;
    }
    if (hazardState.latitude === null || hazardState.longitude === null) {
      setError('Location is required. Please go back and select a location.');
      return;
    }

    setIsSubmitting(true);
    setError(null);

    try {
      const currentUser = auth?.currentUser;
      if (!currentUser) {
        setError('Authentication session expired. Please sign in again.');
        return;
      }
      const idToken = await currentUser.getIdToken();

      await createHazardReport(idToken, {
        reporterId: user.id,
        longitude: hazardState.longitude,
        latitude: hazardState.latitude,
        locationAccuracyMeters: hazardState.locationAccuracyMeters ?? 10,
        address: hazardState.address || undefined,
        severity: SEVERITY_MAP[severity],
        description: notes || 'Hazard reported via HazardHub',
        imageUrl: hazardState.imageUrl || undefined,
        affectedRadiusMeters: 100,
      });

      hazardDispatch({ type: 'RESET' });
      router.push('/map');
    } catch (err) {
      const message = err instanceof Error ? err.message : 'Failed to submit report';
      setError(message);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="flex h-dvh w-full flex-col bg-[#111111]">
      {/* Header */}
      <div className="flex h-14 shrink-0 items-center justify-between border-b border-[#2E2E2E] bg-[#1A1A1A] px-4">
        <button
          onClick={() => router.back()}
          className="flex size-9 items-center justify-center rounded-full bg-[#2E2E2E] transition-colors hover:bg-[#3E3E3E]"
        >
          <ArrowLeft className="size-4.5 text-white" />
        </button>
        <h1 className="text-base font-semibold text-white" style={{ fontFamily: 'var(--font-mono)' }}>
          Report Hazard
        </h1>
        <div className="flex items-center rounded-xl bg-[#2E2E2E] px-2.5 py-1">
          <span className="text-[11px] font-medium text-[#B8B9B6]">Step 2 of 2</span>
        </div>
      </div>

      {/* Scrollable Content */}
      <div className="flex-1 overflow-y-auto">
        {/* Capture Section */}
        <div className="flex flex-col gap-3 p-4">
          <div className="flex items-center justify-between">
            <span
              className="text-[11px] font-semibold tracking-wider text-[#B8B9B6]"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              CAPTURE HAZARD
            </span>
            <div className="flex items-center gap-1 rounded-xl bg-[#FF8400] px-2 py-0.75">
              <Sparkles className="size-2.5 text-[#111111]" />
              <span className="text-[10px] font-medium text-[#111111]">Gemini Vision</span>
            </div>
          </div>

          <button className="flex h-40 flex-col items-center justify-center gap-2.5 rounded-2xl border border-[#2E2E2E] bg-[#2E2E2E]">
            <ImagePlus className="size-9 text-[#B8B9B6]" />
            <span className="text-sm font-medium text-white">Upload or capture photo</span>
            <span className="text-xs text-[#B8B9B6]">Tap to browse or take a photo</span>
          </button>

          <div className="flex gap-2.5">
            <button className="flex h-11 flex-1 items-center justify-center gap-2 rounded-[22px] bg-[#FF8400]">
              <Camera className="size-4.5 text-[#111111]" />
              <span className="text-sm font-medium text-[#111111]" style={{ fontFamily: 'var(--font-mono)' }}>
                Capture
              </span>
            </button>
            <button className="flex h-11 flex-1 items-center justify-center gap-2 rounded-[22px] border border-[#2E2E2E] bg-[#111111]">
              <Upload className="size-4.5 text-white" />
              <span className="text-sm font-medium text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                Upload
              </span>
            </button>
          </div>
        </div>

        <div className="h-px bg-[#2E2E2E]" />

        {/* AI Analysis Section */}
        <div className="flex flex-col gap-3 p-4">
          <div className="flex items-center justify-between">
            <span
              className="text-[11px] font-semibold tracking-wider text-[#B8B9B6]"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              AI ANALYSIS
            </span>
            <div className="flex items-center gap-1.5 rounded-xl bg-[#222924] px-2 py-1">
              <CircleCheck className="size-3 text-[#B6FFCE]" />
              <span className="text-[11px] font-medium text-[#B6FFCE]">Analyzed</span>
            </div>
          </div>

          <div className="flex flex-col gap-2 rounded-xl bg-[#24100B] p-3.5">
            <div className="flex items-center gap-2.5">
              <Zap className="size-5 text-[#FF5C33]" />
              <span className="text-[15px] font-semibold text-[#FF5C33]" style={{ fontFamily: 'var(--font-mono)' }}>
                Power Line Down
              </span>
            </div>
            <p className="text-[13px] leading-[1.4] text-[#FF5C33]">
              Downed power line detected across 2 lanes. High voltage hazard identified.
            </p>
          </div>

          {/* Severity Levels */}
          <div className="flex gap-2">
            {(
              [
                { key: 'LOW', label: 'Low' },
                { key: 'MEDIUM', label: 'Medium' },
                { key: 'HIGH', label: 'High' },
                { key: 'CRITICAL', label: 'Critical', icon: true },
              ] as const
            ).map((level) => {
              const isActive = severity === level.key;
              const styles = getSeverityStyles(level.key, isActive);
              return (
                <button
                  key={level.key}
                  onClick={() => handleSeverityChange(level.key)}
                  className={`flex h-12 flex-1 items-center justify-center gap-1 rounded-lg border ${styles}`}
                >
                  {'icon' in level && level.icon && isActive && (
                    <TriangleAlert className="size-3.5 text-[#FF5C33]" />
                  )}
                  <span className="text-xs font-medium">{level.label}</span>
                </button>
              );
            })}
          </div>
        </div>

        <div className="h-px bg-[#2E2E2E]" />

        {/* Auto-Captured Data */}
        <div className="flex flex-col gap-3 p-4">
          <span
            className="text-[11px] font-semibold tracking-wider text-[#B8B9B6]"
            style={{ fontFamily: 'var(--font-mono)' }}
          >
            AUTO-CAPTURED DATA
          </span>
          <div className="flex gap-2.5">
            <div className="flex flex-1 flex-col gap-1 rounded-[10px] bg-[#2E2E2E] p-3">
              <div className="flex items-center gap-1.5">
                <MapPin className="size-3.5 text-[#FF8400]" />
                <span className="text-[11px] text-[#B8B9B6]">GPS Location</span>
              </div>
              <span className="text-[11px] font-medium text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                {gpsDisplay}
              </span>
            </div>
            <div className="flex flex-1 flex-col gap-1 rounded-[10px] bg-[#2E2E2E] p-3">
              <div className="flex items-center gap-1.5">
                <Clock3 className="size-3.5 text-[#FF8400]" />
                <span className="text-[11px] text-[#B8B9B6]">Timestamp</span>
              </div>
              <span className="text-[11px] font-medium text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                {timestamp}
              </span>
            </div>
          </div>
        </div>

        <div className="h-px bg-[#2E2E2E]" />

        {/* Additional Notes */}
        <div className="flex flex-col gap-2.5 px-4 py-3">
          <span
            className="text-[11px] font-semibold tracking-wider text-[#B8B9B6]"
            style={{ fontFamily: 'var(--font-mono)' }}
          >
            ADDITIONAL NOTES (OPTIONAL)
          </span>
          <textarea
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            placeholder="Add any additional details about the hazard..."
            rows={2}
            className="resize-none rounded-xl border border-[#2E2E2E] bg-[#2E2E2E] p-3 text-[13px] text-white placeholder:text-[#B8B9B6] focus:border-[#FF8400] focus:outline-none"
          />
        </div>
      </div>

      {/* Error Message */}
      {error && (
        <div className="shrink-0 bg-[#24100B] px-4 py-2">
          <p className="text-center text-xs text-[#FF5C33]">{error}</p>
        </div>
      )}

      {/* Bottom Submit */}
      <div className="shrink-0 border-t border-[#2E2E2E] bg-[#1A1A1A] px-4 pb-8 pt-4">
        <button
          onClick={handleSubmit}
          disabled={isSubmitting}
          className="flex h-13 w-full items-center justify-center gap-2 rounded-3xl bg-[#FF8400] transition-opacity hover:opacity-90 disabled:opacity-50"
          style={{ fontFamily: 'var(--font-mono)' }}
        >
          {isSubmitting ? (
            <Loader2 className="size-4.5 animate-spin text-[#111111]" />
          ) : (
            <Send className="size-4.5 text-[#111111]" />
          )}
          <span className="text-[15px] font-semibold text-[#111111]">
            {isSubmitting ? 'Submitting...' : 'Submit Report'}
          </span>
        </button>
      </div>
    </div>
  );
}

function getSeverityStyles(level: SeverityKey, isActive: boolean): string {
  if (!isActive) {
    return 'border-[#2E2E2E] bg-[#2E2E2E] text-[#B8B9B6]';
  }
  switch (level) {
    case 'LOW':
      return 'border-[#2E2E2E] bg-[#2E2E2E] text-[#B8B9B6]';
    case 'MEDIUM':
      return 'border-[#2E2E2E] bg-[#2E2E2E] text-[#B8B9B6]';
    case 'HIGH':
      return 'border-[#FF8400] bg-[#291C0F] text-[#FF8400] font-semibold';
    case 'CRITICAL':
      return 'border-[#FF5C33] border-2 bg-[#24100B] text-[#FF5C33] font-semibold';
  }
}
