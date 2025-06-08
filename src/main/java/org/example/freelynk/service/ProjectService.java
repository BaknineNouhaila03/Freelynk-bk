package org.example.freelynk.service;

import org.example.freelynk.dto.AddProjectRequest;
import org.example.freelynk.model.Client;
import org.example.freelynk.model.Project;
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
        project.setRequiredSkills(request.getRequiredSkills());
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




}