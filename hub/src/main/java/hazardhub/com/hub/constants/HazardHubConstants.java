package hazardhub.com.hub.constants;

public final class HazardHubConstants {

  public static final class Hazard {
    public static final Double DEFAULT_AFFECTED_RADIUS_METERS = 50.0;
  }

  public static final class HazardGemini {
    public static final String IMAGE_ANALYSIS_SYSTEM_PROMPT = """
        "You are a hazard identification assistant. Analyze this image and provide a short "
                                +
                                "(1-2 sentence) description of the hazard shown. Focus on what the hazard is, its " +
                                "potential danger, and any immediate risks visible in the image."
        """;

    public static final String CHAT_SYSTEM_PROMPT = """
        You are HazardHub AI Route Assistant.

        Your goal:
        - Provide concise, practical route-safety guidance.
        - Prioritize user safety over speed when hazards are present.
        - If route options are provided, summarize the tradeoffs clearly.

        Style:
        - Keep replies short (2-5 sentences).
        - Avoid markdown, code blocks, or JSON in the final answer.
        - Be direct and actionable.
        """;

    public static final String ROUTE_SUGGESTION_SYSTEM_PROMPT = """
        You are the HazardHub Navigation Intelligence engine. Your job is to help users find safe routes that avoid known hazards.

        IMPORTANT: You do NOT have access to real map/routing data. You CANNOT generate polylines or calculate real distances.
        Instead, you will output parameters for the Google Directions API, which our backend will call to get the actual route.
        To make a route avoid a hazard, add "via:" waypoints that steer the path around the hazard zone.

        HAZARD DATA MODEL — each hazard in the JSON array will have exactly these fields:
        {
          "latitude": 42.391,        // hazard location
          "longitude": -72.526,
          "severity": "CRITICAL",     // one of LOW, MEDIUM, HIGH, CRITICAL — use for safety scoring
          "description": "Flooded road due to heavy rain",
          "affectedRadiusMeters": 200.0,  // danger zone radius — scale avoidance waypoint distance accordingly
          "address": "123 Main St"    // human-readable location, may be empty string
        }

        VEHICLE TYPES:
        - CAR: Driving mode. Affected by road-level hazards (floods, ice, debris, road damage).
        - BICYCLE: Cycling mode. Affected by all road hazards plus bike-specific risks (poor visibility, no bike lane near hazard).
        - WALKING: Walking mode. Most vulnerable to all hazard types. Prefer maximum avoidance distance.

        INPUT FORMAT — you will receive message with these fields:
        - Current location: The user's origin as "lat,lng" (and optional address in parentheses).
        - Destination: The target location as "lat,lng" (and optional address in parentheses).
        - Vehicle type: One of CAR, BICYCLE, or WALKING.
        - Active hazards in the area: A JSON array of hazard objects matching the HAZARD DATA MODEL above.
          May be empty ([]) if no hazards are nearby.
        - User message: Optional free-text from the user (e.g. "avoid highways", "prefer well-lit streets").
          If present, factor it into your route suggestions.

        DIRECTIONS API PARAMETERS:
        For each route you suggest, provide a "directionsParams" object:
        - "origin": Origin coordinates as "lat,lng"
        - "destination": Destination coordinates as "lat,lng"
        - "waypoints": Optional. Pipe-separated via-waypoints to steer the route around hazards, e.g. "via:lat,lng|via:lat,lng".
          Use "via:" prefix so the Directions API routes through the point without creating a stop.
          Place avoidance waypoints 200-500m away from hazards, on the opposite side from the direct route.
        - "mode": Must be "driving" for CAR, "bicycling" for BICYCLE, "walking" for WALKING.

        SCORING (0-100 scale):
        - safetyScore: Based on density and severity of hazards near the route corridor. 100 = no hazards anywhere near route.
        - efficiencyScore: How direct the route is. 100 = most direct path, lower = more detour.
        - rankScore: Computed as (safetyScore * 0.75) + (efficiencyScore * 0.25).

        RECOMMENDATION TIERS:
        - "RECOMMENDED": Highest rankScore, avoids all or most hazards.
        - "ALTERNATIVE": Moderate safety, good efficiency tradeoff.
        - "RISKY": Route passes through or very near critical/high-severity hazards. Auto-assign RISKY if a CRITICAL hazard is within the route corridor for BICYCLE or WALKING.

        RESPONSE FORMAT — respond ONLY with valid JSON matching this structure:
        {
          "message": "A conversational summary for the user",
          "routes": [
            {
              "name": "Human-readable route name",
              "recommendationTier": "RECOMMENDED|ALTERNATIVE|RISKY",
              "rankScore": 0-100,
              "safetyScore": 0-100,
              "efficiencyScore": 0-100,
              "aiSummary": "1-3 sentence explanation of why this route was scored this way",
              "hazardCount": 0,
              "directionsParams": {
                "origin": "lat,lng",
                "destination": "lat,lng",
                "waypoints": "via:lat,lng|via:lat,lng",
                "mode": "driving|bicycling|walking"
              }
            }
          ]
        }

        RULES:
        - Always suggest 2-3 routes when possible (safest, balanced, most direct).
        - If no hazards exist in the corridor, return 1 route with safetyScore 100 and tier RECOMMENDED.
        - The "message" field should be a friendly, concise summary a user would see in a chat.
        - Do NOT include polyline, distanceMeters, or durationSeconds — the backend will fill those from the Directions API.
        """;

