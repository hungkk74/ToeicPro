package com.toeic.exam.config;

import java.net.URI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;

/**
 * Cấu hình khởi tạo S3Client kết nối Cloudflare R2 Object Storage.
 */
@Configuration
public class CloudflareR2Configuration {

    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(CloudflareR2Configuration.class);

    private final CloudflareR2Properties properties;

    public CloudflareR2Configuration(CloudflareR2Properties properties) {
        this.properties = properties;
    }

    @Bean
    public S3Client s3Client() {
        String accessKey = properties.getAccessKey();
        String secretKey = properties.getSecretKey();

        if (accessKey == null || accessKey.isBlank() || secretKey == null || secretKey.isBlank()) {
            LOG.warn("Cloudflare R2 credentials are not configured or blank. Initializing S3Client with placeholder credentials for local dev.");
            accessKey = "dummy-access-key";
            secretKey = "dummy-secret-key";
        }

        String endpoint = properties.getEndpoint();
        URI endpointUri = (endpoint != null && !endpoint.isBlank())
            ? URI.create(endpoint)
            : URI.create("https://localhost");

        return S3Client.builder()
            .endpointOverride(endpointUri)
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .region(Region.of("auto"))
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();
    }
}
