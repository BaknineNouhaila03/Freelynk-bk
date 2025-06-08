package org.example.freelynk.service;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Review;
import org.example.freelynk.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final FreelancerService freelancerService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, FreelancerService freelancerService) {
        this.reviewRepository = reviewRepository;
        this.freelancerService = freelancerService;
    }


    @Transactional
    public Review createReview(UUID freelancerId, Review review, Client client) {
        Freelancer freelancer = freelancerService.getFreelancerById(freelancerId);
        if (freelancer == null) {
            throw new IllegalArgumentException("Freelancer not found with ID: " + freelancerId);
        }
        review.setFreelancer(freelancer);
        review.setClient(client);
        return reviewRepository.save(review);
    }


    public List<Review> getReviews(UUID freelancerId) {
        return reviewRepository.findByFreelancerId(freelancerId);
    }
}
