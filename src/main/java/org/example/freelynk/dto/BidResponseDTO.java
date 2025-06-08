package org.example.freelynk.dto;

import lombok.Data;
import org.example.freelynk.model.BidStatus;
import java.util.UUID;

@Data
public class BidResponseDTO {
    private UUID id;
    private UUID freelancerId;
    private String freelancerEmail;
    private Double bidAmount;
    private String motivation;
    private Integer deliveryDays;
    private BidStatus status;

    // Constructor to create DTO from Bid entity
    public BidResponseDTO(org.example.freelynk.model.Bid bid) {
        this.id = bid.getId();
        this.freelancerId = bid.getFreelancer().getId();
        this.freelancerEmail = bid.getFreelancer().getEmail();
        this.bidAmount = bid.getBidAmount();
        this.motivation = bid.getMotivation();
        this.deliveryDays = bid.getDeliveryDays();
        this.status = bid.getStatus();
    }
}