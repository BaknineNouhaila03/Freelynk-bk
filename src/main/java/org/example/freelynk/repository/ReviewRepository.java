package org.example.freelynk.repository;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReviewRepository extends JpaRepository<Review, UUID> {

    /**
     * Find all reviews for a specific freelancer, ordered by creation date (newest first)
     */
    List<Review> findByFreelancerOrderByCreatedAtDesc(Freelancer freelancer);

    /**
     * Find all reviews given by a specific client, ordered by creation date (newest first)
     */
    List<Review> findByClientOrderByCreatedAtDesc(Client client);

    /**
     * Check if a client has already reviewed a specific freelancer
     */
    boolean existsByClientAndFreelancer(Client client, Freelancer freelancer);

    /**
     * Count total number of reviews for a freelancer
     */
    Long countByFreelancer(Freelancer freelancer);

    /**
     * Get average rating for a freelancer
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.freelancer = :freelancer")
    Double findAverageRatingByFreelancer(@Param("freelancer") Freelancer freelancer);

    /**
     * Find reviews by freelancer with rating greater than or equal to specified value
     */
    List<Review> findByFreelancerAndRatingGreaterThanEqualOrderByCreatedAtDesc(
            Freelancer freelancer, Integer minRating);

    /**
     * Find recent reviews for a freelancer (limit can be applied in service layer)
     */
    List<Review> findTop10ByFreelancerOrderByCreatedAtDesc(Freelancer freelancer);
}