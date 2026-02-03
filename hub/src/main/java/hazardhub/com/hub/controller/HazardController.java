package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.enums.HazardStatus;
import hazardhub.com.hub.service.HazardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
@RequestMapping("/api/v1/hazards")
@RequiredArgsConstructor
@Validated
@Tag(name = "Hazards", description = "Hazard reporting and management")
public class HazardController {

    private final HazardService hazardService;

    @PostMapping
    @Operation(summary = "Create a new hazard report")
    public ResponseEntity<HazardDTO> create(@Valid @RequestBody HazardDTO hazardDTO) {
        HazardDTO created = hazardService.create(hazardDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a hazard by ID")
    public ResponseEntity<HazardDTO> findById(@PathVariable String id) {
        return hazardService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all hazards")
    public ResponseEntity<List<HazardDTO>> findAll() {
        return ResponseEntity.ok(hazardService.findAll());
    }

    @GetMapping("/paged")
    @Operation(summary = "Get all hazards with pagination")
    public ResponseEntity<Page<HazardDTO>> findAllPaged(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(hazardService.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing hazard")
    public ResponseEntity<HazardDTO> update(@PathVariable String id, @Valid @RequestBody HazardDTO hazardDTO) {
        HazardDTO updated = hazardService.update(id, hazardDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a hazard")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        hazardService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reporter/{reporterId}")
    @Operation(summary = "Get all hazards by reporter ID")
    public ResponseEntity<List<HazardDTO>> findByReporterId(@PathVariable String reporterId) {
        return ResponseEntity.ok(hazardService.findByReporterId(reporterId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get all hazards by status")
    public ResponseEntity<List<HazardDTO>> findByStatus(@PathVariable HazardStatus status) {
        return ResponseEntity.ok(hazardService.findByStatus(status));
    }

    @GetMapping("/status/{status}/paged")
    @Operation(summary = "Get hazards by status with pagination")
    public ResponseEntity<Page<HazardDTO>> findByStatusPaged(
            @PathVariable HazardStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(hazardService.findByStatus(status, pageable));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find hazards near a location")
    public ResponseEntity<List<HazardDTO>> findNearby(
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @NotNull @Positive Double maxDistanceMeters) {
        return ResponseEntity.ok(hazardService.findNearby(longitude, latitude, maxDistanceMeters));
    }

    @GetMapping("/nearby/active")
    @Operation(summary = "Find active hazards near a location")
    public ResponseEntity<List<HazardDTO>> findNearbyActive(
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @NotNull @Positive Double maxDistanceMeters) {
        return ResponseEntity.ok(hazardService.findNearbyActive(longitude, latitude, maxDistanceMeters));
    }
}
