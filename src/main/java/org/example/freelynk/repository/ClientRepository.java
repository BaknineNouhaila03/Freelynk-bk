package org.example.freelynk.repository;

import java.util.Optional;
import java.util.UUID;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {

    @Query("SELECT c FROM Client c WHERE c.id = :userId")
    Optional<Client> findById(@Param("userId") UUID userId);
    Optional<Client> findByEmail(String email);
}
