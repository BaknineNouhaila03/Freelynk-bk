package org.example.freelynk.service;

import org.example.freelynk.dto.AddProjectRequest;
import org.example.freelynk.model.Client;
import org.example.freelynk.model.Project;
import org.example.freelynk.model.ProjectStatus;
import org.example.freelynk.model.User;
import org.example.freelynk.repository.ProjectRepository;
import org.example.freelynk.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProjectService {

    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
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



}