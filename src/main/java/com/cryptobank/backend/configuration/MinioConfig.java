package com.cryptobank.backend.configuration;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class MinioConfig {

    @Value("${MINIO_BUCKET}")
    private String bucket;
    @Value("${MINIO_URL}")
    private String endpoint;
    @Value("${MINIO_ACCESS_KEY}")
    private String accessKey;
    @Value("${MINIO_SECRET_KEY}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(accessKey, secretKey)
            .build();
    }


}
