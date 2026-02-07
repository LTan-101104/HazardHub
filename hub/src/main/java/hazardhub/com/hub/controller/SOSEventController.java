package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.SOSEventDTO;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import hazardhub.com.hub.service.SOSEventService;
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
@RequestMapping("/api/v1/sos-events")
@RequiredArgsConstructor
@Validated
@Tag(name = "SOS Events", description = "SOS event triggering and management")
public class SOSEventController {

    private final SOSEventService sosEventService;

    @PostMapping
    @Operation(summary = "Trigger a new SOS event")
    public ResponseEntity<SOSEventDTO> create(@Valid @RequestBody SOSEventDTO sosEventDTO) {
        SOSEventDTO created = sosEventService.create(sosEventDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an SOS event by ID")
    public ResponseEntity<SOSEventDTO> findById(@PathVariable String id) {
        return sosEventService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all SOS events")
    public ResponseEntity<List<SOSEventDTO>> findAll() {
        return ResponseEntity.ok(sosEventService.findAll());
    }

    @GetMapping("/paged")
    @Operation(summary = "Get all SOS events with pagination")
    public ResponseEntity<Page<SOSEventDTO>> findAllPaged(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sosEventService.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing SOS event")
    public ResponseEntity<SOSEventDTO> update(@PathVariable String id, @Valid @RequestBody SOSEventDTO sosEventDTO) {
        SOSEventDTO updated = sosEventService.update(id, sosEventDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an SOS event")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        sosEventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all SOS events by user ID")
    public ResponseEntity<List<SOSEventDTO>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(sosEventService.findByUserId(userId));
    }

    @GetMapping("/trip/{tripId}")
    @Operation(summary = "Get all SOS events by trip ID")
    public ResponseEntity<List<SOSEventDTO>> findByTripId(@PathVariable String tripId) {
        return ResponseEntity.ok(sosEventService.findByTripId(tripId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get all SOS events by status")
    public ResponseEntity<List<SOSEventDTO>> findByStatus(@PathVariable SOSEventStatus status) {
        return ResponseEntity.ok(sosEventService.findByStatus(status));
    }

    @GetMapping("/status/{status}/paged")
    @Operation(summary = "Get SOS events by status with pagination")
    public ResponseEntity<Page<SOSEventDTO>> findByStatusPaged(
            @PathVariable SOSEventStatus status,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(sosEventService.findByStatus(status, pageable));
    }

    @GetMapping("/nearby")
    @Operation(summary = "Find SOS events near a location")
    public ResponseEntity<List<SOSEventDTO>> findNearby(
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @NotNull @Positive Double maxDistanceMeters) {
        return ResponseEntity.ok(sosEventService.findNearby(longitude, latitude, maxDistanceMeters));
    }

    @GetMapping("/nearby/active")
    @Operation(summary = "Find active SOS events near a location")
    public ResponseEntity<List<SOSEventDTO>> findNearbyActive(
            @RequestParam @NotNull @DecimalMin("-180.0") @DecimalMax("180.0") Double longitude,
            @RequestParam @NotNull @DecimalMin("-90.0") @DecimalMax("90.0") Double latitude,
            @RequestParam @NotNull @Positive Double maxDistanceMeters) {
        return ResponseEntity.ok(sosEventService.findNearbyActive(longitude, latitude, maxDistanceMeters));
    }
}