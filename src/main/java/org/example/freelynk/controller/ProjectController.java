package org.example.freelynk.controller;

import java.util.List;
import java.util.UUID;

import org.example.freelynk.dto.AddProjectRequest;
import org.example.freelynk.model.Client;
import org.example.freelynk.model.Project;
import org.example.freelynk.model.User;
import org.example.freelynk.security.SecurityUtil;
import org.example.freelynk.service.NotificationService;
import org.example.freelynk.service.ProjectService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/projects")
public class ProjectController {

    private final ProjectService projectService;
    private final NotificationService notificationService;

    public ProjectController(ProjectService projectService,NotificationService notificationService) {
        this.projectService = projectService;
        this.notificationService = notificationService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addProject(@RequestBody AddProjectRequest request) {
        // User currentClient =  SecurityUtil.getCurrentUser(); 
        Project project = projectService.addProject(request);
        return ResponseEntity.ok(project);
    }
    @GetMapping("/myProjects")
    public ResponseEntity<?> getMyProjects() {
    User currentClient = SecurityUtil.getCurrentUser();
    List<Project> projects = projectService.getProjectsByClient((Client) currentClient);
    return ResponseEntity.ok(projects);
}

    @GetMapping
    public ResponseEntity<?> getAllProjects() {
        List<Project> projects = projectService.getAllProjects();
        return ResponseEntity.ok(projects);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getProjectByID(@PathVariable  UUID  id ) {
        Project project = projectService.getProjectById(id);
        return ResponseEntity.ok(project);
    }
    @GetMapping("/byFreelancer/{freelancerId}")
public ResponseEntity<?> getProjectsByFreelancerId(@PathVariable UUID freelancerId) {
    List<Project> projects = projectService.getProjectsByFreelancerId(freelancerId);
    return ResponseEntity.ok(projects);
}

@PutMapping("/byFreelancer/{freelancerId}/project/{projectId}")
public ResponseEntity<?> markProjectAsDone(
    @PathVariable UUID freelancerId, 
    @PathVariable UUID projectId) {
    
    try {
        Project updatedProject = projectService.markProjectAsDone(projectId, freelancerId);
        
        // Get the client ID from the project and send notification
        UUID clientId = updatedProject.getClient().getId(); // Assuming Project has getClientId()
        notificationService.createNotification(
            clientId, 
            "PROJECT_COMPLETED", 
            "Your project '" + updatedProject.getName() + "' has been marked as completed by the freelancer"
        );
        
        return ResponseEntity.ok(updatedProject);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    } catch (IllegalStateException e) {
        return ResponseEntity.status(403).body(e.getMessage());
    } catch (Exception e) {
        return ResponseEntity.status(500).body("An error occurred while updating the project");
    }
}
    
}
