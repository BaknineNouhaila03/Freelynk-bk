package org.example.freelynk.service;

import org.example.freelynk.dto.SavedFreelancerDTO;
import org.example.freelynk.model.SavedFreelancer;
import org.example.freelynk.repository.SavedFreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class SavedFreelancerService {
    
    @Autowired
    private SavedFreelancerRepository savedFreelancerRepository;
    
    // Get all saved freelancers for a client
    public List<SavedFreelancerDTO> getSavedFreelancersByClientId(UUID clientId) {
        return savedFreelancerRepository.findSavedFreelancersByClientId(clientId);
    }
    
    // Save a freelancer for a client
    public SavedFreelancer saveFreelancer(UUID clientId, UUID freelancerId) {
        // Check if already saved
        Optional<SavedFreelancer> existing = savedFreelancerRepository
            .findByClientIdAndFreelancerId(clientId, freelancerId);
        
        if (existing.isPresent()) {
            throw new RuntimeException("Freelancer already saved");
        }
        
        SavedFreelancer savedFreelancer = new SavedFreelancer(clientId, freelancerId);
        return savedFreelancerRepository.save(savedFreelancer);
    }
    
    // Remove a saved freelancer
    @Transactional
    public void removeSavedFreelancer(UUID clientId, UUID freelancerId) {
        savedFreelancerRepository.deleteByClientIdAndFreelancerId(clientId, freelancerId);
    }
    
    // Check if freelancer is saved by client
    public boolean isFreelancerSaved(UUID clientId, UUID freelancerId) {
        return savedFreelancerRepository
            .findByClientIdAndFreelancerId(clientId, freelancerId)
            .isPresent();
    }
}
