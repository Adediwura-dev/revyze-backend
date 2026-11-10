package com.revyze.material.service;

import com.revyze.activity.service.ActivityService;
import com.revyze.material.dto.StudyMaterialDto;
import com.revyze.material.entity.StudyMaterial;
import com.revyze.material.repository.StudyMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class StudyMaterialService {

    private final StudyMaterialRepository materialRepository;
    private final FileStorageService fileStorageService;
    private final ActivityService activityService;

    @Autowired
    public StudyMaterialService(StudyMaterialRepository materialRepository,
                                FileStorageService fileStorageService,
                                ActivityService activityService) {
        this.materialRepository = materialRepository;
        this.fileStorageService = fileStorageService;
        this.activityService = activityService;
    }

    @Transactional
    public StudyMaterialDto uploadMaterial(UUID userId, MultipartFile file) {
        String storedFilename = fileStorageService.store(file);

        String originalFilename = file.getOriginalFilename() != null ? file.getOriginalFilename() : "document";
        String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";
        long fileSize = file.getSize();

        StudyMaterial material = new StudyMaterial(
                userId,
                originalFilename,
                storedFilename,
                contentType,
                fileSize
        );

        StudyMaterial saved = materialRepository.save(material);

        activityService.logActivity(
                userId,
                "MATERIAL_UPLOAD",
                "Uploaded " + originalFilename,
                "Added new study material to study space"
        );

        return StudyMaterialDto.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<StudyMaterialDto> getUserMaterials(UUID userId) {
        return materialRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(StudyMaterialDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public StudyMaterial getMaterialEntity(UUID userId, UUID materialId) {
        return materialRepository.findByIdAndUserId(materialId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found with ID: " + materialId));
    }

    public Resource loadMaterialFile(StudyMaterial material) {
        return fileStorageService.loadAsResource(material.getStoredFilename());
    }

    @Transactional
    public void deleteMaterial(UUID userId, UUID materialId) {
        StudyMaterial material = materialRepository.findByIdAndUserId(materialId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Study material not found with ID: " + materialId));

        fileStorageService.delete(material.getStoredFilename());
        materialRepository.delete(material);
    }
}

