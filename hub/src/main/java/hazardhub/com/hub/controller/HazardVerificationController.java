package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardVerificationDTO;
import hazardhub.com.hub.model.enums.VerificationType;
import hazardhub.com.hub.service.HazardVerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/hazard-verifications")
@RequiredArgsConstructor
@Validated
@Tag(name = "Hazard Verifications", description = "Hazard verification and dispute management")
public class HazardVerificationController {

    private final HazardVerificationService hazardVerificationService;

    @PostMapping
    @Operation(summary = "Create a new hazard verification")
    public ResponseEntity<HazardVerificationDTO> create(@Valid @RequestBody HazardVerificationDTO dto) {
        HazardVerificationDTO created = hazardVerificationService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a hazard verification by ID")
    public ResponseEntity<HazardVerificationDTO> findById(@PathVariable String id) {
        return hazardVerificationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all hazard verifications")
    public ResponseEntity<List<HazardVerificationDTO>> findAll() {
        return ResponseEntity.ok(hazardVerificationService.findAll());
    }

    @GetMapping("/paged")
    @Operation(summary = "Get all hazard verifications with pagination")
    public ResponseEntity<Page<HazardVerificationDTO>> findAllPaged(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(hazardVerificationService.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing hazard verification")
    public ResponseEntity<HazardVerificationDTO> update(@PathVariable String id, @Valid @RequestBody HazardVerificationDTO dto) {
        HazardVerificationDTO updated = hazardVerificationService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hazard verification")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        hazardVerificationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/hazard/{hazardId}")
    @Operation(summary = "Get all verifications for a hazard")
    public ResponseEntity<List<HazardVerificationDTO>> findByHazardId(@PathVariable String hazardId) {
        return ResponseEntity.ok(hazardVerificationService.findByHazardId(hazardId));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all verifications by a user")
    public ResponseEntity<List<HazardVerificationDTO>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(hazardVerificationService.findByUserId(userId));
    }

    @GetMapping("/type/{verificationType}")
    @Operation(summary = "Get all verifications by type")
    public ResponseEntity<List<HazardVerificationDTO>> findByVerificationType(@PathVariable VerificationType verificationType) {
        return ResponseEntity.ok(hazardVerificationService.findByVerificationType(verificationType));
    }

    @GetMapping("/hazard/{hazardId}/user/{userId}")
    @Operation(summary = "Get a user's verification for a specific hazard")
    public ResponseEntity<HazardVerificationDTO> findByHazardIdAndUserId(
            @PathVariable String hazardId,
            @PathVariable String userId) {
        return hazardVerificationService.findByHazardIdAndUserId(hazardId, userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/hazard/{hazardId}/type/{verificationType}")
    @Operation(summary = "Get all verifications for a hazard by type")
    public ResponseEntity<List<HazardVerificationDTO>> findByHazardIdAndVerificationType(
            @PathVariable String hazardId,
            @PathVariable VerificationType verificationType) {
        return ResponseEntity.ok(hazardVerificationService.findByHazardIdAndVerificationType(hazardId, verificationType));
    }

    @GetMapping("/hazard/{hazardId}/type/{verificationType}/count")
    @Operation(summary = "Count verifications for a hazard by type")
    public ResponseEntity<Long> countByHazardIdAndVerificationType(
            @PathVariable String hazardId,
            @PathVariable VerificationType verificationType) {
        return ResponseEntity.ok(hazardVerificationService.countByHazardIdAndVerificationType(hazardId, verificationType));
    }

    @GetMapping("/hazard/{hazardId}/user/{userId}/exists")
    @Operation(summary = "Check if a user has verified a specific hazard")
    public ResponseEntity<Boolean> existsByHazardIdAndUserId(
            @PathVariable String hazardId,
            @PathVariable String userId) {
        return ResponseEntity.ok(hazardVerificationService.existsByHazardIdAndUserId(hazardId, userId));
    }
}
