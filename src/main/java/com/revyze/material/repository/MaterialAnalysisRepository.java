package com.revyze.material.repository;

import com.revyze.material.entity.MaterialAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface MaterialAnalysisRepository
        extends JpaRepository<MaterialAnalysis, UUID> {

    Optional<MaterialAnalysis> findByStudyMaterialId(UUID studyMaterialId);
}
