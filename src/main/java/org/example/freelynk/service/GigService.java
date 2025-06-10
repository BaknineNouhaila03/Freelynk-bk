package org.example.freelynk.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.example.freelynk.dto.AddGigRequest;
import org.example.freelynk.model.Freelancer;
import org.example.freelynk.model.Gig;
import org.example.freelynk.repository.GigRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class GigService {

    private final GigRepository gigRepository;
    private final String uploadDir;

    @Value("${server.port:8081}")
    private String serverPort;

    public GigService(GigRepository gigRepository) {
        this.gigRepository = gigRepository;
        // Create absolute path for upload directory
        this.uploadDir = new File("uploads/gigs/").getAbsolutePath() + File.separator;
        // Create upload directory if it doesn't exist
        createUploadDirectory();
    }

    private void createUploadDirectory() {
        File directory = new File(uploadDir);
        if (!directory.exists()) {
            boolean created = directory.mkdirs();
            if (created) {
                System.out.println("Upload directory created at: " + uploadDir);
            } else {
                System.err.println("Failed to create upload directory at: " + uploadDir);
            }
        } else {
            System.out.println("Upload directory exists at: " + uploadDir);
        }
    }

    public Gig addGig(AddGigRequest request, List<MultipartFile> files, Freelancer freelancer) throws IOException {
        List<String> fileUrls = new ArrayList<>();

        // Process each uploaded file
        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                String fileName = saveFile(file);
                // Store the URL that matches your backend port
                fileUrls.add("http://localhost:" + serverPort + "/uploads/gigs/" + fileName);
            }
        }

        Gig gig = Gig.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .gigUrls(fileUrls)
                .freelancer(freelancer)
                .build();

        return gigRepository.save(gig);
    }

    private String saveFile(MultipartFile file) throws IOException {
        // Generate unique filename to avoid conflicts
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = System.currentTimeMillis() + "_" + UUID.randomUUID().toString() + extension;

        // Save file to local storage
        Path filePath = Paths.get(uploadDir + uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        System.out.println("File saved to: " + filePath.toString());
        return uniqueFilename;
    }

    public Gig getGigById(UUID id) {
        return gigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Gig not found with id: " + id));
    }

    public List<Gig> getGigsForFreelancer(Freelancer freelancer) {
        return gigRepository.findByFreelancer(freelancer);
    }
}