package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.RouteMapper;
import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;
import hazardhub.com.hub.repository.RouteRepository;
import hazardhub.com.hub.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final MongoTemplate mongoTemplate;

    @Override
    public RouteDTO create(RouteDTO routeDTO) {
        Route route = RouteMapper.toEntity(routeDTO);
        Route saved = routeRepository.save(route);
        return RouteMapper.toDTO(saved);
    }

    @Override
    public Optional<RouteDTO> findById(String id) {
        return routeRepository.findById(id).map(RouteMapper::toDTO);
    }

    @Override
    public List<RouteDTO> findByTripId(String tripId) {
        return routeRepository.findByTripId(tripId).stream()
                .map(RouteMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<RouteDTO> findSelectedByTripId(String tripId) {
        return routeRepository.findByTripIdAndIsSelectedTrue(tripId)
                .map(RouteMapper::toDTO);
    }

    @Override
    public RouteDTO update(String id, RouteDTO routeDTO) {
        Route existing = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        RouteMapper.updateEntityFromDTO(routeDTO, existing);
        Route saved = routeRepository.save(existing);
        return RouteMapper.toDTO(saved);
    }

    @Override
    public void delete(String id) {
        if (!routeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Route not found with id: " + id);
        }
        routeRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation detail: uses two atomic
     * {@code updateMulti}/{@code updateFirst}
     * calls via {@link MongoTemplate} rather than a read-modify-write cycle,
     * avoiding
     * race conditions where concurrent requests could select multiple routes.
     */
    @Override
    public RouteDTO selectRoute(String id) {
        Route target = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        // Atomically deselect all routes for this trip
        mongoTemplate.updateMulti(
                Query.query(Criteria.where("trip_id").is(target.getTripId())
                        .and("is_selected").is(true)),
                new Update().set("is_selected", false),
                Route.class);

        // Atomically select the target route
        mongoTemplate.updateFirst(
                Query.query(Criteria.where("_id").is(id)),
                new Update().set("is_selected", true),
                Route.class);

        return RouteMapper.toDTO(
                routeRepository.findById(id).orElse(target));
    }
}
