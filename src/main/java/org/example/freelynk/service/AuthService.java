package org.example.freelynk.service;

import lombok.RequiredArgsConstructor;
import org.example.freelynk.dto.*;
import org.example.freelynk.model.*;
import org.example.freelynk.repository.*;
import org.example.freelynk.security.JwtUtils;
import org.springframework.security.authentication.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

        freelancerRepository.save(freelancer);
        return "Freelancer registered successfully.";
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
