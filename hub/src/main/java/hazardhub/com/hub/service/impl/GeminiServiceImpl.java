package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.config.GeminiConfig;
import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;
import hazardhub.com.hub.service.GeminiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class GeminiServiceImpl implements GeminiService {

    private final RestClient geminiRestClient;
    private final GeminiConfig geminiConfig;

    private static final String SYSTEM_PROMPT =
            "You are a hazard identification assistant. Analyze this image and provide a short " +
            "(1-2 sentence) description of the hazard shown. Focus on what the hazard is, its " +
            "potential danger, and any immediate risks visible in the image.";

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

    private Map<String, Object> buildRequestBody(String imageUrl) {
        Map<String, Object> imagePart = Map.of(
                "fileData", Map.of(
                        "mimeType", "image/jpeg",
                        "fileUri", imageUrl
                )
        );

        Map<String, Object> textPart = Map.of(
                "text", "Describe the hazard in this image."
        );

        Map<String, Object> userContent = Map.of(
                "role", "user",
                "parts", List.of(imagePart, textPart)
        );

        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", SYSTEM_PROMPT))
        );

        return Map.of(
                "contents", List.of(userContent),
                "systemInstruction", systemInstruction
        );
    }

    @SuppressWarnings("unchecked")
    private String extractTextFromResponse(Map<String, Object> response) {
        if (response == null) {
            return "Unable to analyze the image.";
        }

        try {
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) response.get("candidates");
            if (candidates == null || candidates.isEmpty()) {
                return "Unable to analyze the image.";
            }

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");
            if (content == null) {
                return "Unable to analyze the image.";
            }

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");
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
}
