package org.example.freelynk.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Gig;
import org.example.freelynk.model.Project;
import java.util.List;
import java.util.Optional;


public interface GigRepository extends JpaRepository<Gig, UUID> {

    Optional<Gig> findById(UUID id);
    List<Gig> findByFreelancer(Freelancer freelancer);

    
}
