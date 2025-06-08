package org.example.freelynk.controller;

import org.example.freelynk.dto.AddBidRequest;
import org.example.freelynk.dto.BidResponseDTO;
import org.example.freelynk.model.Bid;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.security.SecurityUtil;
import org.example.freelynk.service.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bids")

public class BidController {

    private final BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping
    public ResponseEntity<?> addBid(@RequestBody AddBidRequest request) {
        try {
            // Validate that freelancer email is provided
            if (request.getFreelancerEmail() == null || request.getFreelancerEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(new ErrorResponse("Freelancer email is required"));
            }

            BidResponseDTO bid = bidService.addBid(request);
            return ResponseEntity.ok(bid);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Failed to submit bid: " + e.getMessage()));
        }
    }

    // Error response class
    public static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }

        // Getter for JSON serialization
        public String getMessage() {
            return message;
        }
    }
    @GetMapping("/projects/{projectId}")
    public ResponseEntity<List<Bid>> getBidsForProject(@PathVariable UUID projectId) {
        return ResponseEntity.ok(bidService.getBidsForProject(projectId));
    }


}
