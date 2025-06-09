package org.example.freelynk.controller;

import java.util.List;
import java.util.UUID;

import org.example.freelynk.model.Notification;
import org.example.freelynk.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    // Fetch notifications for a user (pass userId as param or get from auth)
    @GetMapping("/{userId}")
    public List<Notification> getNotifications(@PathVariable UUID userId) {
        return notificationService.getUserNotifications(userId);
    }

    // Mark notification as read
    @PostMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/test")
public Notification testNotification(@RequestBody Map<String, String> body) {
    UUID userId = UUID.fromString(body.get("userId"));
    return notificationService.createNotification(userId, "TEST", "Test notification");
}

}
