package org.example.freelynk.service;

import java.util.List;
import java.util.UUID;

import org.example.freelynk.dto.AddGigRequest;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Gig;
import org.example.freelynk.repository.GigRepository;
import org.springframework.stereotype.Service;

@Service
public class GigService {

    private final GigRepository gigRepository;

    public GigService(GigRepository gigRepository) {
        this.gigRepository = gigRepository;
    }

    public Gig addGig(AddGigRequest request, Freelancer freelancer) {
        Gig gig = Gig.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .gigUrls(request.getGigUrls())
                .freelancer(freelancer)
                .build();
        return gigRepository.save(gig);
    }

public Gig getGigById(UUID id) {
    return gigRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Gig not found with id: " + id));
}
public List<Gig> getGigsForFreelancer(Freelancer freelancer) {
    return gigRepository.findByFreelancer(freelancer);
}


}
