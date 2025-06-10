package org.example.freelynk.service;

import org.example.freelynk.dto.AddProjectRequest;
import org.example.freelynk.model.*;
import org.example.freelynk.repository.FreelancerRepository;
import org.example.freelynk.repository.ProjectRepository;
import org.example.freelynk.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final FreelancerRepository freelancerRepository; // Add this


    public ProjectService(ProjectRepository projectRepository, FreelancerRepository freelancerRepository) {
        this.projectRepository = projectRepository;
        this.freelancerRepository = freelancerRepository; // Add this
    }

    public Project addProject(AddProjectRequest request) {
        // Get the current client from security context
        Client client = SecurityUtil.getCurrentClient();
        
        // Validate that the current user is indeed a client
        if (client == null) {
            throw new IllegalStateException("Only clients can create projects");
        }

        Project project = new Project();
        project.setClient(client);
        project.setName(request.getName());
        project.setDescription(request.getDescription());
        project.setRequiredSkillsList(request.getRequiredSkills());
        project.setMinBudget(request.getMinBudget());
        project.setMaxBudget(request.getMaxBudget());
        project.setBindingDeadline(request.getBindingDeadline());
        
        // Save and return the project
        return projectRepository.save(project);
    }

    public Project getProjectById(UUID projectId) {
        return projectRepository.findById(projectId)
                .orElse(null);
    }
    public List<Project> getProjectsByClient(Client client) {
    return projectRepository.findByClient(client);
}

public List<Project> getAllProjects(){
    return projectRepository.findAll();

}
// In ProjectService.java
public List<Project> getProjectsByFreelancerId(UUID freelancerId) {
    return projectRepository.findByFreelancerId(freelancerId);
}

    public Project markProjectAsDone(UUID projectId, UUID freelancerId) {
        // Find the project
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found with ID: " + projectId));

        // Verify that the project is assigned to the given freelancer
        if (project.getFreelancer() == null || !project.getFreelancer().getId().equals(freelancerId)) {
            throw new IllegalStateException("Project is not assigned to this freelancer");
        }

        // Verify that the project is currently ongoing
        if (project.getStatus() != ProjectStatus.ONGOING) {
            throw new IllegalStateException("Only ongoing projects can be marked as done");
        }

        // Update the project status to DONE
        project.setStatus(ProjectStatus.DONE);

        // Save and return the updated project
        return projectRepository.save(project);
    }



    public List<Project> getRecommendedProjectsForFreelancer(String email) {
        // Get freelancer by email
        Freelancer freelancer = freelancerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        // Get all available projects (not assigned to a freelancer yet and with NOT_STARTED status)
        List<Project> availableProjects = projectRepository.findByFreelancerIsNullAndStatus(ProjectStatus.NOT_STARTED);

        // Get freelancer's skills
        List<String> freelancerSkills = freelancer.getSkills();
        if (freelancerSkills == null || freelancerSkills.isEmpty()) {
            return availableProjects; // Return all projects if freelancer has no skills
        }

        // Convert freelancer skills to lowercase for case-insensitive comparison
        List<String> normalizedFreelancerSkills = freelancerSkills.stream()
                .map(skill -> skill.toLowerCase().trim())
                .collect(Collectors.toList());

        // Create a list to hold projects with their match scores
        List<ProjectWithScore> projectsWithScores = new ArrayList<>();

        for (Project project : availableProjects) {
            List<String> projectSkills = project.getRequiredSkillsList();

            if (projectSkills == null || projectSkills.isEmpty()) {
                // Projects with no required skills get score 0
                projectsWithScores.add(new ProjectWithScore(project, 0));
                continue;
            }

            // Normalize project skills for comparison
            List<String> normalizedProjectSkills = projectSkills.stream()
                    .map(skill -> skill.toLowerCase().trim())
                    .collect(Collectors.toList());

            // Calculate matching skills
            long matchingSkillsCount = normalizedProjectSkills.stream()
                    .filter(normalizedFreelancerSkills::contains)
                    .count();

            // Calculate match percentage
            double matchScore = (double) matchingSkillsCount / normalizedProjectSkills.size();

            projectsWithScores.add(new ProjectWithScore(project, matchScore));
        }

        // Sort by match score in descending order (highest match first)
        projectsWithScores.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));

        // Extract projects from the sorted list
        return projectsWithScores.stream()
                .map(ProjectWithScore::getProject)
                .collect(Collectors.toList());
    }

    // Inner class to hold project with its matching score
    private static class ProjectWithScore {
        private final Project project;
        private final double score;

        public ProjectWithScore(Project project, double score) {
            this.project = project;
            this.score = score;
        }

        public Project getProject() {
            return project;
        }

        public double getScore() {
            return score;
        }
    }

}