package org.example.freelynk.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AddBidRequest {
    private UUID projectId;
    private String offer;
    private String deliveryTime;
    private String motivation;
    private String freelancerEmail;

}
