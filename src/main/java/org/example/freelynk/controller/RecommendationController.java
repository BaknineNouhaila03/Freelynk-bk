package org.example.freelynk.controller;

import org.example.freelynk.service.FreelancerRecommendationService;
import org.example.freelynk.service.FreelancerRecommendationService.FreelancerRecommendationDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/recommendations")
@CrossOrigin(origins = "http://localhost:3000")
public class RecommendationController {

    @Autowired
    private FreelancerRecommendationService recommendationService;

    @GetMapping("/freelancers")
    public ResponseEntity<List<FreelancerRecommendationDTO>> getRecommendedFreelancers(
            @RequestParam UUID clientId,
            @RequestParam(defaultValue = "6") int limit) {
        
        try {
            List<FreelancerRecommendationDTO> recommendations = 
                recommendationService.getRecommendedFreelancers(clientId, limit);
            
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}