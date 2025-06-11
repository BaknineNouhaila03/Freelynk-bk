package org.example.freelynk.repository;

import org.example.freelynk.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, UUID> {

    @Query("SELECT DISTINCT f FROM Freelancer f JOIN f.skills s WHERE LOWER(s) IN :skills")
    List<Freelancer> findFreelancersBySkills(@Param("skills") List<String> skills);
    @Query("SELECT f FROM Freelancer f JOIN f.skills s WHERE LOWER(s) LIKE LOWER(CONCAT('%', :skill, '%'))")
    List<Freelancer> findFreelancersBySkillContaining(@Param("skill") String skill);

    Optional<Freelancer> findByEmail(String email);
    List<Freelancer> findByOccupationIgnoreCase(String occupation);
    List<Freelancer> findByOccupationContainingIgnoreCase(String occupation);
    
}