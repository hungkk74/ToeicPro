package com.toeic.exam.service;

import com.toeic.exam.config.CloudflareR2Properties;
import com.toeic.exam.service.dto.FileUploadResponse;
import com.toeic.exam.service.media.AudioCompressionService;
import com.toeic.exam.service.media.ImageCompressionService;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsResponse;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Error;

/**
 * Service điều phối lưu trữ file và upload lên Cloudflare R2 / S3.
 * Tối ưu High-Throughput I/O, chống OOM, bảo mật Magic Bytes và Cloudflare CDN caching.
 */
@Service
public class FileStorageService {

    private static final Logger LOG = LoggerFactory.getLogger(FileStorageService.class);

    private static final String CACHE_CONTROL_IMMUTABLE = "public, max-age=31536000, immutable";

    private static final long MAX_IMAGE_SIZE = 10L * 1024 * 1024; // 10MB
    private static final long MAX_AUDIO_SIZE = 50L * 1024 * 1024; // 50MB (đáp ứng trọn gói file Audio Full Test TOEIC)
    private static final long MAX_GENERIC_FILE_SIZE = 50L * 1024 * 1024;

    private static final Set<String> ALLOWED_IMAGE_EXTENSIONS = Set.of(
        "jpg", "jpeg", "png", "webp", "gif", "bmp"
    );

    private static final Set<String> ALLOWED_AUDIO_EXTENSIONS = Set.of(
        "mp3", "wav", "m4a", "ogg", "flac", "aac"
    );

    private final S3Client s3Client;
    private final CloudflareR2Properties properties;
    private final AudioCompressionService audioCompressionService;
    private final ImageCompressionService imageCompressionService;
    private final Executor fileProcessingExecutor;

    public FileStorageService(
        S3Client s3Client,
        CloudflareR2Properties properties,
        AudioCompressionService audioCompressionService,
        ImageCompressionService imageCompressionService,
        @Qualifier("fileProcessingExecutor") Executor fileProcessingExecutor
    ) {
        this.s3Client = s3Client;
        this.properties = properties;
        this.audioCompressionService = audioCompressionService;
        this.imageCompressionService = imageCompressionService;
        this.fileProcessingExecutor = fileProcessingExecutor;
    }

    /**
     * Upload và nén Audio bất đồng bộ non-blocking (khuyến nghị dùng trên Web/API).
     */
    public CompletableFuture<FileUploadResponse> processAudioAsync(MultipartFile file) {
        validateUploadedFile(file, MAX_AUDIO_SIZE, ALLOWED_AUDIO_EXTENSIONS, true);

        Path tempInput = null;
        try {
            tempInput = Files.createTempFile("audio_in_", ".tmp");
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            validateMagicBytes(tempInput, true);

            final Path finalTempInput = tempInput;
            String originalFilename = file.getOriginalFilename();
            long originalSize = file.getSize();

            return CompletableFuture.supplyAsync(
                () -> executeAudioProcessing(finalTempInput, originalFilename, originalSize),
                fileProcessingExecutor
            );
        } catch (Exception e) {
            safeDelete(tempInput);
            LOG.error("Lỗi khi tiền xử lý audio upload: {}", file.getOriginalFilename(), e);
            return CompletableFuture.failedFuture(new RuntimeException("Lỗi khi đọc file audio upload: " + e.getMessage(), e));
        }
    }

    /**
     * Upload Audio đồng bộ trực tiếp trên calling thread (không dùng .join() trên Thread Pool).
     */
    public FileUploadResponse uploadAudio(MultipartFile file) {
        validateUploadedFile(file, MAX_AUDIO_SIZE, ALLOWED_AUDIO_EXTENSIONS, true);

        Path tempInput = null;
        try {
            tempInput = Files.createTempFile("audio_in_", ".tmp");
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            validateMagicBytes(tempInput, true);
            return executeAudioProcessing(tempInput, file.getOriginalFilename(), file.getSize());
        } catch (Exception e) {
            safeDelete(tempInput);
            LOG.error("Lỗi khi upload audio đồng bộ: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Lỗi khi upload audio: " + e.getMessage(), e);
        }
    }

