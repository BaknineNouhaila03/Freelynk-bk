package org.example.freelynk.dto;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddProjectRequest {
    private String name;
    private String description;
    private List<String> requiredSkills;
    private Double minBudget;
    private Double maxBudget;
    private LocalDateTime bindingDeadline;
}
