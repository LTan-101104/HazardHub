package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.SOSEventMapper;
import hazardhub.com.hub.model.dto.SOSEventDTO;
import hazardhub.com.hub.model.entity.SOSEvent;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import hazardhub.com.hub.repository.SOSEventRepository;
import hazardhub.com.hub.service.SOSEventService;
import hazardhub.com.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SOSEventServiceImpl implements SOSEventService {

    private final SOSEventRepository sosEventRepository;
    private final UserService userService;

    @Override
    public SOSEventDTO create(SOSEventDTO sosEventDTO) {
        SOSEvent sosEvent = SOSEventMapper.toEntity(sosEventDTO);
        if (sosEvent.getStatus() == null) {
            sosEvent.setStatus(SOSEventStatus.ACTIVE);
        }
        if (sosEvent.getTriggeredAt() == null) {
            sosEvent.setTriggeredAt(Instant.now());
        }
        if (sosEvent.getDispatchNotified() == null) {
            sosEvent.setDispatchNotified(false);
        }
        SOSEvent res = sosEventRepository.save(sosEvent);
        return SOSEventMapper.toDTO(res);
    }

    @Override
    public Optional<SOSEventDTO> findById(String id) {
        return sosEventRepository.findById(id)
                .map(SOSEventMapper::toDTO);
    }

    @Override
    public List<SOSEventDTO> findAll() {
        return sosEventRepository.findAll().stream()
                .map(SOSEventMapper::toDTO)
                .toList();
    }

    @Override
    public Page<SOSEventDTO> findAll(Pageable pageable) {
        return sosEventRepository.findAll(pageable)
                .map(SOSEventMapper::toDTO);
    }

    @Override
    public SOSEventDTO update(String id, SOSEventDTO sosEventDTO) {
        SOSEvent existingEvent = sosEventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SOS Event not found with id: " + id));
        SOSEventMapper.updateEntityFromDTO(sosEventDTO, existingEvent);
        SOSEvent updated = sosEventRepository.save(existingEvent);
        return SOSEventMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        if (!sosEventRepository.existsById(id)) {
            throw new ResourceNotFoundException("SOS Event not found with id: " + id);
        }
        sosEventRepository.deleteById(id);
    }

    @Override
    public List<SOSEventDTO> findByUserId(String userId) {
        try {
            if (!userService.existsById(userId)) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify user existence", e);
        }
        return sosEventRepository.findByUserId(userId).stream()
                .map(SOSEventMapper::toDTO)
                .toList();
    }

    @Override
    public List<SOSEventDTO> findByTripId(String tripId) {
        return sosEventRepository.findByTripId(tripId).stream()
                .map(SOSEventMapper::toDTO)
                .toList();
    }

    @Override
    public List<SOSEventDTO> findByStatus(SOSEventStatus status) {
        return sosEventRepository.findByStatus(status).stream()
                .map(SOSEventMapper::toDTO)
                .toList();
    }

    @Override
    public Page<SOSEventDTO> findByStatus(SOSEventStatus status, Pageable pageable) {
        return sosEventRepository.findByStatus(status, pageable)
                .map(SOSEventMapper::toDTO);
    }

    @Override
    public List<SOSEventDTO> findNearby(double longitude, double latitude, double maxDistanceMeters) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        return sosEventRepository.findByLocationNear(point, maxDistanceMeters).stream()
                .map(SOSEventMapper::toDTO)
                .toList();
    }

    @Override
    public List<SOSEventDTO> findNearbyActive(double longitude, double latitude, double maxDistanceMeters) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        return sosEventRepository.findByLocationNearAndStatus(point, maxDistanceMeters, SOSEventStatus.ACTIVE).stream()
                .map(SOSEventMapper::toDTO)
                .toList();
    }
}