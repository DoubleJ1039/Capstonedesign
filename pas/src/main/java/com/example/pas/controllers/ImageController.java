package com.example.pas.controllers;

import com.example.pas.models.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*", allowCredentials = "false")
public class ImageController {

    private final CloudinaryService cloudinaryService;

    @Autowired
    public ImageController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    // 이미지 업로드
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            Map uploadResult = cloudinaryService.uploadImage(file);
            return ResponseEntity.ok(uploadResult);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("이미지 업로드 실패: " + e.getMessage());
        }
    }

    // 이미지 삭제
    @PostMapping("/delete")
    public ResponseEntity<?> deleteImage(@RequestBody Map<String, String> request) {
        String publicId = request.get("publicId");
        if (publicId == null || publicId.isBlank()) {
            return ResponseEntity.badRequest().body("public_id가 필요합니다.");
        }

        try {
            Map result = cloudinaryService.deleteImage(publicId);
            return ResponseEntity.ok(result);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("삭제 실패: " + e.getMessage());
        }
    }
}
