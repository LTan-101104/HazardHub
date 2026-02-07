'use client';

import { ReportHazardProvider } from './_context/report-hazard-context';

export default function ReportHazardLayout({ children }: { children: React.ReactNode }) {
  return <ReportHazardProvider>{children}</ReportHazardProvider>;
}
