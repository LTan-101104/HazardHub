'use client';

import { useState } from 'react';
import { useRouter, useSearchParams } from 'next/navigation';
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
} from 'lucide-react';

type Severity = 'low' | 'medium' | 'high' | 'critical';

export default function ReportHazardDetailsPage() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const lat = searchParams.get('lat');
  const lng = searchParams.get('lng');

  const [severity, setSeverity] = useState<Severity>('critical');
  const [notes, setNotes] = useState('');

  const gpsDisplay = lat && lng
    ? `${Number(lat).toFixed(4)}° N, ${Number(lng).toFixed(4)}° W`
    : '40.7128° N, 74.0060° W';

  const timestamp = new Date().toLocaleDateString('en-US', {
    month: 'short',
    day: 'numeric',
  }) + ', ' + new Date().toLocaleTimeString('en-US', {
    hour: 'numeric',
    minute: '2-digit',
    hour12: true,
  });

  const handleSubmit = () => {
    // TODO: Submit report to backend
    router.push('/map');
  };

  return (
    <div className="flex h-dvh w-full flex-col bg-[#111111]">
      {/* Header */}
      <div className="flex h-14 shrink-0 items-center justify-between border-b border-[#2E2E2E] bg-[#1A1A1A] px-4">
        <button
          onClick={() => router.back()}
          className="flex size-9 items-center justify-center rounded-full bg-[#2E2E2E] transition-colors hover:bg-[#3E3E3E]"
        >
          <ArrowLeft className="size-[18px] text-white" />
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
          {/* Section Label */}
          <div className="flex items-center justify-between">
            <span
              className="text-[11px] font-semibold tracking-wider text-[#B8B9B6]"
              style={{ fontFamily: 'var(--font-mono)' }}
            >
              CAPTURE HAZARD
            </span>
            <div className="flex items-center gap-1 rounded-xl bg-[#FF8400] px-2 py-[3px]">
              <Sparkles className="size-2.5 text-[#111111]" />
              <span className="text-[10px] font-medium text-[#111111]">Gemini Vision</span>
            </div>
          </div>

          {/* Upload Area */}
          <button className="flex h-40 flex-col items-center justify-center gap-2.5 rounded-2xl border border-[#2E2E2E] bg-[#2E2E2E]">
            <ImagePlus className="size-9 text-[#B8B9B6]" />
            <span className="text-sm font-medium text-white">Upload or capture photo</span>
            <span className="text-xs text-[#B8B9B6]">Tap to browse or take a photo</span>
          </button>

          {/* Capture / Upload Buttons */}
          <div className="flex gap-2.5">
            <button className="flex h-11 flex-1 items-center justify-center gap-2 rounded-[22px] bg-[#FF8400]">
              <Camera className="size-[18px] text-[#111111]" />
              <span className="text-sm font-medium text-[#111111]" style={{ fontFamily: 'var(--font-mono)' }}>
                Capture
              </span>
            </button>
            <button className="flex h-11 flex-1 items-center justify-center gap-2 rounded-[22px] border border-[#2E2E2E] bg-[#111111]">
              <Upload className="size-[18px] text-white" />
              <span className="text-sm font-medium text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                Upload
              </span>
            </button>
          </div>
        </div>

        {/* Divider */}
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

          {/* Detected Hazard Card */}
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
            {([
              { key: 'low', label: 'Low' },
              { key: 'medium', label: 'Medium' },
              { key: 'high', label: 'High' },
              { key: 'critical', label: 'Critical', icon: true },
            ] as const).map((level) => {
              const isActive = severity === level.key;
              const styles = getSeverityStyles(level.key, isActive);
              return (
                <button
                  key={level.key}
                  onClick={() => setSeverity(level.key)}
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

        {/* Divider */}
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
            {/* GPS Card */}
            <div className="flex flex-1 flex-col gap-1 rounded-[10px] bg-[#2E2E2E] p-3">
              <div className="flex items-center gap-1.5">
                <MapPin className="size-3.5 text-[#FF8400]" />
                <span className="text-[11px] text-[#B8B9B6]">GPS Location</span>
              </div>
              <span className="text-[11px] font-medium text-white" style={{ fontFamily: 'var(--font-mono)' }}>
                {gpsDisplay}
              </span>
            </div>
            {/* Timestamp Card */}
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

        {/* Divider */}
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

      {/* Bottom Submit */}
      <div className="shrink-0 border-t border-[#2E2E2E] bg-[#1A1A1A] px-4 pb-8 pt-4">
        <button
          onClick={handleSubmit}
          className="flex h-[52px] w-full items-center justify-center gap-2 rounded-3xl bg-[#FF8400] transition-opacity hover:opacity-90"
          style={{ fontFamily: 'var(--font-mono)' }}
        >
          <Send className="size-[18px] text-[#111111]" />
          <span className="text-[15px] font-semibold text-[#111111]">Submit Report</span>
        </button>
      </div>
    </div>
  );
}

function getSeverityStyles(level: Severity, isActive: boolean): string {
  if (!isActive) {
    return 'border-[#2E2E2E] bg-[#2E2E2E] text-[#B8B9B6]';
  }
  switch (level) {
    case 'low':
      return 'border-[#2E2E2E] bg-[#2E2E2E] text-[#B8B9B6]';
    case 'medium':
      return 'border-[#2E2E2E] bg-[#2E2E2E] text-[#B8B9B6]';
    case 'high':
      return 'border-[#FF8400] bg-[#291C0F] text-[#FF8400] font-semibold';
    case 'critical':
      return 'border-[#FF5C33] border-2 bg-[#24100B] text-[#FF5C33] font-semibold';
  }
}
