package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.SOSEventDTO;
import hazardhub.com.hub.model.enums.SOSEventStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SOSEventService {

    SOSEventDTO create(SOSEventDTO sosEventDTO);

    Optional<SOSEventDTO> findById(String id);

    List<SOSEventDTO> findAll();

    Page<SOSEventDTO> findAll(Pageable pageable);

    SOSEventDTO update(String id, SOSEventDTO sosEventDTO);

    void delete(String id);

    List<SOSEventDTO> findByUserId(String userId);

    List<SOSEventDTO> findByTripId(String tripId);

    List<SOSEventDTO> findByStatus(SOSEventStatus status);

    Page<SOSEventDTO> findByStatus(SOSEventStatus status, Pageable pageable);

    List<SOSEventDTO> findNearby(double longitude, double latitude, double maxDistanceMeters);

    List<SOSEventDTO> findNearbyActive(double longitude, double latitude, double maxDistanceMeters);
}