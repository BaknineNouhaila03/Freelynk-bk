package org.example.freelynk.service;

import lombok.RequiredArgsConstructor;
import org.example.freelynk.dto.*;
import org.example.freelynk.model.*;
import org.example.freelynk.repository.*;
import org.example.freelynk.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final FreelancerRepository freelancerRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public JwtResponse login(LoginRequest request) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtils.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtils.generateRefreshToken(user.getEmail());

        return new JwtResponse(accessToken, refreshToken, user.getRole().name(), user.getEmail(),user.getFirstName());
    }

    public String registerClient(SignupClientRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        Client client = new Client();
        client.setFirstName(request.getFirstName());
        client.setLastName(request.getLastName());
        client.setEmail(request.getEmail());
        client.setPassword(passwordEncoder.encode(request.getPassword()));
        client.setRole(Role.CLIENT);

        clientRepository.save(client);
        return "Client registered successfully.";
    }

    @Autowired
    private ImageService imageService;

    public String registerFreelancer(SignupFreelancerRequest request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        Freelancer freelancer = new Freelancer();
        freelancer.setFirstName(request.getFirstName());
        freelancer.setLastName(request.getLastName());
        freelancer.setEmail(request.getEmail());
        freelancer.setPassword(passwordEncoder.encode(request.getPassword()));
        freelancer.setDescription(request.getDescription());
        freelancer.setYearsOfExp(request.getYearsOfExp());
        freelancer.setLocation(request.getLocation());
        freelancer.setLanguages(request.getLanguages());
        freelancer.setOccupation(request.getOccupation());
        freelancer.setSkills(request.getSkills());
        freelancer.setPhone(request.getPhone());
        freelancer.setRating(request.getRating());
        freelancer.setRole(Role.FREELANCER);

        // Handle profile image processing and saving
        if (request.getProfileImage() != null && !request.getProfileImage().isEmpty()) {
            try {
                String imagePath = imageService.saveBase64Image(
                        request.getProfileImage(),
                        "profile-images",
                        "profile",
                        request.getEmail()
                );
                freelancer.setProfileImage(imagePath);
            } catch (Exception e) {
                throw new RuntimeException("Failed to save profile image: " + e.getMessage());
            }
        }

        freelancerRepository.save(freelancer);
        return "Freelancer registered successfully.";
    } // Make sure you have this dependency

    private String saveProfileImage(String base64Image, String email) {
        try {
            // Remove the data URL prefix if present (e.g., "data:image/jpeg;base64,")
            String cleanBase64 = base64Image;
            if (base64Image.contains(",")) {
                cleanBase64 = base64Image.split(",")[1];
            }

            // Decode base64 to bytes
            byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);

            // Create a unique filename using email and timestamp
            String fileName = "profile_" + email.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".jpg";

            // Define the directory path (make sure this directory exists)
            String uploadDir = "uploads/profile-images/";
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file
            String filePath = uploadDir + fileName;
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            }

            // Return the relative path to store in database
            return filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process and save profile image", e);
        }
    }

    public JwtResponse refresh(String refreshToken) {
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token.");
        }

        String email = jwtUtils.getEmailFromToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtUtils.generateAccessToken(user.getEmail());
        return new JwtResponse(newAccessToken, refreshToken, user.getRole().name(), user.getEmail(),user.getFirstName());
    }
}
