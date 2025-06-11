package org.example.freelynk.controller;

import java.util.List;
import java.util.UUID;

import org.example.freelynk.dto.AddGigRequest;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Gig;
import org.example.freelynk.security.SecurityUtil;
import org.example.freelynk.service.FreelancerService;
import org.example.freelynk.service.GigService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/gigs")
public class GigController {

    private final GigService gigService;
    private final FreelancerService freelancerService;

    public GigController(GigService gigService, FreelancerService freelancerService) {
        this.gigService = gigService;
        this.freelancerService = freelancerService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGig(
            @RequestParam("title") String title,
            @RequestParam("description") String description,
            @RequestParam("gigUrls") List<MultipartFile> files) {

        try {
            Freelancer freelancer = (Freelancer) SecurityUtil.getCurrentUser();
            AddGigRequest request = new AddGigRequest();
            request.setTitle(title);
            request.setDescription(description);

            Gig savedGig = gigService.addGig(request, files, freelancer);
            return ResponseEntity.ok(savedGig);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding gig: " + e.getMessage());
        }
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getGigById(@PathVariable UUID id) {
    Gig gig = gigService.getGigById(id);
    return ResponseEntity.ok(gig);
    }
@GetMapping("/freelancers/{freelancerId}")
public ResponseEntity<?> getGigsByFreelancerId(@PathVariable UUID freelancerId) {
    Freelancer freelancer = freelancerService.getFreelancerById(freelancerId);
    List<Gig> gigs = gigService.getGigsForFreelancer(freelancer);
    return ResponseEntity.ok(gigs);
}


}
