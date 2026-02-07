package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.SavedLocationDTO;
import hazardhub.com.hub.service.SavedLocationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saved-locations")
@RequiredArgsConstructor
@Validated
@Tag(name = "Saved Locations", description = "User saved locations management")
public class SavedLocationController {

    private final SavedLocationService savedLocationService;

    @PostMapping
    @Operation(summary = "Create a new saved location")
    public ResponseEntity<SavedLocationDTO> create(@Valid @RequestBody SavedLocationDTO savedLocationDTO) {
        SavedLocationDTO created = savedLocationService.create(savedLocationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a saved location by ID")
    public ResponseEntity<SavedLocationDTO> findById(@PathVariable String id) {
        return savedLocationService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    @Operation(summary = "Get all saved locations")
    public ResponseEntity<List<SavedLocationDTO>> findAll() {
        return ResponseEntity.ok(savedLocationService.findAll());
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all saved locations by user ID")
    public ResponseEntity<List<SavedLocationDTO>> findByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(savedLocationService.findByUserId(userId));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing saved location")
    public ResponseEntity<SavedLocationDTO> update(@PathVariable String id, @Valid @RequestBody SavedLocationDTO savedLocationDTO) {
        SavedLocationDTO updated = savedLocationService.update(id, savedLocationDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a saved location")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        savedLocationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
