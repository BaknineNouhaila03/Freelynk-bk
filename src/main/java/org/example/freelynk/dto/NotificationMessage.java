package org.example.freelynk.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@NoArgsConstructor@AllArgsConstructor
public class NotificationMessage {
    private UUID userId;  // recipient user id
    private String type;
    private String message;

}
