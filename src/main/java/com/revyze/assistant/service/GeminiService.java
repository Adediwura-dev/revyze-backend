package com.revyze.assistant.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.revyze.assistant.entity.ChatMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GeminiService {

    private static final Logger log = LoggerFactory.getLogger(GeminiService.class);

    private final String apiKey;
    private final String model;
    private final RestClient restClient;

    public GeminiService(
            @Value("${app.gemini.api-key:${GEMINI_API_KEY:}}") String apiKey,
            @Value("${app.gemini.model:${GEMINI_MODEL:gemini-3.1-flash-lite}}") String model) {
        this.apiKey = apiKey != null ? apiKey.trim() : "";
        this.model = normalizeModel(model);
        this.restClient = RestClient.builder().build();
        log.info("GeminiService initialized with model: {}, configured: {}", this.model, isConfigured());
    }

    private String normalizeModel(String rawModel) {
        if (rawModel == null || rawModel.isBlank()) {
            return "gemini-3.1-flash-lite";
        }
        String clean = rawModel.trim().toLowerCase().replaceAll("\\s+", "-");
        if (clean.equals("gemini-3.1-flash") || clean.equals("gemini-3-flash")) {
            return "gemini-3.1-flash-lite";
        }
        return clean;
    }

    public boolean isConfigured() {
        return !apiKey.isBlank();
    }

    public String generateResponse(List<ChatMessage> history, String userMessage) {
        if (!isConfigured()) {
            log.warn("Gemini API key is not configured. Falling back to default guidance.");
            return "### Gemini API Key Required\n\n"
                    + "Please configure your `GEMINI_API_KEY` in `.env` to enable live Gemini AI tutoring responses.";
        }

        try {
            GeminiRequest requestPayload = buildRequestPayload(history, userMessage);
            String url = String.format("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s", model, apiKey);

            GeminiResponse response = restClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestPayload)
                    .retrieve()
                    .body(GeminiResponse.class);

            if (response != null && response.getCandidates() != null && !response.getCandidates().isEmpty()) {
                GeminiResponse.Candidate candidate = response.getCandidates().get(0);
                if (candidate.getContent() != null && candidate.getContent().getParts() != null) {
                    StringBuilder sb = new StringBuilder();
                    for (GeminiResponse.Part part : candidate.getContent().getParts()) {
                        if (part.getText() != null) {
                            sb.append(part.getText());
                        }
                    }
                    if (sb.length() > 0) {
                        return sb.toString().trim();
                    }
                }
            }

            return "I understood your question, but could not generate a response. Please try rephrasing.";
        } catch (Exception ex) {
            log.error("Failed to call Gemini API with model {}: {}", model, ex.getMessage());

            // If a custom model failed (such as unreleased name 404), fallback to gemini-2.5-flash
            if (!model.equals("gemini-2.5-flash") && !model.equals("gemini-1.5-flash")) {
                try {
                    log.info("Attempting fallback call to gemini-2.5-flash...");
                    GeminiRequest requestPayload = buildRequestPayload(history, userMessage);
                    String fallbackUrl = String.format("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=%s", apiKey);
                    GeminiResponse fallbackResponse = restClient.post()
                            .uri(fallbackUrl)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(requestPayload)
                            .retrieve()
                            .body(GeminiResponse.class);

                    if (fallbackResponse != null && fallbackResponse.getCandidates() != null && !fallbackResponse.getCandidates().isEmpty()) {
                        StringBuilder sb = new StringBuilder();
                        for (GeminiResponse.Part part : fallbackResponse.getCandidates().get(0).getContent().getParts()) {
                            if (part.getText() != null) sb.append(part.getText());
                        }
                        if (sb.length() > 0) return sb.toString().trim();
                    }
                } catch (Exception fallbackEx) {
                    log.error("Fallback Gemini call also failed: {}", fallbackEx.getMessage());
                }
            }

            return "### AI Academic Assistant Notice\n\n"
                    + "Unable to contact the AI model (" + model + "): " + ex.getMessage() + "\n\n"
                    + "Please verify that your Gemini API key is valid and has access to Gemini API.";
        }
    }


public String generateMaterialAnalysis(String extractedText) {

    if (!isConfigured()) {
        throw new IllegalStateException(
                "Gemini API key is not configured."
        );
    }

    if (extractedText == null || extractedText.isBlank()) {
        throw new IllegalArgumentException(
                "Document text cannot be empty."
        );
    }

    String prompt = """
            Analyse the following university study material.

            Return ONLY valid JSON with exactly these fields:

            {
              "summary": "A clear summary of the material",
              "keyConcepts": ["Concept 1", "Concept 2"],
              "importantPoints": ["Point 1", "Point 2"],
              "definitions": [
                {
                  "term": "Term",
                  "meaning": "Definition"
                }
              ]
            }

            Instructions:
            1. Base your analysis strictly on the supplied material.
            2. Explain concepts clearly for university students.
            3. Include the most important information.
            4. Do not invent facts or definitions.
            5. If a section has no relevant information, return an empty array.
            6. Do not include Markdown or code fences.

            STUDY MATERIAL:

            """ + extractedText;

    GeminiRequest request = new GeminiRequest(
            new GeminiRequest.SystemInstruction(
                    Collections.singletonList(
                            new GeminiRequest.Part(
                                    "You are Revyze AI, an academic study material analyst. "
                                    + "Produce accurate, structured analysis."
                            )
                    )
            ),
            Collections.singletonList(
                    new GeminiRequest.Content(
                            "user",
                            Collections.singletonList(
                                    new GeminiRequest.Part(prompt)
                            )
                    )
            )
    );

    request.setGenerationConfig(
            new GeminiRequest.GenerationConfig("application/json")
    );

    try {
        return callGemini(model, request);

    } catch (Exception ex) {
        log.error("Gemini material analysis failed with model {}: {}",
                model, ex.getMessage());

        if (model.equals("gemini-2.5-flash")) {
            throw new RuntimeException(
                    "Unable to analyse study material.", ex
            );
        }

        try {
            log.info("Retrying material analysis with gemini-2.5-flash");

            return callGemini("gemini-2.5-flash", request);

        } catch (Exception fallbackEx) {
            log.error("Material analysis fallback failed: {}",
                    fallbackEx.getMessage());

            throw new RuntimeException(
                    "Unable to analyse study material.", fallbackEx
            );
        }
    }
}

