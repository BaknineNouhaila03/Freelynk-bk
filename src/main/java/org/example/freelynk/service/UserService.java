package org.example.freelynk.service;

import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.User;
import org.example.freelynk.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
}
