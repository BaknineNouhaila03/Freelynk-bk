package org.example.freelynk.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "clients")
public class Client extends User {

 @ManyToMany
@JoinTable(
    name = "saved_freelancers",
    joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "freelancer_id", referencedColumnName = "user_id")
)
private Set<Freelancer> savedFreelancers = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

    public Client() {
        super();
        this.setRole(Role.CLIENT);
    }

    public Set<Freelancer> getSavedFreelancers() {
        return savedFreelancers;
    }

    public void setSavedFreelancers(Set<Freelancer> savedFreelancers) {
        this.savedFreelancers = savedFreelancers;
    }

    public Set<Project> getProjects() {
        return projects;
    }

    public void setProjects(Set<Project> projects) {
        this.projects = projects;
    }
}
