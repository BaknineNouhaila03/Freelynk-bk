package org.example.freelynk.repository;


import org.example.freelynk.model.SavedFreelancer;
import org.example.freelynk.dto.SavedFreelancerDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SavedFreelancerRepository extends JpaRepository<SavedFreelancer, Long> {

    @Query("SELECT new org.example.freelynk.dto.SavedFreelancerDTO(sf.freelancerId, f.firstName, f.description, f.occupation, f.rating, f.lastName, f.profileImage) " +
            "FROM SavedFreelancer sf JOIN Freelancer f ON sf.freelancerId = f.id " +
            "WHERE sf.clientId = :clientId")
    List<SavedFreelancerDTO> findSavedFreelancersByClientId(@Param("clientId") UUID clientId);
    
    // Check if a freelancer is already saved by a client
    Optional<SavedFreelancer> findByClientIdAndFreelancerId(UUID clientId, UUID freelancerId);
    
    // Find all saved freelancers for a client (entity only)
    List<SavedFreelancer> findByClientId(UUID clientId);
    
    // Delete a saved freelancer
    void deleteByClientIdAndFreelancerId(UUID clientId, UUID freelancerId);
    @Query("SELECT sf.freelancerId FROM SavedFreelancer sf WHERE sf.clientId = :clientId")
List<UUID> findFreelancerIdsByClientId(@Param("clientId") UUID clientId);
}