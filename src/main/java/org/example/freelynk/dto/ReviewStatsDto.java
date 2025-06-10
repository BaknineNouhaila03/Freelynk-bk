package org.example.freelynk.dto;

import lombok.Data;

@Data
public class ReviewStatsDto {
    private int totalReviews;
    private int[] starBreakdown; // [5-star, 4-star, 3-star, 2-star, 1-star]
    private double averageRating;
}