package com.cryptobank.backend.controller;

import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.MinioService;
import com.cryptobank.backend.services.UserService;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    private final UserService userService;

    @PostMapping
    public String uploadImage(@RequestParam MultipartFile file) throws IOException {
        return minioService.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getContentType());
    }

    @DeleteMapping
    public void deleteImage(@RequestParam String file) {
        minioService.deleteFile(file);
    }

    @PostMapping(value = "/{userId}", produces = MediaType.MULTIPART_FORM_DATA_VALUE)
    public User addAvatarToUser(@PathVariable String userId, @RequestParam MultipartFile file) throws IOException {
        User user = userService.getUserKYC(userId);
        String fileUrl = minioService.uploadFile(file.getOriginalFilename(), file.getInputStream(), file.getContentType());
        user.setAvatarUrl(fileUrl);
        return user;
    }

}
