package hazardhub.com.hub.controller;

import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/routes")
@RequiredArgsConstructor
@Validated
@Tag(name = "Routes", description = "Trip routes with safety scoring")
public class RouteController {

    private final RouteService routeService;

    @PostMapping
    @Operation(summary = "Create a new route for a trip")
    public ResponseEntity<RouteDTO> create(@Valid @RequestBody RouteDTO routeDTO) {
        RouteDTO created = routeService.create(routeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get route by ID")
    public ResponseEntity<RouteDTO> findById(@PathVariable String id) {
        return routeService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/trip/{tripId}")
    @Operation(summary = "List routes for a trip")
    public ResponseEntity<List<RouteDTO>> findByTripId(@PathVariable @NotBlank String tripId) {
        return ResponseEntity.ok(routeService.findByTripId(tripId));
    }

    @GetMapping("/trip/{tripId}/selected")
    @Operation(summary = "Get the selected route for a trip")
    public ResponseEntity<RouteDTO> findSelectedByTripId(@PathVariable @NotBlank String tripId) {
        return routeService.findSelectedByTripId(tripId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a route")
    public ResponseEntity<RouteDTO> update(@PathVariable String id, @Valid @RequestBody RouteDTO routeDTO) {
        RouteDTO updated = routeService.update(id, routeDTO);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a route")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/select")
    @Operation(summary = "Mark a route as selected for its trip")
    public ResponseEntity<RouteDTO> select(@PathVariable String id) {
        RouteDTO selected = routeService.selectRoute(id);
        return ResponseEntity.ok(selected);
    }
}
