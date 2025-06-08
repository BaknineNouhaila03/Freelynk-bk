package org.example.freelynk.controller;

import lombok.RequiredArgsConstructor;
import org.example.freelynk.dto.*;
import org.example.freelynk.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/signup/client")
    public ResponseEntity<String> registerClient(@RequestBody SignupClientRequest request) {
        return ResponseEntity.ok(authService.registerClient(request));
    }

    @PostMapping("/signup/freelancer")
    public ResponseEntity<String> registerFreelancer(@RequestBody SignupFreelancerRequest request) {
        return ResponseEntity.ok(authService.registerFreelancer(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> refresh(@RequestParam String token) {
        return ResponseEntity.ok(authService.refresh(token));
    }
}
