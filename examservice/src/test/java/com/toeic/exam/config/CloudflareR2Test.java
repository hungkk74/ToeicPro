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
        String accessKey = "08892b3781c8ccbddfb22200bd39332e";
        String secretKey = "00171e511d22789b7d29efcf41b97ddd60e32df8a0a2b7ce1655d8b0fcbf0696";
        String bucket = "toeic-sever";

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
            ListObjectsV2Request.builder().bucket(bucket).maxKeys(50).build()
        );

        System.out.println(">>> Cloudflare R2 Connection SUCCESS! Bucket: " + bucket);
        System.out.println(">>> Key count: " + response.keyCount());
        // Copy to clean name without spaces
        s3Client.copyObject(
            software.amazon.awssdk.services.s3.model.CopyObjectRequest.builder()
                .sourceBucket(bucket)
                .sourceKey("audio/AUDIO Test 01.mp3")
                .destinationBucket(bucket)
                .destinationKey("audio/ets2023_test01.mp3")
                .build()
        );
        System.out.println(">>> Copied to clean name: audio/ets2023_test01.mp3");

        s3Client.close();
    }
}
