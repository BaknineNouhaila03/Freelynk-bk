package org.example.freelynk.service;

import org.example.freelynk.service.NotificationService;
import org.example.freelynk.dto.AddBidRequest;
import org.example.freelynk.dto.BidResponseDTO;
import org.example.freelynk.model.*;
import org.example.freelynk.repository.BidRepository;
import org.example.freelynk.repository.FreelancerRepository;
import org.example.freelynk.repository.ProjectRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class BidService {

    private final BidRepository bidRepository;
    private final ProjectRepository projectRepository;
    private final FreelancerRepository freelancerRepository;
    private final NotificationService notificationService;

    public BidService(BidRepository bidRepository, ProjectRepository projectRepository,
            FreelancerRepository freelancerRepository,NotificationService notificationService) {
        this.bidRepository = bidRepository;
        this.projectRepository = projectRepository;
        this.freelancerRepository = freelancerRepository;
        this.notificationService=notificationService;
    }

    public BidResponseDTO addBid(AddBidRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("Project not found"));

        Freelancer freelancer = freelancerRepository.findByEmail(request.getFreelancerEmail())
                .orElseThrow(() -> new RuntimeException("Freelancer not found"));

        boolean hasBid = project.getBids().stream()
                .anyMatch(bid -> bid.getFreelancer().getId().equals(freelancer.getId()));

        if (hasBid) {
            throw new RuntimeException("You have already submitted a bid for this project");
        }

        Bid bid = new Bid();
        bid.setFreelancer(freelancer);
        bid.setProject(project);

        Double bidAmount = parseOfferAmount(request.getOffer());
        bid.setBidAmount(bidAmount);

        Integer deliveryDays = parseDeliveryDays(request.getDeliveryTime());
        bid.setDeliveryDays(deliveryDays);

        bid.setStatus(BidStatus.PENDING);
        bid.setMotivation(request.getMotivation());

        Bid savedBid = bidRepository.save(bid);
        Integer currentBidNumber = project.getBidNumber();
        if (currentBidNumber == null) {
            currentBidNumber = 0; // Handle null case
        }
        project.setBidNumber(currentBidNumber + 1);
        projectRepository.save(project);
          UUID clientId = bid.getProject().getClient().getId();
          String projectName = bid.getProject().getName();
notificationService.createNotification(clientId, "NEW_BID", "You have a new bid on "+projectName);
        return new BidResponseDTO(savedBid);
    }

    private Double parseOfferAmount(String offer) {
        if (offer == null || offer.trim().isEmpty()) {
            throw new IllegalArgumentException("Offer amount is required");
        }

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

    @Transactional
    public void updateBidStatus(UUID bidId, BidStatus newStatus) {
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new RuntimeException("Bid not found"));

        Project project = bid.getProject();

        if (newStatus == BidStatus.ACCEPTED && bid.getStatus() != BidStatus.ACCEPTED) {
            bid.setStatus(BidStatus.ACCEPTED);
            bidRepository.save(bid);

            project.setFreelancer(bid.getFreelancer());
            projectRepository.save(project);

            List<Bid> otherBids = bidRepository.findByProjectId(project.getId());
            for (Bid other : otherBids) {
                if (!other.getId().equals(bid.getId())) {
                    other.setStatus(BidStatus.REJECTED);
                    bidRepository.save(other);

                }
            }
        } else {
            bid.setStatus(newStatus);
            bidRepository.save(bid);
            
    String freelancerId = bid.getFreelancer().getId().toString();
        }
    }

}