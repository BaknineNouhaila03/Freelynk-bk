package org.example.freelynk.controller;

import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Project;
import org.example.freelynk.service.FreelancerService;
import org.example.freelynk.service.SavedFreelancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/freelancers")
public class FreelancerController {

    private final FreelancerService freelancerService;
    private final SavedFreelancerService savedFreelancerService;

    @Autowired
    public FreelancerController(FreelancerService freelancerService,SavedFreelancerService savedFreelancerService) {
        this.freelancerService = freelancerService;
        this.savedFreelancerService=savedFreelancerService;
    }



    @GetMapping
    public ResponseEntity<List<Freelancer>> getFreelancers() {
        List<Freelancer> freelancers = freelancerService.getFreelancers();
        return new ResponseEntity<>(freelancers, HttpStatus.OK);
    }

    @GetMapping("/{freelancerId}")
    public ResponseEntity<Freelancer> getFreelancerById(@PathVariable UUID freelancerId) {
        Freelancer freelancer = freelancerService.getFreelancerById(freelancerId);
        if (freelancer != null) {
            return new ResponseEntity<>(freelancer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/email/{freelancerEmail}")
    public ResponseEntity<Freelancer> getFreelancerByEmail(@PathVariable String freelancerEmail) {
        Freelancer freelancer = freelancerService.getFreelancerByEmail(freelancerEmail);
        if (freelancer != null) {
            return new ResponseEntity<>(freelancer, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

@GetMapping("/category/{category}")
public ResponseEntity<List<Freelancer>> getFreelancersByCategory(@PathVariable String category) {
    // Map URL slugs to actual skill names
    String skillName = mapCategoryToSkill(category);
    List<Freelancer> freelancers = freelancerService.getFreelancersBySkills(Collections.singletonList(skillName));
    return new ResponseEntity<>(freelancers, HttpStatus.OK);
}

private String mapCategoryToSkill(String category) {
    Map<String, String> categoryMap = new HashMap<>();
    categoryMap.put("web-development", "Web Development");
    categoryMap.put("graphic-design", "Graphic Design");
    categoryMap.put("writing-translation", "Writing");
    categoryMap.put("digital-marketing", "Digital Marketing");
    categoryMap.put("video-animation", "Video Editing");
    categoryMap.put("business-assistance", "Business");
    
    return categoryMap.getOrDefault(category, category);
}


    public static class FreelancerPublicDTO {
        public String id;
        public String firstName;
        public String lastName;
        public String email;
        public Double rating;
        public String occupation;

        public FreelancerPublicDTO(Freelancer freelancer) {
            this.id = freelancer.getId().toString();
            this.firstName = freelancer.getFirstName();
            this.lastName = freelancer.getLastName();
            this.email = freelancer.getEmail();
            this.rating = freelancer.getRating();
            this.occupation = freelancer.getOccupation();
        }
    }


@GetMapping("/check")
public ResponseEntity<List<UUID>> getBookmarkedFreelancers(@RequestParam UUID clientId) {
    try {
        List<UUID> bookmarkedIds = savedFreelancerService.getBookmarkedFreelancerIds(clientId);
        return ResponseEntity.ok(bookmarkedIds);
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(null);
    }
}
@GetMapping("/occupation/{occupation}")
public ResponseEntity<List<Freelancer>> getFreelancersByOccupation(@PathVariable String occupation) {
    try {
        // Decode URL-encoded occupation (replace dashes with spaces, etc.)
        String decodedOccupation = mapOccupationFromUrl(occupation);
        List<Freelancer> freelancers = freelancerService.getFreelancersByOccupation(decodedOccupation);
        
        if (freelancers.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<>(freelancers, HttpStatus.OK);
    } catch (Exception e) {
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

private String mapOccupationFromUrl(String occupation) {
    Map<String, String> occupationMap = new HashMap<>();
    occupationMap.put("web-development", "Web and App Developmen");
    occupationMap.put("graphic-design", "Graphic & UI/UX design");
    occupationMap.put("writing-translation", "Writing and translation");
    occupationMap.put("digital-marketing", "Digital Marketing");
    occupationMap.put("video-animation", "Video & Animation Services");
    occupationMap.put("business-assistance", "Business & Virtual Assistance");
    
    if (occupationMap.containsKey(occupation)) {
        return occupationMap.get(occupation);
    }
    
    return Arrays.stream(occupation.replace("-", " ").split(" "))
           .map(word -> word.substring(0, 1).toUpperCase() + word.substring(1).toLowerCase())
           .collect(Collectors.joining(" "));
}
}