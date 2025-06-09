
package org.example.freelynk.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.example.freelynk.dto.NotificationMessage;
import org.example.freelynk.model.Notification;
import org.example.freelynk.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationService(NotificationRepository notificationRepository, 
                             SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Crée une notification et l'envoie en temps réel via WebSocket
     */
    public Notification createNotification(UUID userId, String type, String message) {
        try {
            // 1. Créer et sauvegarder la notification en base
            Notification notification = new Notification();
            notification.setUserId(userId);
            notification.setType(type);
            notification.setMessage(message);
            notification.setRead(false);
            notification.setCreatedAt(LocalDateTime.now());
            
            Notification savedNotification = notificationRepository.save(notification);
            logger.info("Notification créée avec l'ID: {}", savedNotification.getId());
            
            // 2. Envoyer la notification via WebSocket
            sendNotificationViaWebSocket(savedNotification);
            
            return savedNotification;
            
        } catch (Exception e) {
            logger.error("Erreur lors de la création de la notification pour l'utilisateur {}: {}", 
                        userId, e.getMessage(), e);
            throw new RuntimeException("Impossible de créer la notification", e);
        }
    }

    /**
     * Envoie une notification via WebSocket
     */
    private void sendNotificationViaWebSocket(Notification notification) {
        try {
            String destination = "/topic/notifications/" + notification.getUserId();
            
            // Créer un DTO pour l'envoi WebSocket (évite les références circulaires)
            NotificationMessage notificationMessage = new NotificationMessage(
                notification.getUserId(),
                notification.getType(),
                notification.getMessage()
            );
            
            messagingTemplate.convertAndSend(destination, notificationMessage);
            logger.info("Notification envoyée via WebSocket à: {}", destination);
            
        } catch (Exception e) {
            logger.error("Erreur lors de l'envoi de la notification via WebSocket: {}", 
                        e.getMessage(), e);
            // Ne pas faire échouer la création de notification si l'envoi WebSocket échoue
        }
    }
    public List<Notification> getUserNotifications(UUID userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
}
