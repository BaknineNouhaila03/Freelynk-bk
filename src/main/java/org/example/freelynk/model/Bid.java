package org.example.freelynk.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "bids")
public class Bid {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "bid_id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", nullable = false)
    @JsonIgnore
    private Freelancer freelancer;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    @JsonIgnore
    private Project project;

    @Column(name = "bid_amount", nullable = false)
    private Double bidAmount;

    @Column(name = "motivation", length = 1000)
    private String motivation;

    @Column(name = "delivery_days", nullable = false)
    private Integer deliveryDays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BidStatus status;

    // Add this getter to include freelancer ID in JSON response
    public UUID getFreelancerId() {
        return freelancer != null ? freelancer.getId() : null;
    }

    // Optional: Add freelancer email getter for convenience
    public String getFreelancerEmail() {
        return freelancer != null ? freelancer.getEmail() : null;
    }
}