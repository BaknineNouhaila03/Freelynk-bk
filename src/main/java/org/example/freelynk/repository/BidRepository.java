package org.example.freelynk.repository;

import org.example.freelynk.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByProjectId(UUID projectId);
}