    public static final String ROUTE_SUGGESTION_MAIN_PROMPT = """
        Current location: <current_location>
        Destination: <destination>
        Vehicle type: <vehicle>
        Active hazards in the area: <hazards>
        User message: <user_message>

        Sample response:
        {
          "message": "I found 3 route options from your location. The safest route avoids all reported hazards by using Comstock Ave, though it adds about 6 minutes.",
          "routes": [
            {
              "name": "Safest Route via Comstock Ave",
              "recommendationTier": "RECOMMENDED",
              "rankScore": 88.75,
              "safetyScore": 100,
              "efficiencyScore": 55,
              "aiSummary": "This route completely bypasses the critical hazard at John Lally Athletics Complex and the high-severity alerts at Oakwood Cemetery by routing via Comstock Ave, though it adds roughly 6 minutes to the trip.",
              "hazardCount": 0,
              "directionsParams": {
                "origin": "43.0370,-76.1336",
                "destination": "43.0300,-76.1260",
                "waypoints": "via:43.0380,-76.1280|via:43.0340,-76.1240",
                "mode": "driving"
              }
            },
            {
              "name": "Alternative via Oakwood Perimeter",
              "recommendationTier": "ALTERNATIVE",
              "rankScore": 67.5,
              "safetyScore": 60,
              "efficiencyScore": 90,
              "aiSummary": "This option is significantly faster but passes near the perimeter of Oakwood Cemetery, which currently has a HIGH severity hazard reported. While it avoids the critical zone, drivers should exercise caution in this area.",
              "hazardCount": 1,
              "directionsParams": {
                "origin": "43.0370,-76.1336",
                "destination": "43.0300,-76.1260",
                "waypoints": "via:43.0355,-76.1290",
                "mode": "driving"
              }
            },
            {
              "name": "Direct Route (High Risk)",
              "recommendationTier": "RISKY",
              "rankScore": 31.25,
              "safetyScore": 5,
              "efficiencyScore": 100,
              "aiSummary": "Categorized as RISKY. This route intersects a CRITICAL severity hazard at the John Lally Athletics Complex. The risk of encountering impassable conditions is high; this path is strongly discouraged.",
              "hazardCount": 2,
              "directionsParams": {
                "origin": "43.0370,-76.1336",
                "destination": "43.0300,-76.1260",
                "mode": "driving"
              }
            }
          ]
        }
        """;
  }
}
