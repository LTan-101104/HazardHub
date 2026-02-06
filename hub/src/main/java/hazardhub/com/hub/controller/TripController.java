package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.enums.TripStatus;
import hazardhub.com.hub.service.TripService;
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
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
@Validated
@Tag(name = "Trips", description = "Trip management")
public class TripController {

    private final TripService tripService;

    @PostMapping
    @Operation(summary = "Create a new trip")
    public ResponseEntity<TripDTO> create(@Valid @RequestBody TripDTO dto) {
        TripDTO created = tripService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a trip by ID")
    public ResponseEntity<TripDTO> findById(@PathVariable String id) {
        return tripService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all trips")
    public ResponseEntity<List<TripDTO>> findAll() {
        return ResponseEntity.ok(tripService.findAll());
    }

    @GetMapping("/paged")
    @Operation(summary = "Get all trips with pagination")
    public ResponseEntity<Page<TripDTO>> findAllPaged(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(tripService.findAll(pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing trip")
    public ResponseEntity<TripDTO> update(@PathVariable String id, @Valid @RequestBody TripDTO dto) {
        TripDTO updated = tripService.update(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a trip")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        tripService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all trips for a user")
    public ResponseEntity<List<TripDTO>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(tripService.findByUserId(userId));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get all trips by status")
    public ResponseEntity<List<TripDTO>> findByStatus(@PathVariable TripStatus status) {
        return ResponseEntity.ok(tripService.findByStatus(status));
    }

    @GetMapping("/user/{userId}/status/{status}")
    @Operation(summary = "Get all trips for a user by status")
    public ResponseEntity<List<TripDTO>> findByUserIdAndStatus(
            @PathVariable String userId,
            @PathVariable TripStatus status) {
        return ResponseEntity.ok(tripService.findByUserIdAndStatus(userId, status));
    }

    @GetMapping("/route/{selectedRouteId}")
    @Operation(summary = "Get a trip by selected route ID")
    public ResponseEntity<TripDTO> findBySelectedRouteId(@PathVariable String selectedRouteId) {
        return tripService.findBySelectedRouteId(selectedRouteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/count")
    @Operation(summary = "Count trips for a user")
    public ResponseEntity<Long> countByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(tripService.countByUserId(userId));
    }

    @GetMapping("/user/{userId}/status/{status}/count")
    @Operation(summary = "Count trips for a user by status")
    public ResponseEntity<Long> countByUserIdAndStatus(
            @PathVariable String userId,
            @PathVariable TripStatus status) {
        return ResponseEntity.ok(tripService.countByUserIdAndStatus(userId, status));
    }

    @GetMapping("/user/{userId}/status/{status}/exists")
    @Operation(summary = "Check if a user has a trip with specific status")
    public ResponseEntity<Boolean> existsByUserIdAndStatus(
            @PathVariable String userId,
            @PathVariable TripStatus status) {
        return ResponseEntity.ok(tripService.existsByUserIdAndStatus(userId, status));
    }
}