package com.toeic.exam.service;

import com.toeic.exam.config.CloudflareR2Properties;
import com.toeic.exam.service.dto.FileUploadResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

/**
 * Service quản lý lưu trữ, tải lên và xóa file hình ảnh / âm thanh trên Cloudflare R2.
 */
@Service
public class FileStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

    private final S3Client s3Client;
    private final CloudflareR2Properties properties;

    public FileStorageService(S3Client s3Client, CloudflareR2Properties properties) {
        this.s3Client = s3Client;
        this.properties = properties;
    }

    /**
     * Upload file âm thanh TOEIC (MP3, WAV, AAC,...) lên thư mục audio/
     *
     * @param file file âm thanh từ client
     * @return FileUploadResponse chứa URL và key
     */
    public FileUploadResponse uploadAudio(MultipartFile file) {
        validateFile(file, "audio");
        return uploadFile(file, "audio");
    }

    /**
     * Upload hình ảnh câu hỏi TOEIC (PNG, JPG, WEBP,...) lên thư mục images/
     *
     * @param file file hình ảnh từ client
     * @return FileUploadResponse chứa URL và key
     */
    public FileUploadResponse uploadImage(MultipartFile file) {
        validateFile(file, "image");
        return uploadFile(file, "images");
    }

    /**
     * Tải file lên Cloudflare R2 theo thư mục chỉ định.
     */
    public FileUploadResponse uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không được để trống");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String fileKey = "%s/%s%s".formatted(folder, UUID.randomUUID(), extension);
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

    /**
     * Xóa file trên Cloudflare R2 theo key.
     */
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
        // Fallback về URL endpoint trực tiếp
        return "%s/%s/%s".formatted(properties.getEndpoint().replaceAll("/$", ""), properties.getBucketName(), fileKey);
    }
}
