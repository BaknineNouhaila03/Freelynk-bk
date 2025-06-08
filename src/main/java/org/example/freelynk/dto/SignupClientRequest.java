package org.example.freelynk.dto;

import lombok.Data;

@Data
public class SignupClientRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
}
