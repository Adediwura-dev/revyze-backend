package com.revyze.material.dto;

import com.revyze.material.entity.StudyMaterial;

import java.time.Instant;
import java.util.UUID;

public class StudyMaterialDto {

    private UUID id;
    private String originalFilename;
    private String contentType;
    private Long fileSize;
    private String status;
    private Instant createdAt;
    private String downloadUrl;

    public StudyMaterialDto() {
    }

    public StudyMaterialDto(UUID id, String originalFilename, String contentType, Long fileSize,
                            String status, Instant createdAt, String downloadUrl) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.status = status;
        this.createdAt = createdAt;
        this.downloadUrl = downloadUrl;
    }

    public static StudyMaterialDto fromEntity(StudyMaterial entity) {
        String downloadUrl = "/api/v1/materials/" + entity.getId() + "/download";
        return new StudyMaterialDto(
                entity.getId(),
                entity.getOriginalFilename(),
                entity.getContentType(),
                entity.getFileSize(),
                entity.getStatus(),
                entity.getCreatedAt(),
                downloadUrl
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
}

