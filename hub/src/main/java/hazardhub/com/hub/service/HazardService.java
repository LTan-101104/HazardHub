package hazardhub.com.hub.service;

import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.enums.HazardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HazardService {

    Hazard create(Hazard hazard);

    Optional<Hazard> findById(String id);

    List<Hazard> findAll();

    Page<Hazard> findAll(Pageable pageable);

    Hazard update(String id, Hazard hazard);

    void delete(String id);

    List<Hazard> findByReporterId(String reporterId);

    List<Hazard> findByStatus(HazardStatus status);

    Page<Hazard> findByStatus(HazardStatus status, Pageable pageable);

    List<Hazard> findNearby(double longitude, double latitude, double maxDistanceMeters);

    List<Hazard> findNearbyActive(double longitude, double latitude, double maxDistanceMeters);
}
