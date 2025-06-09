package org.example.freelynk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    @Id
    @GeneratedValue
    @Column(name = "project_id", columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "name")
    private String name;

    @Column
    private String description;

    // Option 1: Use array mapping with proper column definition
    @Column(name = "required_skills", columnDefinition = "text[]")
    private String[] requiredSkills;

    @Column(name = "min_budget")
    private Double minBudget;

    @Column(name = "max_budget")
    private Double maxBudget;

    @Enumerated(EnumType.STRING)
    private ProjectStatus status;


    @Column(name = "binding_deadline")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime bindingDeadline;

    @ManyToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Bid> bids;

    @JsonProperty("requiredSkills")
    public List<String> getRequiredSkillsList() {
        if (requiredSkills == null || requiredSkills.length == 0) {
            return List.of();
        }

        return Arrays.stream(requiredSkills)
                .map(skill -> skill.replaceAll("[{}\"\\s]", "")) // Remove {, }, quotes, and whitespace
                .filter(skill -> !skill.isEmpty())
                .collect(Collectors.toList());
    }

    public void setRequiredSkillsList(List<String> skills) {
        this.requiredSkills = skills != null ? skills.toArray(new String[0]) : null;
    }

    @Column(name = "bid_number")
    private Integer bidNumber = 0;

}