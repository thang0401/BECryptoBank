package com.cryptobank.backend.controller;

import com.cryptobank.backend.DTO.UrlImage;
import com.cryptobank.backend.DTO.UserInformation;
import com.cryptobank.backend.entity.User;
import com.cryptobank.backend.services.MinioService;
import com.cryptobank.backend.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/image", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "Image", description = "Hình ảnh")
@SecurityRequirement(name = "Bearer Authorization")
public class UploadImageController {

    private final MinioService minioService;
    private final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Lấy đường dẫn từ tên file hình ảnh"
    )
    public UrlImage getUrlImage(
        @Parameter(description = "Tên file") @RequestParam String fileName,
        @Parameter(description = "Tên bucket") @RequestParam(required = false) String bucket
    ) {
        return new UrlImage(minioService.getUrl(fileName, bucket));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Tải một file hình ảnh lên server"
    )
    public UrlImage uploadImage(
        @Parameter(description = "File hình ảnh") @RequestParam MultipartFile file,
        @Parameter(description = "Tên bucket") @RequestParam(required = false) String bucket
    ) throws IOException {
        return new UrlImage(minioService.uploadFile(file.getOriginalFilename(), bucket, file.getInputStream(), file.getContentType()));
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Xóa một hình ảnh trên server dựa theo tên file"
    )
    public void deleteImage(
        @Parameter(description = "Tên file") @RequestParam String file,
        @Parameter(description = "Tên bucket") @RequestParam(required = false) String bucket
    ) {
        minioService.deleteFile(file, bucket);
    }

    @PostMapping(value = "/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Tải một file hình ảnh lên server và lưu tên hình ảnh vào user avatar cụ thể"
    )
    public UserInformation addAvatarToUser(
        @Parameter(description = "ID user") @PathVariable String userId,
        @Parameter(description = "FIle hình ảnh") @RequestParam MultipartFile file,
        @Parameter(description = "Tên bucket") @RequestParam(required = false) String bucket
    ) throws IOException {
        User user = userService.getUserEntity(userId);
        String fileUrl = minioService.uploadFile(file.getOriginalFilename(), bucket, file.getInputStream(), file.getContentType());
        user.setAvatar(fileUrl);
        return userService.convertToUserInformation(user);
    }

}
