package org.example.freelynk.controller;

import org.example.freelynk.dto.ReviewRequestDto;
import org.example.freelynk.dto.ReviewResponseDto;
import org.example.freelynk.dto.ReviewStatsDto;
import org.example.freelynk.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "*")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;


    @GetMapping("/freelancer/{freelancerId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsForFreelancer(
            @PathVariable UUID freelancerId) {
        try {
            List<ReviewResponseDto> reviews = reviewService.getReviewsForFreelancer(freelancerId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
 * Get review statistics for a freelancer
 * GET /api/reviews/freelancer/{freelancerId}/stats
 */
@GetMapping("/freelancer/{freelancerId}/stats")
public ResponseEntity<ReviewStatsDto> getReviewStatsForFreelancer(
        @PathVariable UUID freelancerId) {
    try {
        ReviewStatsDto stats = reviewService.getReviewStatsForFreelancer(freelancerId);
        return ResponseEntity.ok(stats);
    } catch (Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}


    @PostMapping("/freelancer/{freelancerId}")
    public ResponseEntity<ReviewResponseDto> addReviewForFreelancer(
            @PathVariable UUID freelancerId,
             @RequestBody ReviewRequestDto reviewRequest,
            Authentication authentication) {
        try {
            String clientEmail = authentication.getName();
            
            ReviewResponseDto review = reviewService.addReviewForFreelancer(
                freelancerId, clientEmail, reviewRequest);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(review);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/client/my-reviews")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(Authentication authentication) {
        try {
            String clientEmail = authentication.getName();
            List<ReviewResponseDto> reviews = reviewService.getReviewsByClient(clientEmail);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}