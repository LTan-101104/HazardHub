package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.BadRequestException;
import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.HazardVerificationMapper;
import hazardhub.com.hub.model.dto.HazardVerificationDTO;
import hazardhub.com.hub.model.entity.HazardVerification;
import hazardhub.com.hub.model.enums.VerificationType;
import hazardhub.com.hub.repository.HazardVerificationRepository;
import hazardhub.com.hub.service.HazardVerificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HazardVerificationServiceImpl implements HazardVerificationService {

    private final HazardVerificationRepository hazardVerificationRepository;

    @Override
    public HazardVerificationDTO create(HazardVerificationDTO dto) {
        if (existsByHazardIdAndUserId(dto.getHazardId(), dto.getUserId())) {
            throw new BadRequestException("Verification already exists for hazardId: " + dto.getHazardId()
                    + " and userId: " + dto.getUserId());
        }
        HazardVerification entity = HazardVerificationMapper.toEntity(dto);
        HazardVerification saved = hazardVerificationRepository.save(entity);
        return HazardVerificationMapper.toDTO(saved);
    }

    @Override
    public Optional<HazardVerificationDTO> findById(String id) {
        return hazardVerificationRepository.findById(id)
                .map(HazardVerificationMapper::toDTO);
    }

    @Override
    public List<HazardVerificationDTO> findAll() {
        return hazardVerificationRepository.findAll().stream()
                .map(HazardVerificationMapper::toDTO)
                .toList();
    }

    @Override
    public Page<HazardVerificationDTO> findAll(Pageable pageable) {
        return hazardVerificationRepository.findAll(pageable)
                .map(HazardVerificationMapper::toDTO);
    }

    @Override
    public HazardVerificationDTO update(String id, HazardVerificationDTO dto) {
        HazardVerification existing = hazardVerificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("HazardVerification not found with id: " + id));
        HazardVerificationMapper.updateEntityFromDTO(dto, existing);
        HazardVerification updated = hazardVerificationRepository.save(existing);
        return HazardVerificationMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        if (!hazardVerificationRepository.existsById(id)) {
            throw new ResourceNotFoundException("HazardVerification not found with id: " + id);
        }
        hazardVerificationRepository.deleteById(id);
    }

    @Override
    public List<HazardVerificationDTO> findByHazardId(String hazardId) {
        return hazardVerificationRepository.findByHazardId(hazardId).stream()
                .map(HazardVerificationMapper::toDTO)
                .toList();
    }

    @Override
    public List<HazardVerificationDTO> findByUserId(String userId) {
        return hazardVerificationRepository.findByUserId(userId).stream()
                .map(HazardVerificationMapper::toDTO)
                .toList();
    }

    @Override
    public List<HazardVerificationDTO> findByVerificationType(VerificationType verificationType) {
        return hazardVerificationRepository.findByVerificationType(verificationType).stream()
                .map(HazardVerificationMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<HazardVerificationDTO> findByHazardIdAndUserId(String hazardId, String userId) {
        return hazardVerificationRepository.findByHazardIdAndUserId(hazardId, userId)
                .map(HazardVerificationMapper::toDTO);
    }

    @Override
    public List<HazardVerificationDTO> findByHazardIdAndVerificationType(String hazardId,
            VerificationType verificationType) {
        return hazardVerificationRepository.findByHazardIdAndVerificationType(hazardId, verificationType).stream()
                .map(HazardVerificationMapper::toDTO)
                .toList();
    }

    @Override
    public long countByHazardIdAndVerificationType(String hazardId, VerificationType verificationType) {
        return hazardVerificationRepository.countByHazardIdAndVerificationType(hazardId, verificationType);
    }

    @Override
    public boolean existsByHazardIdAndUserId(String hazardId, String userId) {
        return hazardVerificationRepository.existsByHazardIdAndUserId(hazardId, userId);
    }
}