    /**
     * Upload và nén Image sang WebP bất đồng bộ non-blocking.
     */
    public CompletableFuture<FileUploadResponse> processImageAsync(MultipartFile file) {
        validateUploadedFile(file, MAX_IMAGE_SIZE, ALLOWED_IMAGE_EXTENSIONS, false);

        Path tempInput = null;
        try {
            tempInput = Files.createTempFile("image_in_", ".tmp");
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            validateMagicBytes(tempInput, false);

            final Path finalTempInput = tempInput;
            String originalFilename = file.getOriginalFilename();
            long originalSize = file.getSize();

            return CompletableFuture.supplyAsync(
                () -> executeImageProcessing(finalTempInput, originalFilename, originalSize),
                fileProcessingExecutor
            );
        } catch (Exception e) {
            safeDelete(tempInput);
            LOG.error("Lỗi khi tiền xử lý image upload: {}", file.getOriginalFilename(), e);
            return CompletableFuture.failedFuture(new RuntimeException("Lỗi khi đọc file ảnh upload: " + e.getMessage(), e));
        }
    }

    /**
     * Upload Image đồng bộ trực tiếp trên calling thread.
     */
    public FileUploadResponse uploadImage(MultipartFile file) {
        validateUploadedFile(file, MAX_IMAGE_SIZE, ALLOWED_IMAGE_EXTENSIONS, false);

        Path tempInput = null;
        try {
            tempInput = Files.createTempFile("image_in_", ".tmp");
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }
            validateMagicBytes(tempInput, false);
            return executeImageProcessing(tempInput, file.getOriginalFilename(), file.getSize());
        } catch (Exception e) {
            safeDelete(tempInput);
            LOG.error("Lỗi khi upload hình ảnh đồng bộ: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Lỗi khi upload hình ảnh: " + e.getMessage(), e);
        }
    }

    /**
     * Upload file tĩnh thông thường (stream file tạm trung gian, không nạp mảng byte[] vào Heap).
     */
    public FileUploadResponse uploadFile(MultipartFile file, String folder) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không được để trống");
        }
        if (file.getSize() > MAX_GENERIC_FILE_SIZE) {
            throw new IllegalArgumentException(
                "Kích thước file vượt quá giới hạn (%d MB). Kích thước hiện tại: %d MB"
                    .formatted(MAX_GENERIC_FILE_SIZE / (1024 * 1024), file.getSize() / (1024 * 1024))
            );
        }

        String originalFilename = file.getOriginalFilename();
        String extension = extractExtension(originalFilename);
        String targetExtension = extension.isBlank() ? "" : "." + extension;
        String fileKey = generateFileKey(originalFilename, folder, targetExtension);
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        LOG.info("Uploading generic file to Cloudflare R2 - Bucket: {}, Key: {}, Size: {} bytes",
            properties.getBucketName(), fileKey, file.getSize());

        Path tempInput = null;
        try {
            tempInput = Files.createTempFile("upload_generic_", ".tmp");
            try (InputStream is = file.getInputStream()) {
                Files.copy(is, tempInput, StandardCopyOption.REPLACE_EXISTING);
            }

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .cacheControl(CACHE_CONTROL_IMMUTABLE)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(tempInput));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Generic file uploaded successfully to R2: {}", fileUrl);

            return new FileUploadResponse(fileKey, fileUrl, originalFilename, file.getSize(), contentType);
        } catch (Exception e) {
            LOG.error("Failed to upload generic file to Cloudflare R2: {}", fileKey, e);
            throw new RuntimeException("Lỗi khi upload file lên Cloudflare R2: " + e.getMessage(), e);
        } finally {
            safeDelete(tempInput);
        }
    }

    public void deleteFile(String fileKeyOrUrl) {
        String fileKey = extractFileKey(fileKeyOrUrl);
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

    public void deleteFiles(Collection<String> fileKeysOrUrls) {
        if (fileKeysOrUrls == null || fileKeysOrUrls.isEmpty()) {
            return;
        }

        List<ObjectIdentifier> objectsToDelete = fileKeysOrUrls.stream()
            .map(this::extractFileKey)
            .filter(key -> key != null && !key.isBlank())
            .distinct()
            .map(key -> ObjectIdentifier.builder().key(key).build())
            .toList();

        if (objectsToDelete.isEmpty()) {
            return;
        }

        LOG.info("Batch deleting {} files from Cloudflare R2 - Bucket: {}", objectsToDelete.size(), properties.getBucketName());

        final int batchSize = 1000;
        for (int i = 0; i < objectsToDelete.size(); i += batchSize) {
            List<ObjectIdentifier> batch = objectsToDelete.subList(i, Math.min(i + batchSize, objectsToDelete.size()));
            try {
                DeleteObjectsRequest deleteRequest = DeleteObjectsRequest.builder()
                    .bucket(properties.getBucketName())
                    .delete(Delete.builder().objects(batch).quiet(true).build())
                    .build();

                DeleteObjectsResponse response = s3Client.deleteObjects(deleteRequest);
                if (response.hasErrors() && !response.errors().isEmpty()) {
                    for (S3Error error : response.errors()) {
                        LOG.warn("Lỗi khi xóa file trên Cloudflare R2 - Key: {}, Code: {}, Message: {}",
                            error.key(), error.code(), error.message());
                    }
                } else {
                    LOG.info("Xóa thành công batch {} files từ Cloudflare R2", batch.size());
                }
            } catch (Exception e) {
                LOG.error("Failed to batch delete files from Cloudflare R2 (batch size: {})", batch.size(), e);
            }
        }
    }

    public CompletableFuture<Void> deleteFilesAsync(Collection<String> fileKeysOrUrls) {
        if (fileKeysOrUrls == null || fileKeysOrUrls.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        return CompletableFuture.runAsync(() -> {
            try {
                deleteFiles(fileKeysOrUrls);
            } catch (Exception e) {
                LOG.error("Async batch deletion from Cloudflare R2 failed", e);
            }
        }, fileProcessingExecutor);
    }

    public String extractFileKey(String fileUrlOrKey) {
        if (fileUrlOrKey == null || fileUrlOrKey.isBlank()) {
            return null;
        }
        String trimmed = fileUrlOrKey.trim();

        if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
            return trimmed.replaceFirst("^/+", "");
        }

        String publicUrl = properties.getPublicUrl();
        if (publicUrl != null && !publicUrl.isBlank()) {
            String cleanPublic = publicUrl.replaceAll("/+$", "");
            if (trimmed.startsWith(cleanPublic)) {
                String sub = trimmed.substring(cleanPublic.length()).replaceFirst("^/+", "");
                int queryIdx = sub.indexOf('?');
                return queryIdx != -1 ? sub.substring(0, queryIdx) : sub;
            }
        }

        try {
            java.net.URI uri = java.net.URI.create(trimmed);
            String path = uri.getPath();
            if (path != null) {
                path = path.replaceFirst("^/+", "");
                String bucket = properties.getBucketName();
                if (bucket != null && !bucket.isBlank() && path.startsWith(bucket + "/")) {
                    path = path.substring(bucket.length() + 1);
                }
                return path;
            }
        } catch (Exception ignored) {
            // URI parsing fallback
        }

        return trimmed;
    }

    // --- Private Processing & Helper Methods ---

    private FileUploadResponse executeAudioProcessing(Path tempInput, String originalFilename, long originalSize) {
        Path tempOutput = null;
        try {
            File compressedFile = audioCompressionService.compressToMp3(tempInput.toFile());
            tempOutput = compressedFile.toPath();

            String fileKey = generateFileKey(originalFilename, "audio", ".mp3");
            String contentType = "audio/mpeg";
            long optimizedSize = Files.size(tempOutput);

            LOG.info("Uploading optimized MP3 to Cloudflare R2 - Bucket: {}, Key: {}, Original: {} bytes, Optimized: {} bytes",
                properties.getBucketName(), fileKey, originalSize, optimizedSize);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .cacheControl(CACHE_CONTROL_IMMUTABLE)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(tempOutput));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Optimized Audio uploaded successfully to R2: {}", fileUrl);

            return new FileUploadResponse(fileKey, fileUrl, originalFilename, optimizedSize, contentType);
        } catch (Exception e) {
            LOG.error("Failed to optimize and upload audio to Cloudflare R2", e);
            throw new RuntimeException("Lỗi khi tối ưu và upload audio: " + e.getMessage(), e);
        } finally {
            safeDelete(tempInput);
            safeDelete(tempOutput);
        }
    }

    private FileUploadResponse executeImageProcessing(Path tempInput, String originalFilename, long originalSize) {
        Path tempOutput = null;
        try {
            File compressedFile = imageCompressionService.compressToWebpFile(tempInput.toFile());
            tempOutput = compressedFile.toPath();

            String fileKey = generateFileKey(originalFilename, "images", ".webp");
            String contentType = "image/webp";
            long optimizedSize = Files.size(tempOutput);

            LOG.info("Uploading optimized WebP to Cloudflare R2 - Bucket: {}, Key: {}, Original: {} bytes, Optimized: {} bytes",
                properties.getBucketName(), fileKey, originalSize, optimizedSize);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .cacheControl(CACHE_CONTROL_IMMUTABLE)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(tempOutput));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Optimized Image uploaded successfully to R2: {}", fileUrl);

            return new FileUploadResponse(fileKey, fileUrl, originalFilename, optimizedSize, contentType);
        } catch (Exception e) {
            LOG.error("Failed to optimize and upload image to Cloudflare R2", e);
            throw new RuntimeException("Lỗi khi tối ưu và upload ảnh: " + e.getMessage(), e);
        } finally {
            safeDelete(tempInput);
            safeDelete(tempOutput);
        }
    }

    private void validateUploadedFile(MultipartFile file, long maxSizeBytes, Set<String> allowedExtensions, boolean isAudio) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File upload không được để trống");
        }
        if (file.getSize() > maxSizeBytes) {
            throw new IllegalArgumentException(
                "Kích thước file vượt quá giới hạn (%d MB). Kích thước hiện tại: %d MB"
                    .formatted(maxSizeBytes / (1024 * 1024), file.getSize() / (1024 * 1024))
            );
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException(
                "Định dạng file không được hỗ trợ: '%s'. Danh sách định dạng cho phép: %s"
                    .formatted(extension, allowedExtensions)
            );
        }
    }

    private void validateMagicBytes(Path tempFile, boolean isAudio) throws IOException {
        byte[] header = new byte[16];
        int bytesRead;
        try (InputStream is = Files.newInputStream(tempFile)) {
            bytesRead = is.read(header);
        }
        if (bytesRead < 4) {
            throw new IllegalArgumentException("File upload bị hỏng hoặc rỗng");
        }

        boolean valid = isAudio ? isAudioSignature(header, bytesRead) : isImageSignature(header, bytesRead);
        if (!valid) {
            throw new IllegalArgumentException(
                "Nội dung file không khớp với định dạng công bố (Magic Bytes mismatch). Nguy cơ file độc hại!"
            );
        }
    }

    private static boolean isImageSignature(byte[] header, int length) {
        if (header == null || length < 4) {
            return false;
        }
        // JPEG: FF D8 FF
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xFF) == 0xD8 && (header[2] & 0xFF) == 0xFF) {
            return true;
        }
        // PNG: 89 50 4E 47 0D 0A 1A 0A
        if (length >= 8 && (header[0] & 0xFF) == 0x89 && header[1] == 0x50 && header[2] == 0x4E && header[3] == 0x47
            && header[4] == 0x0D && header[5] == 0x0A && header[6] == 0x1A && header[7] == 0x0A) {
            return true;
        }
        // GIF: GIF87a or GIF89a
        if (header[0] == 'G' && header[1] == 'I' && header[2] == 'F' && header[3] == '8') {
            return true;
        }
        // WebP: RIFF....WEBP (offset 0..3 is "RIFF", offset 8..11 is "WEBP")
        if (length >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
            && header[8] == 'W' && header[9] == 'E' && header[10] == 'B' && header[11] == 'P') {
            return true;
        }
        // BMP: BM
        if (header[0] == 'B' && header[1] == 'M') {
            return true;
        }
        return false;
    }

    private static boolean isAudioSignature(byte[] header, int length) {
        if (header == null || length < 4) {
            return false;
        }
        // MP3 with ID3v2 tag: "ID3"
        if (header[0] == 'I' && header[1] == 'D' && header[2] == '3') {
            return true;
        }
        // MP3 frame sync without ID3: 11 bits set (0xFF, 0xE0..0xFF)
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xE0) == 0xE0) {
            return true;
        }
        // WAV: RIFF....WAVE
        if (length >= 12 && header[0] == 'R' && header[1] == 'I' && header[2] == 'F' && header[3] == 'F'
            && header[8] == 'W' && header[9] == 'A' && header[10] == 'V' && header[11] == 'E') {
            return true;
        }
        // OGG: "OggS"
        if (header[0] == 'O' && header[1] == 'g' && header[2] == 'g' && header[3] == 'S') {
            return true;
        }
        // FLAC: "fLaC"
        if (header[0] == 'f' && header[1] == 'L' && header[2] == 'a' && header[3] == 'C') {
            return true;
        }
        // M4A / MP4 Audio: bytes 4..7 are "ftyp"
        if (length >= 8 && header[4] == 'f' && header[5] == 't' && header[6] == 'y' && header[7] == 'p') {
            return true;
        }
        // AAC ADTS sync: 12 bits set (0xFF, 0xF0..0xFF)
        if ((header[0] & 0xFF) == 0xFF && (header[1] & 0xF0) == 0xF0) {
            return true;
        }
        return false;
    }

    private String sanitizeFolder(String folder) {
        if (folder == null || folder.isBlank()) {
            return "general";
        }
        String sanitized = folder.trim()
            .replaceAll("[^a-zA-Z0-9_/-]", "_")
            .replaceAll("/{2,}", "/")
            .replaceAll("^/+", "")
            .replaceAll("/+$", "");
        if (sanitized.contains("..") || sanitized.isBlank()) {
            return "general";
        }
        return sanitized;
    }

    private String sanitizeFilename(String filename) {
        if (filename == null || filename.isBlank()) {
            return "file";
        }
        String clean = filename.replaceAll("^.*[/\\\\]", "").replaceAll("\0", "");
        int lastDot = clean.lastIndexOf('.');
        String nameWithoutExt = lastDot > 0 ? clean.substring(0, lastDot) : clean;
        String sanitized = nameWithoutExt.replaceAll("[^a-zA-Z0-9._-]", "_");
        if (sanitized.isBlank()) {
            sanitized = "file";
        } else if (sanitized.length() > 50) {
            sanitized = sanitized.substring(0, 50);
        }
        return sanitized;
    }

    private String extractExtension(String filename) {
        if (filename == null || filename.isBlank()) {
            return "";
        }
        String clean = filename.replaceAll("^.*[/\\\\]", "").replaceAll("\0", "");
        int lastDot = clean.lastIndexOf('.');
        if (lastDot < 0 || lastDot == clean.length() - 1) {
            return "";
        }
        return clean.substring(lastDot + 1).toLowerCase(Locale.ROOT);
    }

    private String generateFileKey(String originalFilename, String folder, String targetExtension) {
        String safeFolder = sanitizeFolder(folder);
        String safeName = sanitizeFilename(originalFilename);
        String uniquePrefix = UUID.randomUUID().toString().replace("-", "").substring(0, 12);
        String safeExt = targetExtension.startsWith(".") ? targetExtension : "." + targetExtension;
        return "%s/%s_%s%s".formatted(safeFolder, uniquePrefix, safeName, safeExt);
    }

    private String buildPublicUrl(String fileKey) {
        String publicUrl = properties.getPublicUrl();
        if (publicUrl != null && !publicUrl.isBlank()) {
            return "%s/%s".formatted(publicUrl.replaceAll("/$", ""), fileKey);
        }
        return "%s/%s/%s".formatted(properties.getEndpoint().replaceAll("/$", ""), properties.getBucketName(), fileKey);
    }

    private void safeDelete(Path path) {
        if (path != null) {
            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                LOG.warn("Không thể xóa file tạm: {}", path, e);
            }
        }
    }
}
