package org.example.freelynk.model;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
public class Project {
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getMinBudget() {
        return minBudget;
    }

    public void setMinBudget(Double minBudget) {
        this.minBudget = minBudget;
    }

    public Double getMaxBudget() {
        return maxBudget;
    }

    public void setMaxBudget(Double maxBudget) {
        this.maxBudget = maxBudget;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public LocalDateTime getBindingDeadline() {
        return bindingDeadline;
    }

    public void setBindingDeadline(LocalDateTime bindingDeadline) {
        this.bindingDeadline = bindingDeadline;
    }

    public Freelancer getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }

    public Integer getBidNumber() {
        return bidNumber;
    }

    public void setBidNumber(Integer bidNumber) {
        this.bidNumber = bidNumber;
    }

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
@JsonBackReference("freelancer-projects")
    private Freelancer freelancer;
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL)
    private List<Bid> bids;

    @JsonProperty("requiredSkills")
    public List<String> getRequiredSkillsList() {
        if (requiredSkills == null || requiredSkills.length == 0) {
            return List.of();
        }

        return Arrays.stream(requiredSkills)
                .map(skill -> skill.replaceAll("[{}\"\\s]", "")) 
                .filter(skill -> !skill.isEmpty())
                .collect(Collectors.toList());
    }

    public void setRequiredSkillsList(List<String> skills) {
        this.requiredSkills = skills != null ? skills.toArray(new String[0]) : null;
    }

    @Column(name = "bid_number")
    private Integer bidNumber = 0;

}