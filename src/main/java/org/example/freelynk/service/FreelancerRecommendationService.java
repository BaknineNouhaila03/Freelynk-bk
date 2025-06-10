package org.example.freelynk.service;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Project;
import org.example.freelynk.repository.ClientRepository;
import org.example.freelynk.repository.FreelancerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreelancerRecommendationService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

 public List<FreelancerRecommendationDTO> getRecommendedFreelancers(UUID clientId, int limit) {
    // Get client with their projects
    Optional<Client> clientOptional = clientRepository.findById(clientId);
    if (clientOptional.isEmpty()) {
        System.out.println("Client not found: " + clientId);
        return Collections.emptyList();
    }

    Client client = clientOptional.get();
    Set<Project> clientProjects = client.getProjects();
    
    System.out.println("Client ID: " + clientId);
    System.out.println("Number of projects: " + clientProjects.size());

    if (clientProjects.isEmpty()) {
        System.out.println("No projects found - returning top rated freelancers");
        return getTopRatedFreelancers(limit);
    }

    // Extract all required skills from client's projects
    Set<String> allRequiredSkills = extractRequiredSkills(clientProjects);
    System.out.println("Required skills: " + allRequiredSkills);

    if (allRequiredSkills.isEmpty()) {
        System.out.println("No required skills found - returning top rated freelancers");
        return getTopRatedFreelancers(limit);
    }

    // Get all freelancers
    List<Freelancer> allFreelancers = freelancerRepository.findAll();
    System.out.println("Total freelancers: " + allFreelancers.size());

    // Calculate recommendation scores
    List<FreelancerRecommendationDTO> recommendations = allFreelancers.stream()
            .map(freelancer -> calculateRecommendationScore(freelancer, allRequiredSkills, clientProjects))
            .filter(rec -> rec.getScore() > 0)
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .limit(limit)
            .collect(Collectors.toList());

    System.out.println("Recommendations returned: " + recommendations.size());
    return recommendations;
}
    private Set<String> extractRequiredSkills(Set<Project> projects) {
        return projects.stream()
                .flatMap(project -> project.getRequiredSkillsList().stream())
                .map(skill -> skill.toLowerCase().trim()) // Normalize skills
                .collect(Collectors.toSet());
    }

