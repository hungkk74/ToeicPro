package com.toeic.exam.service.dto;

import java.io.Serializable;

/**
 * Payload phản hồi kết quả sau khi upload file lên Cloudflare R2.
 */
public record FileUploadResponse(
    String fileKey,
    String fileUrl,
    String originalFileName,
    long fileSize,
    String contentType
) implements Serializable {}
