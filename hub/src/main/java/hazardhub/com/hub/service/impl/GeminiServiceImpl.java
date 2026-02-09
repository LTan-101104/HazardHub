package hazardhub.com.hub.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import hazardhub.com.hub.config.GeminiConfig;
import hazardhub.com.hub.constants.HazardHubConstants;
import hazardhub.com.hub.model.dto.ChatRequestDTO;
import hazardhub.com.hub.model.dto.ChatResponseDTO;
import hazardhub.com.hub.model.dto.ChatRouteOptionDTO;
import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;
import hazardhub.com.hub.model.dto.RouteSuggestionResponseDTO;
import hazardhub.com.hub.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.io.InputStream;
import java.net.URI;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/*
The current approach relies temporarily store the image in base64 format in-memory in server, this is not a long-term solution.
This is only implemented because gemini api is not config to read directly from firebase bucket yet. 
Will need some better, well-rounded solution in the future
*/
@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiServiceImpl implements GeminiService {

        private final RestClient geminiRestClient;
        private final GeminiConfig geminiConfig;
        private final ObjectMapper objectMapper;

        @Override
        public ImageAnalysisResponseDTO analyzeHazardImage(String imageUrl) {
                log.info("Analyzing hazard image: {}", imageUrl);

                Map<String, Object> requestBody = buildRequestBody(imageUrl);

                String uri = String.format("/models/%s:generateContent?key=%s",
                                geminiConfig.getModel(), geminiConfig.getApiKey());

                @SuppressWarnings("unchecked")
                Map<String, Object> response = geminiRestClient.post()
                                .uri(uri)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(requestBody)
                                .retrieve()
                                .body(Map.class);

                String description = extractTextFromResponse(response);

                log.info("Gemini analysis complete for image: {}", imageUrl);
                return ImageAnalysisResponseDTO.builder()
                                .description(description)
                                .build();
        }

        @Override
        public ChatResponseDTO chat(ChatRequestDTO request, RouteSuggestionResponseDTO routeSuggestion,
                        List<HazardDTO> hazards) {
                List<ChatRouteOptionDTO> routeOptions = mapRouteOptions(routeSuggestion);
                String prompt = buildChatPrompt(request, routeSuggestion, routeOptions, hazards);

                String reply = null;
                try {
                        Client client = new Client();
                        GenerateContentResponse response = client.models.generateContent(
                                        geminiConfig.getModel(),
                                        prompt,
                                        null);
                        reply = response != null ? response.text() : null;
                } catch (Exception e) {
                        log.error("Gemini chat generation failed", e);
                }

                if (reply == null || reply.isBlank()) {
                        reply = buildFallbackReply(routeSuggestion, routeOptions);
                }

                return ChatResponseDTO.builder()
                                .reply(reply.trim())
                                .routeOptions(routeOptions)
                                .build();
        }

        private static final long MAX_IMAGE_BYTES = 20 * 1024 * 1024; // 20 MB guard
        private static final String ALLOWED_HOST = "firebasestorage.googleapis.com";

        private String buildChatPrompt(ChatRequestDTO request,
                        RouteSuggestionResponseDTO routeSuggestion,
                        List<ChatRouteOptionDTO> routeOptions,
                        List<HazardDTO> hazards) {
                String routeSummary = routeSuggestion != null
                                && routeSuggestion.getMessage() != null
                                && !routeSuggestion.getMessage().isBlank()
                                                ? routeSuggestion.getMessage().trim()
                                                : "No precomputed route summary.";
                String routeOptionsJson = serializeRoutesForPrompt(routeOptions);
                String routeContextJson = serializeRouteContextForPrompt(request);
                String hazardsJson = serializeHazardsForPrompt(hazards);

                return """
                                %s

                                User message:
                                %s

                                Route context (origin, destination, vehicle):
                                %s

                                Nearby active hazards from backend:
                                %s

                                Route summary:
                                %s

                                Route options:
                                %s

                                Generate a single response for the user.
                                """.formatted(
                                HazardHubConstants.HazardGemini.CHAT_SYSTEM_PROMPT,
                                request.getMessage(),
                                routeContextJson,
                                hazardsJson,
                                routeSummary,
                                routeOptionsJson);
        }

        private String serializeRouteContextForPrompt(ChatRequestDTO request) {
                if (request == null || !request.hasRouteContext()) {
                        return "{}";
                }

                Map<String, Object> routeContext = new LinkedHashMap<>();
                routeContext.put("originLatitude", request.getOriginLatitude());
                routeContext.put("originLongitude", request.getOriginLongitude());
                routeContext.put("originAddress", request.getOriginAddress());
                routeContext.put("destinationLatitude", request.getDestinationLatitude());
                routeContext.put("destinationLongitude", request.getDestinationLongitude());
                routeContext.put("destinationAddress", request.getDestinationAddress());
                routeContext.put("vehicleType", request.getVehicleType() != null ? request.getVehicleType().name() : "CAR");

                try {
                        return objectMapper.writeValueAsString(routeContext);
                } catch (JsonProcessingException e) {
                        log.warn("Failed to serialize route context for prompt: {}", e.getMessage());
                        return "{}";
                }
        }

        private String serializeHazardsForPrompt(List<HazardDTO> hazards) {
                if (hazards == null || hazards.isEmpty()) {
                        return "[]";
                }

                List<Map<String, Object>> hazardSummaries = hazards.stream().map(hazard -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("id", hazard.getId());
                        item.put("severity", hazard.getSeverity() != null ? hazard.getSeverity().name() : null);
                        item.put("description", hazard.getDescription());
                        item.put("latitude", hazard.getLatitude());
                        item.put("longitude", hazard.getLongitude());
                        item.put("address", hazard.getAddress());
                        item.put("affectedRadiusMeters", hazard.getAffectedRadiusMeters());
                        item.put("verificationCount", hazard.getVerificationCount());
                        item.put("disputeCount", hazard.getDisputeCount());
                        return item;
                }).toList();

                try {
                        return objectMapper.writeValueAsString(hazardSummaries);
                } catch (JsonProcessingException e) {
                        log.warn("Failed to serialize hazards for prompt: {}", e.getMessage());
                        return "[]";
                }
        }

        private String serializeRoutesForPrompt(List<ChatRouteOptionDTO> routeOptions) {
                if (routeOptions == null || routeOptions.isEmpty()) {
                        return "[]";
                }

                List<Map<String, Object>> routeSummaries = routeOptions.stream().map(route -> {
                        Map<String, Object> item = new LinkedHashMap<>();
                        item.put("name", route.getName());
                        item.put("recommendationTier", route.getRecommendationTier());
                        item.put("safetyScore", route.getSafetyScore());
                        item.put("hazardCount", route.getHazardCount());
                        item.put("distanceMeters", route.getDistanceMeters());
                        item.put("durationSeconds", route.getDurationSeconds());
                        item.put("summary", route.getSummary());
                        return item;
                }).toList();

                try {
                        return objectMapper.writeValueAsString(routeSummaries);
                } catch (JsonProcessingException e) {
                        log.warn("Failed to serialize route options for prompt: {}", e.getMessage());
                        return "[]";
                }
        }

        private List<ChatRouteOptionDTO> mapRouteOptions(RouteSuggestionResponseDTO routeSuggestion) {
                if (routeSuggestion == null || routeSuggestion.getRoutes() == null || routeSuggestion.getRoutes().isEmpty()) {
                        return List.of();
                }

                return routeSuggestion.getRoutes().stream().map(route -> ChatRouteOptionDTO.builder()
                                .name(route.getName())
                                .recommendationTier(route.getRecommendationTier())
                                .safetyScore(route.getSafetyScore())
                                .hazardCount(route.getHazardCount())
                                .summary(route.getAiSummary())
                                .distanceMeters(route.getDistanceMeters())
                                .durationSeconds(route.getDurationSeconds())
                                .polyline(route.getPolyline())
                                .build()).toList();
        }

        private String buildFallbackReply(RouteSuggestionResponseDTO routeSuggestion, List<ChatRouteOptionDTO> routeOptions) {
                if (routeSuggestion != null
                                && routeSuggestion.getMessage() != null
                                && !routeSuggestion.getMessage().isBlank()) {
                        return routeSuggestion.getMessage();
                }

                if (routeOptions != null && !routeOptions.isEmpty()) {
                        return "I found route options and highlighted the safest tradeoffs for your trip.";
                }

                return "I can help with hazard-aware navigation. Share your route details and I will suggest safer options.";
        }

        private Map<String, Object> buildRequestBody(String imageUrl) {
                // SSRF guard — only allow downloads from Firebase Storage
                URI uri = URI.create(imageUrl);
                if (!"https".equalsIgnoreCase(uri.getScheme())
                                || !ALLOWED_HOST.equalsIgnoreCase(uri.getHost())) {
                        throw new IllegalArgumentException(
                                        "Image URL must be an HTTPS Firebase Storage URL");
                }

                // Download image and send as inline base64 — Firebase Storage URLs
                // aren't directly accessible to Gemini via gs:// or https://.
                byte[] imageBytes;
                try (InputStream in = uri.toURL().openStream()) {
                        imageBytes = in.readAllBytes();
                } catch (Exception e) {
                        log.error("Failed to download image from URL: {}", imageUrl, e);
                        throw new RuntimeException("Failed to download hazard image for analysis", e);
                }

                if (imageBytes.length > MAX_IMAGE_BYTES) {
                        throw new IllegalArgumentException(
                                        "Image too large for analysis (" + imageBytes.length / (1024 * 1024)
                                                        + " MB). Max: 20 MB.");
                }

                String base64Image = Base64.getEncoder().encodeToString(imageBytes);
                String mimeType = detectMimeType(imageUrl);

                Map<String, Object> imagePart = Map.of(
                                "inlineData", Map.of(
                                                "mimeType", mimeType,
                                                "data", base64Image));

                Map<String, Object> textPart = Map.of(
                                "text", "Describe the hazard in this image.");

                Map<String, Object> userContent = Map.of(
                                "role", "user",
                                "parts", List.of(imagePart, textPart));

                Map<String, Object> systemInstruction = Map.of(
                                "parts", List.of(Map.of("text", HazardHubConstants.HazardGemini.IMAGE_ANALYSIS_SYSTEM_PROMPT)));

                return Map.of(
                                "contents", List.of(userContent),
                                "systemInstruction", systemInstruction);
        }

        @SuppressWarnings("unchecked")
        private String extractTextFromResponse(Map<String, Object> response) {
                if (response == null) {
                        return "Unable to analyze the image.";
                }

                try {
                        List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
                        if (candidates == null || candidates.isEmpty()) {
                                return "Unable to analyze the image.";
                        }

                        Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                        if (content == null) {
                                return "Unable to analyze the image.";
                        }

                        List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                        if (parts == null || parts.isEmpty()) {
                                return "Unable to analyze the image.";
                        }

                        String text = (String) parts.get(0).get("text");
                        return text != null ? text.trim() : "Unable to analyze the image.";
                } catch (ClassCastException e) {
                        log.error("Failed to parse Gemini response", e);
                        return "Unable to analyze the image.";
                }
        }

        private String detectMimeType(String url) {
                String lower = url.toLowerCase();
                if (lower.contains(".png"))
                        return "image/png";
                if (lower.contains(".webp"))
                        return "image/webp";
                if (lower.contains(".gif"))
                        return "image/gif";
                return "image/jpeg";
        }
}
