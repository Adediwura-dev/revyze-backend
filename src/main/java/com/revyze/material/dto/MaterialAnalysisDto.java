
package com.revyze.material.dto;

import com.revyze.material.entity.MaterialAnalysis;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class MaterialAnalysisDto {

    private UUID id;
    private UUID materialId;
    private String summary;
    private List<String> keyConcepts;
    private List<String> importantPoints;
    private List<MaterialAnalysis.Definition> definitions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MaterialAnalysisDto() {
    }

    public MaterialAnalysisDto(
            UUID id,
            UUID materialId,
            String summary,
            List<String> keyConcepts,
            List<String> importantPoints,
            List<MaterialAnalysis.Definition> definitions,
            LocalDateTime createdAt,
            LocalDateTime updatedAt) {

        this.id = id;
        this.materialId = materialId;
        this.summary = summary;
        this.keyConcepts = keyConcepts;
        this.importantPoints = importantPoints;
        this.definitions = definitions;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static MaterialAnalysisDto fromEntity(MaterialAnalysis entity) {
        return new MaterialAnalysisDto(
                entity.getId(),
                entity.getStudyMaterial().getId(),
                entity.getSummary(),
                entity.getKeyConcepts(),
                entity.getImportantPoints(),
                entity.getDefinitions(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public UUID getMaterialId() {
        return materialId;
    }

    public String getSummary() {
        return summary;
    }

    public List<String> getKeyConcepts() {
        return keyConcepts;
    }

    public List<String> getImportantPoints() {
        return importantPoints;
    }

    public List<MaterialAnalysis.Definition> getDefinitions() {
        return definitions;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
