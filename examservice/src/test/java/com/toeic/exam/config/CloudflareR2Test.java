package com.toeic.exam.config;

import java.net.URI;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;

class CloudflareR2Test {

    @Test
    @Disabled("Chạy thủ công khi cần kiểm tra kết nối trực tiếp tới Cloudflare R2")
    void testCloudflareR2Connection() {
        String endpoint = "https://64c053901170dae4a44916ada3c5847f.r2.cloudflarestorage.com";
        String accessKey = System.getenv("CLOUDFLARE_R2_ACCESS_KEY");
        String secretKey = System.getenv("CLOUDFLARE_R2_SECRET_KEY");
        String bucket = "toeic-sever";

        if (accessKey == null || secretKey == null) {
            System.out.println(">>> SKIP: CLOUDFLARE_R2_ACCESS_KEY / SECRET_KEY env vars not set");
            return;
        }

        S3Client s3Client = S3Client.builder()
            .endpointOverride(URI.create(endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
            ))
            .region(Region.of("auto"))
            .serviceConfiguration(S3Configuration.builder()
                .pathStyleAccessEnabled(true)
                .build())
            .build();

        ListObjectsV2Response response = s3Client.listObjectsV2(
            ListObjectsV2Request.builder().bucket(bucket).maxKeys(5).build()
        );

        System.out.println(">>> Cloudflare R2 Connection SUCCESS! Bucket: " + bucket);
        System.out.println(">>> Key count: " + response.keyCount());
        response.contents().forEach(obj ->
            System.out.println("    - " + obj.key() + " (" + obj.size() + " bytes)")
        );

        s3Client.close();
    }
}
