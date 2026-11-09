package com.revyze.material;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revyze.user.dto.RegisterRequest;
import com.revyze.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class StudyMaterialEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String userAuthToken;

    @BeforeEach
    void setupUserToken() throws Exception {
        userRepository.deleteAll();

        RegisterRequest registerReq = new RegisterRequest("Bushra Student", "materials.tester@revyze.com", "SecretPass123!");
        MvcResult res = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode jsonNode = objectMapper.readTree(res.getResponse().getContentAsString());
        this.userAuthToken = jsonNode.path("data").path("token").asText();
    }

    @Test
    @DisplayName("Uploads PDF file, verifies presence in list, downloads, and deletes")
    void verifyFullMaterialLifecycle() throws Exception {
        MockMultipartFile mockPdf = new MockMultipartFile(
                "file",
                "lecture_slides.pdf",
                MediaType.APPLICATION_PDF_VALUE,
                "Sample PDF content for revision".getBytes()
        );

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/materials/upload")
                        .file(mockPdf)
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.originalFilename").value("lecture_slides.pdf"))
                .andReturn();

        JsonNode uploadJson = objectMapper.readTree(uploadResult.getResponse().getContentAsString());
        String materialId = uploadJson.path("data").path("id").asText();

        mockMvc.perform(get("/api/v1/materials")
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0].id").value(materialId));

        mockMvc.perform(get("/api/v1/materials/" + materialId + "/download")
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"lecture_slides.pdf\""));

        mockMvc.perform(delete("/api/v1/materials/" + materialId)
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/materials")
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isEmpty());
    }

    @Test
    @DisplayName("Rejects unsupported file type (.exe)")
    void shouldRejectUnsupportedFileExtension() throws Exception {
        MockMultipartFile badFile = new MockMultipartFile(
                "file",
                "dangerous_script.exe",
                "application/octet-stream",
                "malicious executable bytes".getBytes()
        );

        mockMvc.perform(multipart("/api/v1/materials/upload")
                        .file(badFile)
                        .header("Authorization", "Bearer " + userAuthToken))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("Rejects unauthenticated material requests with 401")
    void shouldRejectUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/v1/materials"))
                .andExpect(status().isUnauthorized());
    }
}

