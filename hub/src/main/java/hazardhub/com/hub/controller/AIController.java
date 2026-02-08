package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.ImageAnalysisRequestDTO;
import hazardhub.com.hub.model.dto.ImageAnalysisResponseDTO;
import hazardhub.com.hub.service.GeminiService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
@Validated
@Tag(name = "AI", description = "AI-powered hazard analysis")
public class AIController {

    private final GeminiService geminiService;

    @PostMapping("/analyze-hazard-image")
    @Operation(summary = "Analyze a hazard image using Gemini AI",
            description = "Takes a Firebase Storage image URL and returns an AI-generated hazard description")
    public ResponseEntity<ImageAnalysisResponseDTO> analyzeHazardImage(
            @Valid @RequestBody ImageAnalysisRequestDTO request) {
        ImageAnalysisResponseDTO response = geminiService.analyzeHazardImage(request.getImageUrl());
        return ResponseEntity.ok(response);
    }
}
