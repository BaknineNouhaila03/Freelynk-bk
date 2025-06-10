// src/main/java/org/example/freelynk/repository/PasswordResetTokenRepository.java
package org.example.freelynk.repository;

import org.example.freelynk.model.PasswordResetToken;
import org.example.freelynk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByTokenAndUsedFalse(String token);

    @Query("SELECT p FROM PasswordResetToken p WHERE p.user = :user AND p.used = false")
    Optional<PasswordResetToken> findActiveTokenByUser(@Param("user") User user);

    @Modifying
    @Transactional
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiryDate < :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE PasswordResetToken p SET p.used = true WHERE p.user = :user AND p.used = false")
    void markAllUserTokensAsUsed(@Param("user") User user);
}