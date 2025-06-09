package org.example.freelynk.service;

import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Project;
import org.example.freelynk.repository.FreelancerRepository;
import org.example.freelynk.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreelancerService {

    private final FreelancerRepository freelancerRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public FreelancerService(FreelancerRepository freelancerRepository, ProjectRepository projectRepository) {
        this.freelancerRepository = freelancerRepository;
        this.projectRepository = projectRepository;
    }



    public List<Freelancer> getFreelancers() {
        return freelancerRepository.findAll();
    }

    public Freelancer getFreelancerById(UUID freelancerId) {
        return freelancerRepository.findById(freelancerId).orElse(null);
    }

    public Freelancer getFreelancerByEmail(String email) {
        return freelancerRepository.findByEmail(email).orElse(null);
    }

public List<Freelancer> getFreelancersBySkills(List<String> skills) {
    List<Freelancer> result = new ArrayList<>();
    for (String skill : skills) {
        // Try exact match first
        List<String> lowerCaseSkills = Collections.singletonList(skill.toLowerCase());
        List<Freelancer> exactMatch = freelancerRepository.findFreelancersBySkills(lowerCaseSkills);
        
        if (exactMatch.isEmpty()) {
            // Try partial match
            List<Freelancer> partialMatch = freelancerRepository.findFreelancersBySkillContaining(skill);
            result.addAll(partialMatch);
        } else {
            result.addAll(exactMatch);
        }
    }
    return result.stream().distinct().collect(Collectors.toList());
}

    // Service method to get saved projects
    public List<Project> getSavedProject(Freelancer freelancer) {
        return freelancer.getSavedProjects();
    }

    // Service method to add/remove saved project
    public boolean toggleSavedProject(String freelancerEmail, UUID projectId) {
        try {
            Freelancer freelancer = freelancerRepository.findByEmail(freelancerEmail)
                    .orElseThrow(() -> new RuntimeException("Freelancer not found"));

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            List<Project> savedProjects = freelancer.getSavedProjects();

            if (savedProjects.contains(project)) {
                // Remove from saved projects
                savedProjects.remove(project);
                freelancerRepository.save(freelancer);
                return false; // Removed
            } else {
                // Add to saved projects
                savedProjects.add(project);
                freelancerRepository.save(freelancer);
                return true; // Added
            }
        } catch (Exception e) {
            throw new RuntimeException("Error toggling saved project: " + e.getMessage());
        }
    }

    // Service method to check if project is saved
    public boolean isProjectSaved(String freelancerEmail, UUID projectId) {
        Freelancer freelancer = freelancerRepository.findByEmail(freelancerEmail)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        return freelancer.getSavedProjects().stream()
                .anyMatch(project -> project.getId().equals(projectId));
    }


}