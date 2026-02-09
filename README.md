# HazardHub

- Official website: [HazardHub](https://hazard-hub-pymz.vercel.app/)
- Gemini Hackathon Devpost submission: [HazardHub submission](https://devpost.com/submit-to/27555-gemini-3-hackathon/manage/submissions/916634-hazardhub/)

## Inspiration

Every year, over 42,000 people die in traffic crashes in the United States alone, and millions more are injured. What struck us was how many of these incidents are preventable — a driver hits debris that someone saw 10 minutes ago, a commuter takes a flooded road because no one flagged it, or a stranded motorist waits too long for help because they couldn't communicate their exact location. Existing navigation apps optimize for speed, not safety. We asked ourselves: **what if your GPS didn't just get you there faster, but got you there alive?**

We were also inspired by the power of community-driven platforms like Waze, but wanted to go further. Beyond just reporting, we envisioned an intelligent system that analyzes hazards with AI, scores routes by safety — not just ETA — and puts life-saving emergency tools one tap away. The recent rise of extreme weather events, road infrastructure decay, and distracted driving made this feel not just timely, but urgent. HazardHub was born from the belief that technology should protect people, not just move them.

## What it does

**HazardHub** is a mobile-first web platform that transforms everyday drivers into a real-time safety network. It combines community-driven hazard reporting, AI-powered route intelligence, and one-touch emergency response into a single, intuitive experience.

- **Real-Time Hazard Reporting:** Drivers can report road hazards — accidents, debris, flooding, potholes, construction, wildlife — by dropping a pin or using their current location. They can upload photos, which are automatically analyzed by Google Gemini AI to classify the hazard type, severity, and generate descriptions.
- **Community Verification:** Other users can confirm or dispute reported hazards, creating a crowdsourced trust layer that keeps the data reliable and up-to-date. Hazards automatically expire to prevent stale reports.
- **Safety-Scored Route Navigation:** When searching for directions, HazardHub doesn't just show the fastest route — it calculates a **safety score** for each option based on active hazards along the path, giving drivers the information they need to choose the safest way home.
- **One-Touch SOS Emergency System:** In a crisis, users can trigger an SOS with a single tap. The system broadcasts their precise GPS location, generates an AI-powered safety checklist tailored to their situation, provides one-tap dialing to 911 and roadside assistance, and instantly notifies their pre-configured emergency contacts.
- **Safety Profile:** Users can save important locations (home, work, emergency shelters) and maintain a priority-ordered list of emergency contacts — all accessible instantly when seconds matter most.
- **AI-Powered Assistance:** An integrated AI chat assistant answers safety-related questions, and the hazard detail view provides AI-generated suggestions for how to handle specific road dangers.

## How we built it

We architected HazardHub as a modern, full-stack application with clear separation of concerns:

- **Frontend:** Built with **Next.js 16** and **React 19** using the App Router pattern, styled with **Tailwind CSS v4** and **Radix UI** primitives for an accessible, responsive UI. The interactive map experience is powered by the **Google Maps JavaScript API** with custom dark-themed styling.
- **Backend:** A **Spring Boot 4** (Java 21) REST API handles all business logic, with **MongoDB** as our database — chosen for its native geospatial indexing capabilities, which are critical for location-based hazard queries and nearby SOS detection.
- **Authentication:** **Firebase Authentication** provides secure email/password and Google OAuth login flows, with token validation enforced on every backend request through a custom security filter chain.
- **AI Integration:** **Google Gemini 2.0 Flash** powers our hazard image analysis, safety suggestion generation, and route safety assessments — turning raw data into actionable intelligence.
- **Infrastructure:** **Docker Compose** orchestrates our MongoDB instance and monitoring tools, enabling consistent development environments across the team.

## Challenges we ran into

- **Geospatial query performance:** Implementing efficient "nearby hazard" and "nearby SOS" searches required careful design of MongoDB geospatial indexes and tuning query parameters to balance accuracy with response time.
- **Real-time state synchronization:** Keeping the map, hazard markers, SOS status, and route data in sync across multiple interactive panels — especially on mobile where sheet-based navigation adds complexity — required thoughtful state management and custom React hooks.
- **AI reliability for image analysis:** Getting Gemini to consistently return structured, actionable hazard classifications from user-uploaded photos (which vary wildly in quality, angle, and lighting) took significant prompt engineering and confidence-threshold tuning.
- **Mobile-first UX with desktop parity:** Designing an interface that works beautifully on a phone dashboard mount while also being powerful on desktop required building two distinct but synchronized interaction patterns — bottom sheets for mobile and side panels for desktop.
- **Security at every layer:** Ensuring that users can only access their own data (emergency contacts, saved locations, SOS events) while keeping the hazard reporting system open and collaborative demanded fine-grained ownership validation throughout the API.

## Accomplishments that we're proud of

- **The SOS system works end-to-end.** From one-tap activation to GPS broadcasting, AI safety checklists, emergency contact notification, and 911 quick-dial — it's a feature that could genuinely save a life.
- **AI-powered hazard analysis actually works.** Users can snap a photo of a road hazard and the system automatically classifies it, estimates severity, and generates a description — dramatically lowering the friction of reporting.
- **Safety-scored routing is a paradigm shift.** We believe we're among the first to present route options ranked by safety rather than just speed, giving drivers agency over their own well-being.
- **The community verification system.** By letting users confirm or dispute hazards, we built a self-correcting data ecosystem that gets more accurate as more people use it.
- **Production-quality architecture.** Despite the hackathon timeline, we built a properly secured, well-documented API (with Swagger/OpenAPI), clean separation of concerns, and a CI-friendly codebase — this isn't a prototype, it's a foundation.

## What we learned

- **Geospatial data is a different beast.** Working with GeoJSON, spatial indexes, and distance-based queries taught us that location-aware applications require fundamentally different data modeling than traditional CRUD apps.
- **AI is a force multiplier, not a magic wand.** Gemini's image analysis is powerful, but it required careful prompt design, confidence thresholds, and graceful fallbacks to deliver a reliable user experience.
- **Mobile-first is a mindset, not a media query.** Designing for drivers who might be pulled over on the shoulder of a highway means every tap counts — we learned to ruthlessly prioritize the most critical actions.
- **Security and user experience are not at odds.** Firebase's authentication flow, combined with our backend token validation, gave us bank-grade security without adding friction to the user journey.
- **The power of full-stack ownership.** Having the team span frontend, backend, AI, and infrastructure meant we could make holistic design decisions that a fragmented team couldn't.

## What's next for HazardHub

- **Real-time push notifications** to alert drivers approaching a newly reported hazard on their current route, even if they didn't search for directions.
- **Dispatcher dashboard** for emergency services and DOT agencies to monitor active SOS events and hazard clusters in their jurisdiction.
- **Predictive hazard modeling** using historical data, weather feeds, and traffic patterns to warn drivers about likely danger zones before incidents happen.
- **Native mobile apps** (iOS and Android) for deeper OS integration — background location tracking, lock-screen SOS widgets, and CarPlay/Android Auto support.
- **Gamification and incentives** — reward active reporters with badges, leaderboard rankings, and potential partnerships with insurance companies offering safe-driver discounts.
- **Multi-language and international expansion** to serve communities worldwide, starting with regions with the highest traffic fatality rates.
- **Integration with connected vehicles** and IoT sensors for automatic hazard detection — potholes sensed by accelerometers, black ice detected by temperature sensors, and more.
