package hazardhub.com.hub.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hazardhub.com.hub.config.GeminiConfig;
import hazardhub.com.hub.constants.HazardHubConstants;
import hazardhub.com.hub.model.dto.DirectionsParamsDTO;
import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionRequestDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionResponseDTO;
import hazardhub.com.hub.model.dto.SuggestedRouteDTO;
import hazardhub.com.hub.service.GoogleDirectionsService;
import hazardhub.com.hub.service.RouteSuggestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import hazardhub.com.hub.model.enums.VehicleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class RouteSuggestionServiceImpl implements RouteSuggestionService {

    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;
    private final GoogleDirectionsService googleDirectionsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RouteSuggestionResponseDTO suggestRoutes(RouteSuggestionRequestDTO request, List<HazardDTO> hazards) {
        // log.info("Suggesting routes from ({},{}) to ({},{}) with {} hazards",
        // request.getOriginLatitude(), request.getOriginLongitude(),
        // request.getDestinationLatitude(), request.getDestinationLongitude(),
        // hazards.size());

        // 1. Build prompts
        String systemPrompt = HazardHubConstants.HazardGemini.ROUTE_SUGGESTION_SYSTEM_PROMPT;
        String userPrompt = buildUserPrompt(request, hazards);

        // 2. Call Gemini for structured JSON
        Map<String, Object> geminiResponse = callGemini(systemPrompt, userPrompt);

        // 3. Parse Gemini's response
        RouteSuggestionResponseDTO suggestion = parseGeminiResponse(geminiResponse);

        // 4. For each route, call Directions API to get real polyline/distance/duration
        List<SuggestedRouteDTO> enrichedRoutes = new ArrayList<>();
        for (SuggestedRouteDTO route : suggestion.getRoutes()) {
            SuggestedRouteDTO enriched = enrichRouteWithDirections(route, request.getVehicleType());
            if (enriched.getPolyline() != null) {
                enrichedRoutes.add(enriched);
            } else {
                log.warn("Dropping route '{}' — no polyline after Directions API call", route.getName());
            }
        }

        return RouteSuggestionResponseDTO.builder()
                .message(suggestion.getMessage())
                .routes(enrichedRoutes)
                .build();
    }

    private String buildUserPrompt(RouteSuggestionRequestDTO request, List<HazardDTO> hazards) {
        String hazardsJson;
        try {
            List<Map<String, Object>> hazardSummaries = hazards.stream()
                    .map(h -> Map.<String, Object>of(
                            "latitude", h.getLatitude(),
                            "longitude", h.getLongitude(),
                            "severity", h.getSeverity().name(),
                            "description", h.getDescription(),
                            "affectedRadiusMeters", h.getAffectedRadiusMeters(),
                            "address", h.getAddress() != null ? h.getAddress() : ""))
                    .toList();
            hazardsJson = objectMapper.writeValueAsString(hazardSummaries);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize hazards to JSON", e);
            hazardsJson = "[]";
        }

        String originStr = request.getOriginLatitude() + "," + request.getOriginLongitude();
        if (request.getOriginAddress() != null && !request.getOriginAddress().isBlank()) {
            originStr += " (" + request.getOriginAddress() + ")";
        }

        String destStr = request.getDestinationLatitude() + "," + request.getDestinationLongitude();
        if (request.getDestinationAddress() != null && !request.getDestinationAddress().isBlank()) {
            destStr += " (" + request.getDestinationAddress() + ")";
        }

        String userMessage = request.getUserMessage() != null ? request.getUserMessage() : "None";

        return HazardHubConstants.HazardGemini.ROUTE_SUGGESTION_MAIN_PROMPT
                .replace("<current_location>", originStr)
                .replace("<destination>", destStr)
                .replace("<vehicle>", request.getVehicleType().name())
                .replace("<hazards>", hazardsJson)
                .replace("<user_message>", userMessage);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> callGemini(String systemPrompt, String userPrompt) {
        Map<String, Object> requestBody = Map.of(
                "contents", List.of(
                        Map.of("role", "user",
                                "parts", List.of(Map.of("text", userPrompt)))),
                "systemInstruction", Map.of(
                        "parts", List.of(Map.of("text", systemPrompt))),
                "generationConfig", Map.of(
                        "responseMimeType", "application/json"));

        String uri = String.format("/models/%s:generateContent?key=%s",
                geminiConfig.getModel(), geminiConfig.getApiKey());

        log.info("Calling Gemini for route suggestions");

        Map<String, Object> response = geminiRestClient.post()
                .uri(uri)
                .contentType(MediaType.APPLICATION_JSON)
                .body(requestBody)
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new RuntimeException("Gemini returned null response for route suggestion");
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    private RouteSuggestionResponseDTO parseGeminiResponse(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                throw new RuntimeException("No candidates in Gemini response");
            }

            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            String jsonText = (String) parts.get(0).get("text");

            log.info("Gemini raw response: {}", jsonText);

            // Parse the JSON text into our DTO
            Map<String, Object> parsed = objectMapper.readValue(jsonText, new TypeReference<>() {
            });

            String message = (String) parsed.get("message");
            List<Map<String, Object>> routeMaps = (List<Map<String, Object>>) parsed.get("routes");

            List<SuggestedRouteDTO> routes = new ArrayList<>();
            for (Map<String, Object> routeMap : routeMaps) {
                Map<String, Object> paramsMap = (Map<String, Object>) routeMap.get("directionsParams");

                DirectionsParamsDTO directionsParams = DirectionsParamsDTO.builder()
                        .origin((String) paramsMap.get("origin"))
                        .destination((String) paramsMap.get("destination"))
                        .waypoints((String) paramsMap.get("waypoints"))
                        .mode((String) paramsMap.get("mode"))
                        .build();

                SuggestedRouteDTO route = SuggestedRouteDTO.builder()
                        .name((String) routeMap.get("name"))
                        .recommendationTier((String) routeMap.get("recommendationTier"))
                        .safetyScore(toDouble(routeMap.get("safetyScore")))
                        .efficiencyScore(toDouble(routeMap.get("efficiencyScore")))
                        .rankScore(toDouble(routeMap.get("rankScore")))
                        .aiSummary((String) routeMap.get("aiSummary"))
                        .hazardCount(toInteger(routeMap.get("hazardCount")))
                        .directionsParams(directionsParams)
                        .build();

                routes.add(route);
            }

            return RouteSuggestionResponseDTO.builder()
                    .message(message)
                    .routes(routes)
                    .build();

        } catch (Exception e) {
            log.error("Failed to parse Gemini route suggestion response", e);
            throw new RuntimeException("Failed to parse Gemini route suggestion response", e);
        }
    }

    private SuggestedRouteDTO enrichRouteWithDirections(SuggestedRouteDTO route, VehicleType vehicleType) {
        DirectionsParamsDTO params = route.getDirectionsParams();
        if (params == null) {
            log.warn("Route '{}' has no directionsParams, skipping Directions API call", route.getName());
            return route;
        }

        String normalizedMode = normalizeMode(params.getMode(), vehicleType);
        params.setMode(normalizedMode);

        try {
            Map<String, Object> directionsResponse = googleDirectionsService.getDirections(
                    params.getOrigin(),
                    params.getDestination(),
                    params.getWaypoints() != null ? params.getWaypoints() : "",
                    normalizedMode);

            route.setPolyline(googleDirectionsService.extractPolyline(directionsResponse));
            route.setDistanceMeters(googleDirectionsService.extractDistanceMeters(directionsResponse));
            route.setDurationSeconds(googleDirectionsService.extractDurationSeconds(directionsResponse));

            log.info("Enriched route '{}': polyline={}, distance={}m, duration={}s",
                    route.getName(),
                    route.getPolyline() != null
                            ? route.getPolyline().substring(0, Math.min(30, route.getPolyline().length())) + "..."
                            : "null",
                    route.getDistanceMeters(),
                    route.getDurationSeconds());

        } catch (Exception e) {
            log.error("Failed to get directions for route '{}': {}", route.getName(), e.getMessage());
        }

        return route;
    }

    private static final Set<String> VALID_MODES = Set.of("driving", "bicycling", "walking");

    private String normalizeMode(String rawMode, VehicleType vehicleType) {
        if (rawMode != null) {
            String lower = rawMode.strip().toLowerCase();
            if (VALID_MODES.contains(lower)) {
                return lower;
            }
            // Handle common LLM variants
            if (lower.startsWith("driv") || lower.equals("car")) {
                return "driving";
            }
            if (lower.startsWith("bic") || lower.startsWith("cycl") || lower.equals("bike")) {
                return "bicycling";
            }
            if (lower.startsWith("walk") || lower.equals("foot") || lower.equals("pedestrian")) {
                return "walking";
            }
            log.warn("Unrecognized mode '{}' from Gemini, falling back to vehicleType={}", rawMode, vehicleType);
        }
        return switch (vehicleType) {
            case CAR -> "driving";
            case BICYCLE -> "bicycling";
            case WALKING -> "walking";
        };
    }

    private Double toDouble(Object value) {
        if (value == null)
            return null;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        return Double.parseDouble(value.toString());
    }

    private Integer toInteger(Object value) {
        if (value == null)
            return null;
        if (value instanceof Number)
            return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }
}
