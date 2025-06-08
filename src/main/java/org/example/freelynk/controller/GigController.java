package org.example.freelynk.controller;

import java.util.List;
import java.util.UUID;

import org.example.freelynk.dto.AddGigRequest;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Gig;
import org.example.freelynk.security.SecurityUtil;
import org.example.freelynk.service.GigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gigs")
public class GigController {

    private final GigService gigService;

    public GigController(GigService gigService) {
        this.gigService = gigService;
    }

    @PostMapping("/add")
    public ResponseEntity<?> addGig(@RequestBody AddGigRequest request) {
        Freelancer freelancer = (Freelancer) SecurityUtil.getCurrentUser();
        Gig savedGig = gigService.addGig(request, freelancer);
        return ResponseEntity.ok(savedGig);
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getGigById(@PathVariable UUID id) {
    Gig gig = gigService.getGigById(id);
    return ResponseEntity.ok(gig);
}
@GetMapping("/myGigs")
public ResponseEntity<?> getMyGigs() {
    Freelancer freelancer = (Freelancer) SecurityUtil.getCurrentUser();
    List<Gig> gigs = gigService.getGigsForFreelancer(freelancer);
    return ResponseEntity.ok(gigs);
}


}