private FreelancerRecommendationDTO calculateRecommendationScore(
        Freelancer freelancer, 
        Set<String> requiredSkills, 
        Set<Project> clientProjects) {
    
    List<String> freelancerSkills = freelancer.getSkills();
    System.out.println("Freelancer: " + freelancer.getFirstName() + " " + freelancer.getLastName());
    System.out.println("Freelancer skills: " + freelancerSkills);
    System.out.println("Required skills: " + requiredSkills);
    
    if (freelancerSkills == null || freelancerSkills.isEmpty()) {
        System.out.println("No skills listed for freelancer");
        return new FreelancerRecommendationDTO(freelancer, 0.0, Collections.emptyList(), "No skills listed");
    }

    // Normalize freelancer skills
    Set<String> normalizedFreelancerSkills = freelancerSkills.stream()
            .map(skill -> skill.toLowerCase().trim())
            .collect(Collectors.toSet());
    
    System.out.println("Normalized freelancer skills: " + normalizedFreelancerSkills);

    // Calculate skill match percentage
    Set<String> matchingSkills = requiredSkills.stream()
            .filter(normalizedFreelancerSkills::contains)
            .collect(Collectors.toSet());

    System.out.println("Matching skills: " + matchingSkills);

    if (matchingSkills.isEmpty()) {
        System.out.println("No matching skills - returning score 0.0");
        return new FreelancerRecommendationDTO(freelancer, 0.0, Collections.emptyList(), "No matching skills");
    }

        // Base score: percentage of required skills that freelancer has
        double skillMatchPercentage = (double) matchingSkills.size() / requiredSkills.size();
        
        // Skill frequency weight: skills that appear in multiple projects get higher weight
        double frequencyWeight = calculateSkillFrequencyWeight(matchingSkills, clientProjects);
        
        // Rating boost: freelancers with higher ratings get a small boost
        double ratingBoost = freelancer.getRating() != null ? freelancer.getRating() / 10.0 : 0.0; // Max 0.5 boost
        
        // Experience boost: more experienced freelancers get a small boost
        double experienceBoost = freelancer.getYearsOfExp() != null ? 
                Math.min(freelancer.getYearsOfExp() / 20.0, 0.3) : 0.0; // Max 0.3 boost

        double finalScore = (skillMatchPercentage * 0.7) + (frequencyWeight * 0.2) + (ratingBoost * 0.05) + (experienceBoost * 0.05);

        String reason = generateRecommendationReason(matchingSkills, requiredSkills, freelancer);

        return new FreelancerRecommendationDTO(freelancer, finalScore, new ArrayList<>(matchingSkills), reason);
    }

    private double calculateSkillFrequencyWeight(Set<String> matchingSkills, Set<Project> projects) {
        Map<String, Integer> skillFrequency = new HashMap<>();
        
        for (Project project : projects) {
            for (String skill : project.getRequiredSkillsList()) {
                String normalizedSkill = skill.toLowerCase().trim();
                if (matchingSkills.contains(normalizedSkill)) {
                    skillFrequency.put(normalizedSkill, skillFrequency.getOrDefault(normalizedSkill, 0) + 1);
                }
            }
        }

        // Calculate weighted score based on skill frequency
        return matchingSkills.stream()
                .mapToDouble(skill -> skillFrequency.getOrDefault(skill, 1) / (double) projects.size())
                .average()
                .orElse(0.0);
    }

    private String generateRecommendationReason(Set<String> matchingSkills, Set<String> requiredSkills, Freelancer freelancer) {
        StringBuilder reason = new StringBuilder();
        
        if (matchingSkills.size() == requiredSkills.size()) {
            reason.append("Perfect match! Has all required skills: ");
        } else {
            reason.append("Good match! Has ").append(matchingSkills.size())
                  .append(" out of ").append(requiredSkills.size())
                  .append(" required skills: ");
        }
        
        reason.append(String.join(", ", matchingSkills));
        
        if (freelancer.getRating() != null && freelancer.getRating() >= 4.0) {
            reason.append(" • Highly rated (").append(freelancer.getRating()).append("/5)");
        }
        
        if (freelancer.getYearsOfExp() != null && freelancer.getYearsOfExp() >= 3) {
            reason.append(" • ").append(freelancer.getYearsOfExp()).append(" years experience");
        }

        return reason.toString();
    }

    private List<FreelancerRecommendationDTO> getTopRatedFreelancers(int limit) {
        List<Freelancer> topRated = freelancerRepository.findAll().stream()
                .filter(f -> f.getRating() != null)
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating()))
                .limit(limit)
                .collect(Collectors.toList());

        return topRated.stream()
                .map(f -> new FreelancerRecommendationDTO(f, f.getRating() / 5.0, Collections.emptyList(), "Top rated freelancer"))
                .collect(Collectors.toList());
    }

    // Inner DTO class for recommendations
    public static class FreelancerRecommendationDTO {
        private Freelancer freelancer;
        private Double score;
        private List<String> matchingSkills;
        private String reason;

        public FreelancerRecommendationDTO(Freelancer freelancer, Double score, List<String> matchingSkills, String reason) {
            this.freelancer = freelancer;
            this.score = score;
            this.matchingSkills = matchingSkills;
            this.reason = reason;
        }

        // Getters and setters
        public Freelancer getFreelancer() { return freelancer; }
        public void setFreelancer(Freelancer freelancer) { this.freelancer = freelancer; }
        
        public Double getScore() { return score; }
        public void setScore(Double score) { this.score = score; }
        
        public List<String> getMatchingSkills() { return matchingSkills; }
        public void setMatchingSkills(List<String> matchingSkills) { this.matchingSkills = matchingSkills; }
        
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
}