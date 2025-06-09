package org.example.freelynk.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SavedFreelancerDTO {
    private UUID freelancerId;
    private String firstName;
    private String description;
    private String occupation;
    private double rating;
    private String lastName;
}