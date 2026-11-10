package com.revyze.practice;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.revyze.practice.dto.QuizGenerateRequest;
import com.revyze.practice.dto.QuizSubmitRequest;
import com.revyze.user.dto.RegisterRequest;
import com.revyze.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PracticeEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String userToken;

    @BeforeEach
    void setupUser() throws Exception {
        userRepository.deleteAll();

        RegisterRequest req = new RegisterRequest("Bushra Quiz", "quiz.student@revyze.com", "Password123!");
        MvcResult res = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andReturn();

        JsonNode root = objectMapper.readTree(res.getResponse().getContentAsString());
        this.userToken = root.path("data").path("token").asText();
    }

    @Test
    @DisplayName("Generates quiz, retrieves questions, submits answers, and checks history")
    void verifyQuizFlow() throws Exception {
        QuizGenerateRequest genReq = new QuizGenerateRequest("Recursion", null, 3);
        MvcResult genRes = mockMvc.perform(post("/api/v1/practice/generate")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(genReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.topic").value("Recursion"))
                .andExpect(jsonPath("$.data.questions").isArray())
                .andReturn();

        JsonNode genJson = objectMapper.readTree(genRes.getResponse().getContentAsString());
        String quizId = genJson.path("data").path("id").asText();

        mockMvc.perform(get("/api/v1/practice/quizzes/" + quizId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(quizId));

        QuizSubmitRequest submitReq = new QuizSubmitRequest(
                Collections.singletonList(new QuizSubmitRequest.AnswerSubmission(0, "B"))
        );

        mockMvc.perform(post("/api/v1/practice/quizzes/" + quizId + "/submit")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(submitReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.quizId").value(quizId))
                .andExpect(jsonPath("$.data.results").isArray());

        mockMvc.perform(get("/api/v1/practice/history")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}

