package com.revyze.material.controller;

import com.revyze.common.response.ApiResponse;
import com.revyze.material.dto.StudyMaterialDto;
import com.revyze.material.entity.StudyMaterial;
import com.revyze.material.service.StudyMaterialService;
import com.revyze.security.UserPrincipal;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/materials")
public class StudyMaterialController {

    private final StudyMaterialService studyMaterialService;

    public StudyMaterialController(StudyMaterialService studyMaterialService) {
        this.studyMaterialService = studyMaterialService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<StudyMaterialDto>> upload(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @RequestParam("file") MultipartFile file) {
        StudyMaterialDto savedMaterial = studyMaterialService.uploadMaterial(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.ok("Uploaded successfully", savedMaterial));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StudyMaterialDto>>> list(
            @AuthenticationPrincipal UserPrincipal currentUser) {
        List<StudyMaterialDto> list = studyMaterialService.getUserMaterials(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.ok(list));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<Resource> download(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable("id") UUID id) {
        StudyMaterial entity = studyMaterialService.getMaterialEntity(currentUser.getId(), id);
        Resource fileResource = studyMaterialService.loadMaterialFile(entity);

        String mediaType = entity.getContentType() != null ? entity.getContentType() : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        String headerValue = "attachment; filename=\"" + entity.getOriginalFilename() + "\"";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(mediaType))
                .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                .body(fileResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> remove(
            @AuthenticationPrincipal UserPrincipal currentUser,
            @PathVariable("id") UUID id) {
        studyMaterialService.deleteMaterial(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Material deleted", null));
    }
}
