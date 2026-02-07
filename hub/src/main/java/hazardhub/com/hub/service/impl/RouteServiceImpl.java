package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.RouteMapper;
import hazardhub.com.hub.model.dto.RouteDTO;
import hazardhub.com.hub.model.entity.Route;
import hazardhub.com.hub.repository.RouteRepository;
import hazardhub.com.hub.service.RouteService;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AggregationUpdate;
import org.springframework.data.mongodb.core.aggregation.ComparisonOperators;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
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

    @Override
    public RouteDTO selectRoute(String id) {
        Route target = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));

        AggregationUpdate update = AggregationUpdate.update()
                .set("is_selected").toValueOf(
                        ComparisonOperators.valueOf("$_id").equalToValue(new ObjectId(id)));

        mongoTemplate.updateMulti(
                Query.query(Criteria.where("trip_id").is(target.getTripId())),
                update,
                Route.class);

        Route selected = routeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Route not found with id: " + id));
        return RouteMapper.toDTO(selected);
    }
}
