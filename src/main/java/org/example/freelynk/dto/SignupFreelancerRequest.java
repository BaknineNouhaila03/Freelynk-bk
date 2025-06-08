package org.example.freelynk.dto;

import lombok.Data;

import java.util.List;

@Data
public class SignupFreelancerRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String confirmPassword;
    private String description;
    private Integer yearsOfExp;
    private String location;
    private String languages;
    private String occupation;
    private List<String> skills;
    private String phone;
    private Double rating;
}
