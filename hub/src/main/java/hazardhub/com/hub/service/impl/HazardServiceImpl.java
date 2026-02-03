package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.mapper.HazardMapper;
import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.enums.HazardStatus;
import hazardhub.com.hub.repository.HazardRepository;
import hazardhub.com.hub.service.HazardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HazardServiceImpl implements HazardService {

    private final HazardRepository hazardRepository;
    private final HazardMapper hazardMapper;

    @Override
    public Hazard create(HazardDTO hazardDTO) {
        Hazard hazard = HazardMapper.toEntity(hazardDTO);
        if (hazard.getStatus() == null) {
            hazard.setStatus(HazardStatus.PENDING);
        }
        if (hazard.getVerificationCount() == null) {
            hazard.setVerificationCount(0);
        }
        if (hazard.getDisputeCount() == null) {
            hazard.setDisputeCount(0);
        }
        if (hazard.getAffectedRadiusMeters() == null) {
            hazard.setAffectedRadiusMeters(50.0);
        }
        return hazardRepository.save(hazard);
    }

    @Override
    public Optional<Hazard> findById(String id) {
        return hazardRepository.findById(id);
    }

    @Override
    public List<Hazard> findAll() {
        return hazardRepository.findAll();
    }

    @Override
    public Page<Hazard> findAll(Pageable pageable) {
        return hazardRepository.findAll(pageable);
    }

    @Override
    public Hazard update(String id, HazardDTO hazardDTO) {
        Hazard existingHazard = hazardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hazard not found with id: " + id));
        hazardMapper.updateEntityFromDTO(hazardDTO, existingHazard);
        return hazardRepository.save(existingHazard);
    }

    @Override
    public void delete(String id) {
        hazardRepository.deleteById(id);
    }

    @Override
    public List<Hazard> findByReporterId(String reporterId) {
        return hazardRepository.findByReporterId(reporterId);
    }

    @Override
    public List<Hazard> findByStatus(HazardStatus status) {
        return hazardRepository.findByStatus(status);
    }

    @Override
    public Page<Hazard> findByStatus(HazardStatus status, Pageable pageable) {
        return hazardRepository.findByStatus(status, pageable);
    }

    @Override
    public List<Hazard> findNearby(double longitude, double latitude, double maxDistanceMeters) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        return hazardRepository.findByLocationNear(point, maxDistanceMeters);
    }

    @Override
    public List<Hazard> findNearbyActive(double longitude, double latitude, double maxDistanceMeters) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        return hazardRepository.findByLocationNearAndStatus(point, maxDistanceMeters, HazardStatus.ACTIVE);
    }
}
