package org.example.freelynk.controller;

import org.example.freelynk.dto.SavedFreelancerDTO;
import org.example.freelynk.model.SavedFreelancer;
import org.example.freelynk.service.SavedFreelancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/savedFreelancers")
@CrossOrigin(origins = "*")
public class SavedFreelancerController {
    
    @Autowired
    private SavedFreelancerService savedFreelancerService;
    
    @GetMapping("/client/{clientId}")
    public ResponseEntity<List<SavedFreelancerDTO>> getSavedFreelancers(
            @PathVariable UUID clientId) {
        try {
            List<SavedFreelancerDTO> savedFreelancers = 
                savedFreelancerService.getSavedFreelancersByClientId(clientId);
            return ResponseEntity.ok(savedFreelancers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<List<UUID>> getBookmarkedFreelancerIds(
            @RequestParam UUID clientId) {
        try {
            List<UUID> bookmarkedIds = savedFreelancerService.getBookmarkedFreelancerIds(clientId);
            return ResponseEntity.ok(bookmarkedIds);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<String> saveFreelancer(
            @RequestParam UUID clientId,
            @RequestParam UUID freelancerId) {
        try {
            savedFreelancerService.saveFreelancer(clientId, freelancerId);
            return ResponseEntity.ok("Freelancer saved successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error saving freelancer");
        }
    }
    
    @DeleteMapping("/remove")
    public ResponseEntity<String> removeSavedFreelancer(
            @RequestParam UUID clientId,
            @RequestParam UUID freelancerId) {
        try {
            savedFreelancerService.removeSavedFreelancer(clientId, freelancerId);
            return ResponseEntity.ok("Freelancer removed from saved list");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error removing freelancer");
        }
    }
    
    @GetMapping("/check-single")
    public ResponseEntity<Boolean> isFreelancerSaved(
            @RequestParam UUID clientId,
            @RequestParam UUID freelancerId) {
        try {
            boolean isSaved = savedFreelancerService.isFreelancerSaved(clientId, freelancerId);
            return ResponseEntity.ok(isSaved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}