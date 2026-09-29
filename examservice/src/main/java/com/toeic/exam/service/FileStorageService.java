package com.toeic.exam.service;

import com.toeic.exam.config.CloudflareR2Properties;
import com.toeic.exam.service.dto.FileUploadResponse;
import com.toeic.exam.service.media.AudioCompressionService;
import com.toeic.exam.service.media.ImageCompressionService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service điều phối lưu trữ file và upload lên Cloudflare R2 / S3.
 */
@Service
public class FileStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

    private final S3Client s3Client;
    private final CloudflareR2Properties properties;
    private final AudioCompressionService audioCompressionService;
    private final ImageCompressionService imageCompressionService;

    public FileStorageService(
        S3Client s3Client,
        CloudflareR2Properties properties,
        AudioCompressionService audioCompressionService,
        ImageCompressionService imageCompressionService
    ) {
        this.s3Client = s3Client;
        this.properties = properties;
        this.audioCompressionService = audioCompressionService;
        this.imageCompressionService = imageCompressionService;
    }

    public FileUploadResponse uploadAudio(MultipartFile file) {
        return processAudioAsync(file).join();
    }

    public CompletableFuture<FileUploadResponse> processAudioAsync(MultipartFile file) {
        validateFile(file, "audio");
        File tempInput = null;
        try {
            tempInput = File.createTempFile("audio_input_", ".tmp");
            Files.copy(file.getInputStream(), tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return uploadAudioAsync(tempInput, file.getOriginalFilename(), file.getSize());
        } catch (IOException e) {
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
            LOG.error("Failed to buffer audio upload input stream", e);
            CompletableFuture<FileUploadResponse> failed = new CompletableFuture<>();
            failed.completeExceptionally(new RuntimeException("Lỗi khi đọc file audio upload: " + e.getMessage(), e));
            return failed;
        }
    }

    @Async("fileProcessingExecutor")
    public CompletableFuture<FileUploadResponse> uploadAudioAsync(File tempInput, String originalFilename, long originalSize) {
        File tempOutput = null;

        try {
            tempOutput = audioCompressionService.compressToMp3(tempInput);

            String fileKey = generateFileKey(originalFilename, "audio", ".mp3");
            String contentType = "audio/mpeg";
            long optimizedSize = tempOutput.length();

            LOG.info("Uploading optimized MP3 to Cloudflare R2 - Bucket: {}, Key: {}, Original Size: {} bytes, Optimized Size: {} bytes",
                properties.getBucketName(), fileKey, originalSize, optimizedSize);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(tempOutput));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Optimized Audio uploaded successfully to R2: {}", fileUrl);

            return CompletableFuture.completedFuture(
                new FileUploadResponse(fileKey, fileUrl, originalFilename, optimizedSize, contentType)
            );
        } catch (Exception e) {
            LOG.error("Failed to optimize and upload audio to Cloudflare R2", e);
            CompletableFuture<FileUploadResponse> failed = new CompletableFuture<>();
            failed.completeExceptionally(new RuntimeException("Lỗi khi tối ưu và upload audio: " + e.getMessage(), e));
            return failed;
        } finally {
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
            if (tempOutput != null && tempOutput.exists()) {
                tempOutput.delete();
            }
        }
    }

    public FileUploadResponse uploadImage(MultipartFile file) {
        validateFile(file, "image");

        try {
            byte[] webpBytes = imageCompressionService.compressToWebp(file.getInputStream());

            String fileKey = generateFileKey(file.getOriginalFilename(), "images", ".webp");
            String contentType = "image/webp";

            LOG.info("Uploading optimized WebP to Cloudflare R2 - Bucket: {}, Key: {}, Original Size: {} bytes, Optimized Size: {} bytes",
                properties.getBucketName(), fileKey, file.getSize(), webpBytes.length);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromBytes(webpBytes));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Optimized Image uploaded successfully to R2: {}", fileUrl);

            return new FileUploadResponse(fileKey, fileUrl, file.getOriginalFilename(), webpBytes.length, contentType);
        } catch (Exception e) {
            LOG.error("Failed to optimize and upload image to Cloudflare R2", e);
            throw new RuntimeException("Lỗi khi tối ưu và upload ảnh: " + e.getMessage(), e);
        }
    }

    public FileUploadResponse uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không được để trống");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileKey = generateFileKey(originalFilename, folder, extension);
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        LOG.info("Uploading file to Cloudflare R2 - Bucket: {}, Key: {}, Size: {} bytes",
            properties.getBucketName(), fileKey, file.getSize());

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("File uploaded successfully to R2: {}", fileUrl);

            return new FileUploadResponse(fileKey, fileUrl, originalFilename, file.getSize(), contentType);
        } catch (IOException e) {
            LOG.error("Failed to read input stream for file: {}", originalFilename, e);
            throw new RuntimeException("Lỗi khi đọc file upload: " + e.getMessage(), e);
        } catch (Exception e) {
            LOG.error("Failed to upload file to Cloudflare R2: {}", fileKey, e);
            throw new RuntimeException("Lỗi khi upload file lên Cloudflare R2: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String fileKey) {
        if (fileKey == null || fileKey.isBlank()) {
            return;
        }

        LOG.info("Deleting file from Cloudflare R2 - Bucket: {}, Key: {}", properties.getBucketName(), fileKey);
        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .build();

            s3Client.deleteObject(deleteRequest);
            LOG.info("Deleted file successfully: {}", fileKey);
        } catch (Exception e) {
            LOG.error("Failed to delete file from Cloudflare R2: {}", fileKey, e);
            throw new RuntimeException("Lỗi khi xóa file trên Cloudflare R2: " + e.getMessage(), e);
        }
    }

    private void validateFile(MultipartFile file, String expectedTypePrefix) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith(expectedTypePrefix)) {
            throw new IllegalArgumentException(
                "Định dạng file không hợp lệ. Kỳ vọng loại '%s', nhưng nhận được: '%s'".formatted(
                    expectedTypePrefix, contentType)
            );
        }
    }

    private String buildPublicUrl(String fileKey) {
        String publicUrl = properties.getPublicUrl();
        if (publicUrl != null && !publicUrl.isBlank()) {
            return "%s/%s".formatted(publicUrl.replaceAll("/$", ""), fileKey);
        }
        return "%s/%s/%s".formatted(properties.getEndpoint().replaceAll("/$", ""), properties.getBucketName(), fileKey);
    }

    private String generateFileKey(String originalFilename, String folder, String targetExtension) {
        if (originalFilename != null && !originalFilename.isBlank()) {
            String cleanName = originalFilename.replaceAll("^.*[/\\\\]", "");
            int lastDot = cleanName.lastIndexOf('.');
            String nameWithoutExt = lastDot > 0 ? cleanName.substring(0, lastDot) : cleanName;

            String sanitized = nameWithoutExt.replaceAll("[^a-zA-Z0-9._-]", "_");
            if (sanitized.isBlank()) {
                sanitized = "file";
            } else if (sanitized.length() > 50) {
                sanitized = sanitized.substring(0, 50);
            }

            String uniquePrefix = UUID.randomUUID().toString().substring(0, 8);
            return "%s/%s_%s%s".formatted(folder, uniquePrefix, sanitized, targetExtension);
        }
        return "%s/%s%s".formatted(folder, UUID.randomUUID().toString(), targetExtension);
    }
}
