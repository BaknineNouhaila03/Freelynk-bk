package org.example.freelynk.model;

import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "saved_freelancers")
public class SavedFreelancer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "client_id")
    private UUID clientId;
    
    @Column(name = "freelancer_id") 
    private UUID freelancerId;
    
    public SavedFreelancer() {}
    
    public SavedFreelancer(UUID clientId, UUID freelancerId) {
        this.clientId = clientId;
        this.freelancerId = freelancerId;
    }
    


}