private String callGemini(String modelName, GeminiRequest request) {

    String url = String.format(
            "https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent?key=%s",
            modelName,
            apiKey
    );

    GeminiResponse response = restClient.post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(GeminiResponse.class);

    if (response == null
            || response.getCandidates() == null
            || response.getCandidates().isEmpty()) {
        throw new RuntimeException("Gemini returned an empty response.");
    }

    StringBuilder result = new StringBuilder();

    GeminiResponse.Candidate candidate = response.getCandidates().get(0);

    if (candidate.getContent() != null
            && candidate.getContent().getParts() != null) {

        for (GeminiResponse.Part part : candidate.getContent().getParts()) {
            if (part.getText() != null) {
                result.append(part.getText());
            }
        }
    }

    if (result.isEmpty()) {
        throw new RuntimeException("Gemini returned no analysis text.");
    }

    return result.toString().trim();
}

    private GeminiRequest buildRequestPayload(List<ChatMessage> history, String userMessage) {
        String systemPrompt = "You are Revyze AI, an expert, encouraging university-level academic tutor. "
                + "Answer the student's exact question accurately and directly. "
                + "Provide clear step-by-step explanations, active recall tips, code snippets or diagrams where relevant, "
                + "and use structured GitHub-flavored markdown with bullet points and bold headers. "
                + "Do not deflect or output generic stock replies; focus entirely on the subject of the student's prompt.";

        GeminiRequest.SystemInstruction systemInstruction = new GeminiRequest.SystemInstruction(
                Collections.singletonList(new GeminiRequest.Part(systemPrompt))
        );

        List<GeminiRequest.Content> contents = new ArrayList<>();

        if (history != null) {
            for (ChatMessage msg : history) {
                String role = "USER".equalsIgnoreCase(msg.getSender()) ? "user" : "model";
                contents.add(new GeminiRequest.Content(
                        role,
                        Collections.singletonList(new GeminiRequest.Part(msg.getContent()))
                ));
            }
        }

        contents.add(new GeminiRequest.Content(
                "user",
                Collections.singletonList(new GeminiRequest.Part(userMessage))
        ));

        return new GeminiRequest(systemInstruction, contents);
    }

    // DTO Classes for Gemini API JSON
    public static class GeminiRequest {
        @JsonProperty("systemInstruction")
        private SystemInstruction systemInstruction;
        private List<Content> contents;
        private GenerationConfig generationConfig;

        public GenerationConfig getGenerationConfig() {
    return generationConfig;
}

    public void setGenerationConfig(GenerationConfig generationConfig) {
    this.generationConfig = generationConfig;
}

    public static class GenerationConfig {

    @JsonProperty("responseMimeType")
    private String responseMimeType;

    public GenerationConfig() {}

    public GenerationConfig(String responseMimeType) {
        this.responseMimeType = responseMimeType;
    }

    public String getResponseMimeType() {
        return responseMimeType;
    }

    public void setResponseMimeType(String responseMimeType) {
        this.responseMimeType = responseMimeType;
    }
}

        public GeminiRequest() {}

        public GeminiRequest(SystemInstruction systemInstruction, List<Content> contents) {
            this.systemInstruction = systemInstruction;
            this.contents = contents;
        }

        public SystemInstruction getSystemInstruction() { return systemInstruction; }
        public void setSystemInstruction(SystemInstruction systemInstruction) { this.systemInstruction = systemInstruction; }
        public List<Content> getContents() { return contents; }
        public void setContents(List<Content> contents) { this.contents = contents; }

        public static class SystemInstruction {
            private List<Part> parts;
            public SystemInstruction() {}
            public SystemInstruction(List<Part> parts) { this.parts = parts; }
            public List<Part> getParts() { return parts; }
            public void setParts(List<Part> parts) { this.parts = parts; }
        }

        public static class Content {
            private String role;
            private List<Part> parts;
            public Content() {}
            public Content(String role, List<Part> parts) {
                this.role = role;
                this.parts = parts;
            }
            public String getRole() { return role; }
            public void setRole(String role) { this.role = role; }
            public List<Part> getParts() { return parts; }
            public void setParts(List<Part> parts) { this.parts = parts; }
        }

        public static class Part {
            private String text;
            public Part() {}
            public Part(String text) { this.text = text; }
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GeminiResponse {
        private List<Candidate> candidates;

        public List<Candidate> getCandidates() { return candidates; }
        public void setCandidates(List<Candidate> candidates) { this.candidates = candidates; }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Candidate {
            private Content content;
            public Content getContent() { return content; }
            public void setContent(Content content) { this.content = content; }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Content {
            private List<Part> parts;
            public List<Part> getParts() { return parts; }
            public void setParts(List<Part> parts) { this.parts = parts; }
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Part {
            private String text;
            public String getText() { return text; }
            public void setText(String text) { this.text = text; }
        }
    }
}
