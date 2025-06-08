package org.example.freelynk.dto;

import lombok.Data;

import java.util.List;

@Data
public class AddGigRequest {
    private String title;
    private String description;
    private List<String> gigUrls; 
}
