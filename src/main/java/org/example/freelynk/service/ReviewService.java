package org.example.freelynk.service;

import org.example.freelynk.dto.ReviewRequestDto;
import org.example.freelynk.dto.ReviewResponseDto;
import org.example.freelynk.model.Client;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Review;
import org.example.freelynk.repository.ClientRepository;
import org.example.freelynk.repository.FreelancerRepository;
import org.example.freelynk.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get all reviews for a specific freelancer
     */
    public List<ReviewResponseDto> getReviewsForFreelancer(UUID freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
            .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        List<Review> reviews = reviewRepository.findByFreelancerOrderByCreatedAtDesc(freelancer);
        
        return reviews.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    /**
     * Add a review for a freelancer by a client
     */
    public ReviewResponseDto addReviewForFreelancer(UUID freelancerId, String clientEmail, 
                                                   ReviewRequestDto reviewRequest) {
        // Find freelancer
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
            .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        // Find client by email
        Client client = clientRepository.findByEmail(clientEmail)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        // Validate rating
        if (reviewRequest.getRating() < 1 || reviewRequest.getRating() > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }

        // Check if client has already reviewed this freelancer
        boolean alreadyReviewed = reviewRepository.existsByClientAndFreelancer(client, freelancer);
        if (alreadyReviewed) {
            throw new IllegalArgumentException("You have already reviewed this freelancer");
        }

        // Create new review
        Review review = new Review();
        review.setClient(client);
        review.setFreelancer(freelancer);
        review.setRating(reviewRequest.getRating());
        review.setComment(reviewRequest.getComment());

        Review savedReview = reviewRepository.save(review);
        
        return convertToResponseDto(savedReview);
    }

    /**
     * Get all reviews given by a specific client
     */
    public List<ReviewResponseDto> getReviewsByClient(String clientEmail) {
        Client client = clientRepository.findByEmail(clientEmail)
            .orElseThrow(() -> new RuntimeException("Client not found"));

        List<Review> reviews = reviewRepository.findByClientOrderByCreatedAtDesc(client);
        
        return reviews.stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }

    /**
     * Get average rating for a freelancer
     */
    public Double getAverageRatingForFreelancer(UUID freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
            .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        return reviewRepository.findAverageRatingByFreelancer(freelancer);
    }

    /**
     * Get total number of reviews for a freelancer
     */
    public Long getReviewCountForFreelancer(UUID freelancerId) {
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
            .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        return reviewRepository.countByFreelancer(freelancer);
    }

    /**
     * Convert Review entity to ReviewResponseDto
     */
    private ReviewResponseDto convertToResponseDto(Review review) {
        ReviewResponseDto dto = new ReviewResponseDto();
        dto.setId(review.getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        
        // Client information
        dto.setClientName(review.getClient().getFirstName() + " " + review.getClient().getLastName());
        dto.setClientId(review.getClient().getId());
        
        // Freelancer information
        dto.setFreelancerName(review.getFreelancer().getFirstName() + " " + review.getFreelancer().getLastName());
        dto.setFreelancerId(review.getFreelancer().getId());
        
        return dto;
    }
}