package org.example.freelynk.service;

import org.example.freelynk.dto.AddBidRequest;
import org.example.freelynk.dto.BidResponseDTO;
import org.example.freelynk.model.*;
import org.example.freelynk.repository.BidRepository;
import org.example.freelynk.repository.FreelancerRepository;
import org.example.freelynk.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final FreelancerRepository freelancerRepository;

    public BidService(BidRepository bidRepository, ProjectRepository projectRepository, FreelancerRepository freelancerRepository) {
        this.bidRepository = bidRepository;
        this.projectRepository = projectRepository;
        this.freelancerRepository = freelancerRepository;
    }

    public BidResponseDTO addBid(AddBidRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        // Find freelancer by email
        Freelancer freelancer = freelancerRepository.findByEmail(request.getFreelancerEmail())
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        // Check if freelancer already has a bid for this project
        boolean hasBid = project.getBids().stream()
                .anyMatch(bid -> bid.getFreelancer().getId().equals(freelancer.getId()));

        if (hasBid) {
            throw new RuntimeException("You have already submitted a bid for this project");
        }

        Bid bid = new Bid();
        bid.setFreelancer(freelancer);
        bid.setProject(project);

        // Parse the offer string to extract numeric value
        Double bidAmount = parseOfferAmount(request.getOffer());
        bid.setBidAmount(bidAmount);

        // Parse delivery time string to extract days
        Integer deliveryDays = parseDeliveryDays(request.getDeliveryTime());
        bid.setDeliveryDays(deliveryDays);

        bid.setStatus(BidStatus.PENDING);
        bid.setMotivation(request.getMotivation());

        Bid savedBid = bidRepository.save(bid);
        return new BidResponseDTO(savedBid);
    }

    private Double parseOfferAmount(String offer) {
        if (offer == null || offer.trim().isEmpty()) {
            throw new IllegalArgumentException("Offer amount is required");
        }

        // Remove currency symbols and extract numeric value
        String numericValue = offer.replaceAll("[^0-9.]", "");
        try {
            return Double.parseDouble(numericValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid offer amount format: " + offer);
        }
    }

    private Integer parseDeliveryDays(String deliveryTime) {
        if (deliveryTime == null || deliveryTime.trim().isEmpty()) {
            throw new IllegalArgumentException("Delivery time is required");
        }

        // Extract numeric value from delivery time string
        String numericValue = deliveryTime.replaceAll("[^0-9]", "");
        try {
            return Integer.parseInt(numericValue);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid delivery time format: " + deliveryTime);
        }
    }
    public List<Bid> getBidsForProject(UUID projectId) {
        return bidRepository.findByProjectId(projectId);
    }
}
