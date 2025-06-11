package org.example.freelynk.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/debug")
public class DebugController {

    @GetMapping("/upload-info")
    public ResponseEntity<Map<String, Object>> getUploadInfo() {
        Map<String, Object> info = new HashMap<>();

        File uploadDir = new File("uploads/gigs/");
        info.put("uploadDirPath", uploadDir.getAbsolutePath());
        info.put("uploadDirExists", uploadDir.exists());
        info.put("uploadDirCanRead", uploadDir.canRead());

        if (uploadDir.exists()) {
            File[] files = uploadDir.listFiles();
            info.put("fileCount", files != null ? files.length : 0);
            if (files != null && files.length > 0) {
                String[] fileNames = new String[Math.min(5, files.length)];
                for (int i = 0; i < fileNames.length; i++) {
                    fileNames[i] = files[i].getName();
                }
                info.put("sampleFiles", fileNames);
            }
        }

        return ResponseEntity.ok(info);
    }
}