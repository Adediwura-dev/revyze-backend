package com.revyze.assistant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.revyze.assistant.dto.ChatRequest;
import com.revyze.user.dto.RegisterRequest;
import com.revyze.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class AssistantEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();

        String uniqueEmail = "assistant-test-" + UUID.randomUUID() + "@revyze.com";
        RegisterRequest registerReq = new RegisterRequest("Assistant Tester", uniqueEmail, "ValidPass123!");

        MvcResult registerResult = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerReq)))
                .andExpect(status().isOk())
                .andReturn();

        jwtToken = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .path("data")
                .path("token")
                .asText();
    }

    @Test
    void shouldSendMessageAndRetrieveSessionHistory() throws Exception {
        ChatRequest chatReq = new ChatRequest(null, "How do base cases work in recursion?");

        MvcResult chatResult = mockMvc.perform(post("/api/v1/assistant/chat")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(chatReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.sessionId").isNotEmpty())
                .andExpect(jsonPath("$.data.userMessage.content").value("How do base cases work in recursion?"))
                .andExpect(jsonPath("$.data.assistantMessage.content").isNotEmpty())
                .andReturn();

        String sessionId = objectMapper.readTree(chatResult.getResponse().getContentAsString())
                .path("data")
                .path("sessionId")
                .asText();

        // Retrieve sessions
        mockMvc.perform(get("/api/v1/assistant/sessions")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.length()").value(1));

        // Retrieve messages for session
        mockMvc.perform(get("/api/v1/assistant/sessions/" + sessionId)
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.messages.length()").value(2));
    }
}

