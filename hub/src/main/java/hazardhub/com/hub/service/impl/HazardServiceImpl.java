package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.HazardMapper;
import hazardhub.com.hub.model.dto.HazardDTO;
import hazardhub.com.hub.model.entity.Hazard;
import hazardhub.com.hub.model.enums.HazardStatus;
import hazardhub.com.hub.repository.HazardRepository;
import hazardhub.com.hub.service.HazardService;
import hazardhub.com.hub.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class HazardServiceImpl implements HazardService {

    private HazardRepository hazardRepository;
    private UserService userService;

    @Override
    public HazardDTO create(HazardDTO hazardDTO) {
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
        Hazard res = hazardRepository.save(hazard);
        return HazardMapper.toDTO(res);
    }

    @Override
    public Optional<HazardDTO> findById(String id) {
        return hazardRepository.findById(id)
                .map(HazardMapper::toDTO);
    }

    @Override
    public List<HazardDTO> findAll() {
        return hazardRepository.findAll().stream()
                .map(HazardMapper::toDTO)
                .toList();
    }

    @Override
    public Page<HazardDTO> findAll(Pageable pageable) {
        return hazardRepository.findAll(pageable)
                .map(HazardMapper::toDTO);
    }

    @Override
    public HazardDTO update(String id, HazardDTO hazardDTO) {
        Hazard existingHazard = hazardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Hazard not found with id: " + id));
        HazardMapper.updateEntityFromDTO(hazardDTO, existingHazard);
        Hazard updated = hazardRepository.save(existingHazard);
        return HazardMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        if (!hazardRepository.existsById(id)) {
            throw new ResourceNotFoundException("Hazard not found with id: " + id);
        }
        hazardRepository.deleteById(id);
    }

    @Override
    public List<HazardDTO> findByReporterId(String reporterId) {
        try {
            if (!userService.existsById(reporterId)) {
                throw new ResourceNotFoundException("User not found with id: " + reporterId);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify user existence", e);
        }
        return hazardRepository.findByReporterId(reporterId).stream()
                .map(HazardMapper::toDTO)
                .toList();
    }

    @Override
    public List<HazardDTO> findByStatus(HazardStatus status) {
        return hazardRepository.findByStatus(status).stream()
                .map(HazardMapper::toDTO)
                .toList();
    }

    @Override
    public Page<HazardDTO> findByStatus(HazardStatus status, Pageable pageable) {
        return hazardRepository.findByStatus(status, pageable)
                .map(HazardMapper::toDTO);
    }

    @Override
    public List<HazardDTO> findNearby(double longitude, double latitude, double maxDistanceMeters) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        return hazardRepository.findByLocationNear(point, maxDistanceMeters).stream()
                .map(HazardMapper::toDTO)
                .toList();
    }

    @Override
    public List<HazardDTO> findNearbyActive(double longitude, double latitude, double maxDistanceMeters) {
        GeoJsonPoint point = new GeoJsonPoint(longitude, latitude);
        return hazardRepository.findByLocationNearAndStatus(point, maxDistanceMeters, HazardStatus.ACTIVE).stream()
                .map(HazardMapper::toDTO)
                .toList();
    }
}
