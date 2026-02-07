package hazardhub.com.hub.service;

import hazardhub.com.hub.model.dto.HazardDTO;
// import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.enums.HazardStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface HazardService {

    HazardDTO create(HazardDTO hazardDTO);

    Optional<HazardDTO> findById(String id);

    List<HazardDTO> findAll();

    Page<HazardDTO> findAll(Pageable pageable);

    HazardDTO update(String id, HazardDTO hazardDTO);

    void delete(String id);

    List<HazardDTO> findByReporterId(String reporterId);

    List<HazardDTO> findByStatus(HazardStatus status);

    Page<HazardDTO> findByStatus(HazardStatus status, Pageable pageable);

    List<HazardDTO> findNearby(double longitude, double latitude, double maxDistanceMeters);

    List<HazardDTO> findNearbyActive(double longitude, double latitude, double maxDistanceMeters);
}
