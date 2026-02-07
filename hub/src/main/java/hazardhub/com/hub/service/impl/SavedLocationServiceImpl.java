package hazardhub.com.hub.service.impl;

import hazardhub.com.hub.exception.ResourceNotFoundException;
import hazardhub.com.hub.mapper.SavedLocationMapper;
import hazardhub.com.hub.model.dto.SavedLocationDTO;
import hazardhub.com.hub.model.entity.SavedLocation;
import hazardhub.com.hub.repository.SavedLocationRepository;
import hazardhub.com.hub.service.SavedLocationService;
import hazardhub.com.hub.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedLocationServiceImpl implements SavedLocationService {

    private final SavedLocationRepository savedLocationRepository;
    private final UserService userService;

    @Override
    public SavedLocationDTO create(SavedLocationDTO savedLocationDTO) {
        try {
            if (!userService.existsById(savedLocationDTO.getUserId())) {
                throw new ResourceNotFoundException("User not found with id: " + savedLocationDTO.getUserId());
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify user existence", e);
        }

        SavedLocation savedLocation = SavedLocationMapper.toEntity(savedLocationDTO);
        SavedLocation saved = savedLocationRepository.save(savedLocation);
        return SavedLocationMapper.toDTO(saved);
    }

    @Override
    public Optional<SavedLocationDTO> findById(String id) {
        return savedLocationRepository.findById(id)
                .map(SavedLocationMapper::toDTO);
    }

    @Override
    public List<SavedLocationDTO> findAll() {
        return savedLocationRepository.findAll().stream()
                .map(SavedLocationMapper::toDTO)
                .toList();
    }

    @Override
    public List<SavedLocationDTO> findByUserId(String userId) {
        try {
            if (!userService.existsById(userId)) {
                throw new ResourceNotFoundException("User not found with id: " + userId);
            }
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify user existence", e);
        }

        return savedLocationRepository.findByUserId(userId).stream()
                .map(SavedLocationMapper::toDTO)
                .toList();
    }

    @Override
    public SavedLocationDTO update(String id, SavedLocationDTO savedLocationDTO) {
        SavedLocation existingLocation = savedLocationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SavedLocation not found with id: " + id));

        SavedLocationMapper.updateEntityFromDTO(savedLocationDTO, existingLocation);
        SavedLocation updated = savedLocationRepository.save(existingLocation);
        return SavedLocationMapper.toDTO(updated);
    }

    @Override
    public void delete(String id) {
        if (!savedLocationRepository.existsById(id)) {
            throw new ResourceNotFoundException("SavedLocation not found with id: " + id);
        }
        savedLocationRepository.deleteById(id);
    }
}
