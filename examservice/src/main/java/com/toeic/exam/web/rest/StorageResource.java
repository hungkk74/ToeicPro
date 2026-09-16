package com.toeic.exam.web.rest;

import com.toeic.exam.service.FileStorageService;
import com.toeic.exam.service.dto.FileUploadResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller quản lý upload và xóa file lưu trữ trên Cloudflare R2.
 */
@RestController
@RequestMapping("/api/storage")
public class StorageResource {

    private static final Logger LOG = LoggerFactory.getLogger(StorageResource.class);

    private final FileStorageService fileStorageService;

    public StorageResource(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    /**
     * POST /api/storage/upload/audio : Upload file âm thanh TOEIC.
     */
    @PostMapping(value = "/upload/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadAudio(@RequestParam("file") MultipartFile file) {
        LOG.debug("REST request to upload audio file: {}", file.getOriginalFilename());
        FileUploadResponse response = fileStorageService.uploadAudio(file);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/storage/upload/image : Upload hình ảnh đề thi TOEIC.
     */
    @PostMapping(value = "/upload/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadImage(@RequestParam("file") MultipartFile file) {
        LOG.debug("REST request to upload image file: {}", file.getOriginalFilename());
        FileUploadResponse response = fileStorageService.uploadImage(file);
        return ResponseEntity.ok(response);
    }

    /**
     * DELETE /api/storage/file : Xóa file theo fileKey.
     */
    @DeleteMapping("/file")
    public ResponseEntity<Void> deleteFile(@RequestParam("fileKey") String fileKey) {
        LOG.debug("REST request to delete file with key: {}", fileKey);
        fileStorageService.deleteFile(fileKey);
        return ResponseEntity.noContent().build();
    }
}
