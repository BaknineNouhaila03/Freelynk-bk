package org.example.freelynk.controller;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.Review;
import org.example.freelynk.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/freelancers/{freelancerId}/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping
    public ResponseEntity<Review> createReview(
            @PathVariable UUID freelancerId,
            @RequestBody Review review
    ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Client client = (Client) authentication.getPrincipal();


        Review createdReview = reviewService.createReview(freelancerId, review, client);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdReview);
    }

    @GetMapping
    public ResponseEntity<List<Review>> getReviews(@PathVariable UUID freelancerId) {
        List<Review> reviews = reviewService.getReviews(freelancerId);
        return ResponseEntity.ok(reviews);
    }
}
