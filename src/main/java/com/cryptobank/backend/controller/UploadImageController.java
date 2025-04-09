package com.cryptobank.backend.controller;

import com.cryptobank.backend.services.MinioService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/image")
@RequiredArgsConstructor
public class UploadImageController {

    private final MinioService minioService;

    @PostMapping
    public String uploadImage(@RequestParam MultipartFile file) throws IOException {
        return minioService.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getContentType());
    }

    @DeleteMapping
    public void deleteImage(@RequestParam String file) {
        minioService.deleteFile(file);
    }

}
