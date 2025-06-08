package org.example.freelynk.security;

import java.util.UUID;

import org.example.freelynk.model.Client;
import org.example.freelynk.model.User;
import org.example.freelynk.repository.ClientRepository; // Add this import
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private static ClientRepository clientRepository;

    @Autowired
    public SecurityUtil(ClientRepository clientRepository) {
        SecurityUtil.clientRepository = clientRepository;
    }

    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthorized");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails customUserDetails) {
            return customUserDetails.getUser(); 
        } else {
            throw new RuntimeException("Unexpected principal type");
        }
    }
    public static Client getCurrentClient() {
    User currentUser = getCurrentUser();
    System.out.println(currentUser.getId());
    System.out.println("WOWOWOOWOWOWOOOOOOOOOOOOOOOOW");
    return clientRepository.findById(currentUser.getId())
            .orElseThrow(() -> new RuntimeException("Current user is not a client"));
}


}




