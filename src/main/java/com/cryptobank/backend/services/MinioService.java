package com.cryptobank.backend.services;

import com.cryptobank.backend.configuration.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioConfig minioConfig;

    public String uploadFile(String object, InputStream inputStream, String contentType) {
        return uploadFile(object, minioConfig.getBucket(), inputStream, contentType);
    }

    @SneakyThrows(Exception.class)
    public String uploadFile(String object, String bucket, InputStream inputStream, String contentType) {
        String filename = UUID.randomUUID() + getFileType(object);
        createBucket(bucket);
        minioClient.putObject(PutObjectArgs.builder()
            .bucket(bucket)
            .object(filename)
            .contentType(contentType)
            .stream(inputStream, inputStream.available(), -1)
            .build());
        return minioConfig.getEndpoint() + "/" + minioConfig.getEndpoint() + "/" + filename;
    }

    public void deleteFile(String object) {
        deleteFile(object, minioConfig.getBucket());
    }

    @SneakyThrows(Exception.class)
    public void deleteFile(String object, String bucket) {
        createBucket(bucket);
        minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucket).object(object).build());
    }

    @SneakyThrows(Exception.class)
    public boolean isBucketExists(String bucket) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
    }

    @SneakyThrows(Exception.class)
    public void createBucket(String bucket) {
        if (!isBucketExists(bucket)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
    }

    private String getFileType(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1 || lastDot == filename.length() - 1) {
            return "";
        }
        return filename.substring(lastDot);
    }

}
