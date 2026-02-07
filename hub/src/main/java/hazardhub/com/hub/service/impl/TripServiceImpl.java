package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.TripMapper;
import hazardhub.com.hub.model.dto.TripDTO;
import hazardhub.com.hub.model.entity.Trip;
import hazardhub.com.hub.model.enums.TripStatus;
import hazardhub.com.hub.repository.TripRepository;
import hazardhub.com.hub.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TripServiceImpl implements TripService {

    private final TripRepository tripRepository;

    @Override
    public TripDTO create(TripDTO dto) {
        Trip entity = TripMapper.toEntity(dto);
        Trip saved = tripRepository.save(entity);
        return TripMapper.toDTO(saved);
    }

    @Override
    public Optional<TripDTO> findById(String id) {
        return tripRepository.findById(id)
                .map(TripMapper::toDTO);
    }

    @Override
    public List<TripDTO> findAll() {
        return tripRepository.findAll().stream()
                .map(TripMapper::toDTO)
                .toList();
    }

    @Override
    public Page<TripDTO> findAll(Pageable pageable) {
        return tripRepository.findAll(pageable)
                .map(TripMapper::toDTO);
    }

    @Override
    public TripDTO update(String id, TripDTO dto) {
        Trip existing = tripRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Trip not found with id: " + id));
        TripMapper.updateEntityFromDTO(dto, existing);
        Trip updated = tripRepository.save(existing);
        return TripMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        if (!tripRepository.existsById(id)) {
            throw new ResourceNotFoundException("Trip not found with id: " + id);
        }
        tripRepository.deleteById(id);
    }

    @Override
    public List<TripDTO> findByUserId(String userId) {
        return tripRepository.findByUserId(userId).stream()
                .map(TripMapper::toDTO)
                .toList();
    }

    @Override
    public List<TripDTO> findByStatus(TripStatus status) {
        return tripRepository.findByStatus(status).stream()
                .map(TripMapper::toDTO)
                .toList();
    }

    @Override
    public List<TripDTO> findByUserIdAndStatus(String userId, TripStatus status) {
        return tripRepository.findByUserIdAndStatus(userId, status).stream()
                .map(TripMapper::toDTO)
                .toList();
    }

    @Override
    public Optional<TripDTO> findBySelectedRouteId(String selectedRouteId) {
        return tripRepository.findBySelectedRouteId(selectedRouteId)
                .map(TripMapper::toDTO);
    }

    @Override
    public long countByUserId(String userId) {
        return tripRepository.countByUserId(userId);
    }

    @Override
    public long countByUserIdAndStatus(String userId, TripStatus status) {
        return tripRepository.countByUserIdAndStatus(userId, status);
    }

    @Override
    public boolean existsByUserIdAndStatus(String userId, TripStatus status) {
        return tripRepository.existsByUserIdAndStatus(userId, status);
    }
}