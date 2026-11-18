
package com.revyze.material.service;

import org.apache.tika.Tika;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class MaterialTextExtractionService {

    private final Tika tika;

    public MaterialTextExtractionService() {
        this.tika = new Tika();
    }

    public String extractText(Resource resource) {
        try (InputStream inputStream = resource.getInputStream()) {

            String extractedText = tika.parseToString(inputStream);

            if (extractedText == null || extractedText.isBlank()) {
                throw new IllegalArgumentException(
                        "No readable text found in the uploaded document."
                );
            }

            return extractedText.trim();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to extract text from the uploaded document.",
                    e
            );
        }
    }
}
