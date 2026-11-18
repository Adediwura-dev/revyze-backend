package com.revyze.material.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revyze.assistant.service.GeminiService;
import com.revyze.material.dto.MaterialAnalysisDto;
import com.revyze.material.entity.MaterialAnalysis;
import com.revyze.material.entity.StudyMaterial;
import com.revyze.material.repository.MaterialAnalysisRepository;
import com.revyze.material.repository.StudyMaterialRepository;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MaterialAnalysisService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final MaterialAnalysisRepository materialAnalysisRepository;
    private final FileStorageService fileStorageService;
    private final MaterialTextExtractionService textExtractionService;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;

    public MaterialAnalysisService(
            StudyMaterialRepository studyMaterialRepository,
            MaterialAnalysisRepository materialAnalysisRepository,
            FileStorageService fileStorageService,
            MaterialTextExtractionService textExtractionService,
            GeminiService geminiService,
            ObjectMapper objectMapper) {

        this.studyMaterialRepository = studyMaterialRepository;
        this.materialAnalysisRepository = materialAnalysisRepository;
        this.fileStorageService = fileStorageService;
        this.textExtractionService = textExtractionService;
        this.geminiService = geminiService;
        this.objectMapper = objectMapper;
    }

    

@Async
public void analyzeMaterialAsync(UUID materialId) {

    StudyMaterial material = studyMaterialRepository.findById(materialId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Study material not found: " + materialId
            ));

    try {
        // 1. Give analysis status
        updateAnalysisStatus(materialId, "PROCESSING");

        // 2. Load document
        Resource file = fileStorageService.loadAsResource(
                material.getStoredFilename()
        );

        // 3. Text being extracted from the document
        String extractedText = textExtractionService.extractText(file);

        // 4. Send text to Gemini
        String analysisJson = geminiService.generateMaterialAnalysis(
                extractedText
        );

        // 5. Parse Gemini response
        GeminiAnalysisResponse response = objectMapper.readValue(
                analysisJson,
                GeminiAnalysisResponse.class
        );

        // 6. Create analysis entity
        MaterialAnalysis analysis = materialAnalysisRepository
        .findByStudyMaterialId(material.getId())
        .orElseGet(MaterialAnalysis::new);

        analysis.setStudyMaterial(material);
        analysis.setSummary(response.summary());
        analysis.setKeyConcepts(response.keyConcepts());
        analysis.setImportantPoints(response.importantPoints());
        analysis.setDefinitions(response.definitions());

        // 7. Save analysis
        materialAnalysisRepository.save(analysis);

        // 8. Mark as completed
        updateAnalysisStatus(materialId, "COMPLETED");

    } catch (Exception e) {
        updateAnalysisStatus(materialId, "FAILED");

        System.err.println(
                "Material analysis failed for " + materialId + ": " + e.getMessage()
        );
    }
}

    @Transactional
    public void updateAnalysisStatus(UUID materialId, String status) {
        try {
            studyMaterialRepository.findById(materialId).ifPresent(mat -> {
                mat.setAnalysisStatus(status);
                studyMaterialRepository.save(mat);
            });
        } catch (Exception ex) {
            System.err.println("Failed to update analysis status to " + status + " for " + materialId + ": " + ex.getMessage());
        }
    }

@Transactional(readOnly = true)
public MaterialAnalysisDto getMaterialAnalysis(UUID userId, UUID materialId) {

    StudyMaterial material = studyMaterialRepository
            .findByIdAndUserId(materialId, userId)
            .orElseThrow(() -> new IllegalArgumentException(
                    "Study material not found"
            ));

    MaterialAnalysis analysis = materialAnalysisRepository
            .findByStudyMaterialId(material.getId())
            .orElseThrow(() -> new IllegalArgumentException(
                    "Analysis is not available yet"
            ));

    return MaterialAnalysisDto.fromEntity(analysis);
}

    private record GeminiAnalysisResponse(
        String summary,
        List<String> keyConcepts,
        List<String> importantPoints,
        List<MaterialAnalysis.Definition> definitions
) {}
}
