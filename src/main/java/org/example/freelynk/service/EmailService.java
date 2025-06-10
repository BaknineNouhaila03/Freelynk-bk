package org.example.freelynk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendPasswordResetEmail(String toEmail, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Password Reset Request - FreeLynk");

            String resetLink = frontendUrl + "/reset-password?token=" + token;

            String emailBody = String.format(
                    "Hello,\n\n" +
                            "You have requested to reset your password for your FreeLynk account.\n\n" +
                            "Please click the link below to reset your password:\n" +
                            "%s\n\n" +
                            "This link will expire in 1 hour for security reasons.\n\n" +
                            "If you did not request this password reset, please ignore this email.\n\n" +
                            "Best regards,\n" +
                            "The FreeLynk Team",
                    resetLink
            );

            message.setText(emailBody);

            log.info("Attempting to send password reset email to: {}", toEmail);
            mailSender.send(message);
            log.info("Password reset email sent successfully to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}