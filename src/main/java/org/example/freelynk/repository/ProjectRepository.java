package org.example.freelynk.repository;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.Project;
import org.example.freelynk.model.ProjectStatus;
import org.example.freelynk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {    // Filter projects by budget range (matching PDF filters)
@Query("SELECT p FROM Project p WHERE p.minBudget >= :min AND p.maxBudget <= :max")
List<Project> findByBudgetRange(@Param("min") Double min, @Param("max") Double max);

    // Filter by required skills (e.g., "Graphic Design")
    @Query("SELECT p FROM Project p JOIN p.requiredSkills s WHERE s IN :skills")
    List<Project> findByRequiredSkills(@Param("skills") List<String> skills);

    List<Project> findByClient(Client client);
    List<Project> findAll();
    Optional<Project> findById(UUID id);
    // In ProjectRepository.java
List<Project> findByFreelancerId(UUID freelancerId);

    // Find projects that are not assigned to any freelancer (available projects)
    List<Project> findByFreelancerIsNull();

    // You can also add a method to find projects by status if needed
    List<Project> findByStatus(ProjectStatus status);

    // Find available projects by status
    List<Project> findByFreelancerIsNullAndStatus(ProjectStatus status);


}