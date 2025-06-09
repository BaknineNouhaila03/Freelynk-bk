package org.example.freelynk.repository;

import org.example.freelynk.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BidRepository extends JpaRepository<Bid, UUID> {
    List<Bid> findByProjectId(UUID projectId);
@Query("SELECT COUNT(b) FROM Bid b WHERE b.project.id = :projectId")
Integer countByProjectId(@Param("projectId") UUID projectId);}
