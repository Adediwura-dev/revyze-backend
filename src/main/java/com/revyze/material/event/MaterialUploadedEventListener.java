package com.revyze.material.event;

import com.revyze.material.service.MaterialAnalysisService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class MaterialUploadedEventListener {

    private final MaterialAnalysisService materialAnalysisService;

    public MaterialUploadedEventListener(MaterialAnalysisService materialAnalysisService) {
        this.materialAnalysisService = materialAnalysisService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleMaterialUploaded(MaterialUploadedEvent event) {
        materialAnalysisService.analyzeMaterialAsync(event.materialId());
    }
}