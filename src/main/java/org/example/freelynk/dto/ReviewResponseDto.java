package org.example.freelynk.dto;


import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ReviewResponseDto {
    
    private UUID id;
    private Integer rating;
    private String comment;
    private LocalDateTime createdAt;
    
    private UUID clientId;
    private String clientName;
    
    private UUID freelancerId;
    private String freelancerName;
}