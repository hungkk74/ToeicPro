package com.toeic.exam.service;

import com.toeic.exam.config.CloudflareR2Properties;
import com.toeic.exam.service.dto.FileUploadResponse;
import java.io.IOException;
import java.util.UUID;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import ws.schild.jave.Encoder;
import ws.schild.jave.MultimediaObject;
import ws.schild.jave.encode.AudioAttributes;
import ws.schild.jave.encode.EncodingAttributes;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
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

        File tempInput = null;
        File tempOutput = null;

        try {
            // 1. Tạo file tạm để chứa âm thanh gốc
            tempInput = File.createTempFile("audio_input_", ".tmp");
            Files.copy(file.getInputStream(), tempInput.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // 2. Tạo cấu hình mã hoá Audio (MP3, 64kbps, Mono)
            AudioAttributes audio = new AudioAttributes();
            audio.setCodec("libmp3lame");
            audio.setBitRate(64000);
            audio.setChannels(1); // Mono
            audio.setSamplingRate(44100);

            EncodingAttributes attrs = new EncodingAttributes();
            attrs.setOutputFormat("mp3");
            attrs.setAudioAttributes(audio);

            // 3. Tiến hành encode bằng JAVE2
            tempOutput = File.createTempFile("audio_output_", ".mp3");
            Encoder encoder = new Encoder();
            encoder.encode(new MultimediaObject(tempInput), tempOutput, attrs);

            // 4. Upload file đã nén lên R2
            String fileKey = generateFileKey(file.getOriginalFilename(), "audio", ".mp3");
            String contentType = "audio/mpeg";
            long optimizedSize = tempOutput.length();

            LOG.info("Uploading optimized MP3 to Cloudflare R2 - Bucket: {}, Key: {}, Original Size: {} bytes, Optimized Size: {} bytes",
                properties.getBucketName(), fileKey, file.getSize(), optimizedSize);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

            s3Client.putObject(putRequest, RequestBody.fromFile(tempOutput));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Optimized Audio uploaded successfully to R2: {}", fileUrl);

            String originalFilename = file.getOriginalFilename();
            return new FileUploadResponse(fileKey, fileUrl, originalFilename, optimizedSize, contentType);

        } catch (Exception e) {
            LOG.error("Failed to optimize and upload audio to Cloudflare R2", e);
            throw new RuntimeException("Lỗi khi tối ưu và upload audio: " + e.getMessage(), e);
        } finally {
            // 5. Dọn dẹp file tạm
            if (tempInput != null && tempInput.exists()) {
                tempInput.delete();
            }
            if (tempOutput != null && tempOutput.exists()) {
                tempOutput.delete();
            }
        }
    }

    /**
     * Upload hình ảnh câu hỏi TOEIC (PNG, JPG, WEBP,...) lên thư mục images/
     *
     * @param file file hình ảnh từ client
     * @return FileUploadResponse chứa URL và key
     */
    public FileUploadResponse uploadImage(MultipartFile file) {
        validateFile(file, "image");
        
        try {
            // 1. Đọc ảnh vào bộ nhớ
            ImmutableImage image = ImmutableImage.loader().fromStream(file.getInputStream());
            
            // 2. Resize nếu ảnh quá lớn (width > 1200)
            if (image.awt().getWidth() > 1200) {
                image = image.scaleToWidth(1200);
            }
            
            // 3. Convert to WebP với chất lượng 75%
            byte[] webpBytes = image.bytes(WebpWriter.DEFAULT.withQ(75));
            
            // 4. Khởi tạo metadata cho R2
            String fileKey = generateFileKey(file.getOriginalFilename(), "images", ".webp");
            String contentType = "image/webp";
            
            LOG.info("Uploading optimized WebP to Cloudflare R2 - Bucket: {}, Key: {}, Original Size: {} bytes, Optimized Size: {} bytes",
                properties.getBucketName(), fileKey, file.getSize(), webpBytes.length);

            PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(properties.getBucketName())
                .key(fileKey)
                .contentType(contentType)
                .build();

            // 5. Upload mảng byte WebP
            s3Client.putObject(putRequest, RequestBody.fromBytes(webpBytes));

            String fileUrl = buildPublicUrl(fileKey);
            LOG.info("Optimized Image uploaded successfully to R2: {}", fileUrl);
            
            String originalFilename = file.getOriginalFilename();
            return new FileUploadResponse(fileKey, fileUrl, originalFilename, webpBytes.length, contentType);

        } catch (Exception e) {
            LOG.error("Failed to optimize and upload image to Cloudflare R2", e);
            throw new RuntimeException("Lỗi khi tối ưu và upload ảnh: " + e.getMessage(), e);
        }
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

    private String generateFileKey(String originalFilename, String folder, String targetExtension) {
        if (originalFilename != null && !originalFilename.isBlank()) {
            int lastDot = originalFilename.lastIndexOf('.');
            String nameWithoutExt = lastDot > 0 ? originalFilename.substring(0, lastDot) : originalFilename;
            
            // Chỉ đổi dấu cách thành dấu gạch dưới để link không bị lỗi khoảng trắng, còn lại giữ nguyên tên gốc
            String sanitized = nameWithoutExt.replaceAll("\\s+", "_");
            
            return "%s/%s%s".formatted(folder, sanitized, targetExtension);
        }
        return "%s/%s%s".formatted(folder, UUID.randomUUID().toString(), targetExtension);
    }
}
