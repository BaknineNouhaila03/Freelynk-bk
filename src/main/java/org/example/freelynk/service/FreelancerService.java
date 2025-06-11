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
        List<String> lowerCaseSkills = Collections.singletonList(skill.toLowerCase());
        List<Freelancer> exactMatch = freelancerRepository.findFreelancersBySkills(lowerCaseSkills);
        
        if (exactMatch.isEmpty()) {
            List<Freelancer> partialMatch = freelancerRepository.findFreelancersBySkillContaining(skill);
            result.addAll(partialMatch);
        } else {
            result.addAll(exactMatch);
        }
    }
    return result.stream().distinct().collect(Collectors.toList());
}
public List<Freelancer> getFreelancersByOccupation(String occupation) {
        List<Freelancer> exactMatch = freelancerRepository.findByOccupationIgnoreCase(occupation);
    
    if (exactMatch.isEmpty()) {
        return freelancerRepository.findByOccupationContainingIgnoreCase(occupation);
    }
    
    return exactMatch;
}

    public List<Project> getSavedProject(Freelancer freelancer) {
        return freelancer.getSavedProjects();
    }

    public boolean toggleSavedProject(String freelancerEmail, UUID projectId) {
        try {
            Freelancer freelancer = freelancerRepository.findByEmail(freelancerEmail)
                    .orElseThrow(() -> new RuntimeException("Freelancer not found"));

            Project project = projectRepository.findById(projectId)
                    .orElseThrow(() -> new RuntimeException("Project not found"));

            List<Project> savedProjects = freelancer.getSavedProjects();

            if (savedProjects.contains(project)) {
                savedProjects.remove(project);
                freelancerRepository.save(freelancer);
                return false; 
            } else {
                savedProjects.add(project);
                freelancerRepository.save(freelancer);
                return true; 
            }
        } catch (Exception e) {
            throw new RuntimeException("Error toggling saved project: " + e.getMessage());
        }
    }

    public boolean isProjectSaved(String freelancerEmail, UUID projectId) {
        Freelancer freelancer = freelancerRepository.findByEmail(freelancerEmail)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        return freelancer.getSavedProjects().stream()
                .anyMatch(project -> project.getId().equals(projectId));
    }


}