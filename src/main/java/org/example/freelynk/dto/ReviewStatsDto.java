package org.example.freelynk.dto;

import lombok.Data;

@Data
public class ReviewStatsDto {
    private int totalReviews;
    private int[] starBreakdown;
    private double averageRating;
}