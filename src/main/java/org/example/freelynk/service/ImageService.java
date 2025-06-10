package org.example.freelynk.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class ImageService {

    @Value("${server.port:8081}")
    private String serverPort;

    public String saveBase64Image(String base64Image, String directory, String filePrefix, String identifier) {
        try {
            // Remove the data URL prefix if present (e.g., "data:image/jpeg;base64,")
            String cleanBase64 = base64Image;
            if (base64Image.contains(",")) {
                cleanBase64 = base64Image.split(",")[1];
            }

            // Decode base64 to bytes
            byte[] imageBytes = Base64.getDecoder().decode(cleanBase64);

            // Create a unique filename
            String fileName = filePrefix + "_" + identifier.replaceAll("[^a-zA-Z0-9]", "_") + "_" + System.currentTimeMillis() + ".jpg";

            // Define the directory path
            String uploadDir = "uploads/" + directory + "/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Save the file
            String filePath = uploadDir + fileName;
            try (FileOutputStream fos = new FileOutputStream(filePath)) {
                fos.write(imageBytes);
            }

            // Return the full URL path that can be accessed from frontend
            return "http://localhost:" + serverPort + "/" + filePath;
        } catch (Exception e) {
            throw new RuntimeException("Failed to process and save image", e);
        }
    }

    public boolean deleteImage(String imageUrl) {
        try {
            if (imageUrl != null && !imageUrl.isEmpty()) {
                // Extract the file path from the URL
                String filePath = imageUrl.replace("http://localhost:" + serverPort + "/", "");
                File file = new File(filePath);
                return file.delete();
            }
            return false;
        } catch (Exception e) {
            // Log the error but don't throw exception
            System.err.println("Failed to delete image: " + imageUrl + " - " + e.getMessage());
            return false;
        }
    }

    public String getImageExtension(String base64Image) {
        if (base64Image.contains("data:image/")) {
            String mimeType = base64Image.substring(5, base64Image.indexOf(";"));
            return mimeType.substring(mimeType.indexOf("/") + 1);
        }
        return "jpg"; // default extension
    }

    // Helper method to get just the filename from a full URL
    public String getFileNameFromUrl(String fullUrl) {
        if (fullUrl != null && fullUrl.contains("/")) {
            return fullUrl.substring(fullUrl.lastIndexOf("/") + 1);
        }
        return fullUrl;
    }
}