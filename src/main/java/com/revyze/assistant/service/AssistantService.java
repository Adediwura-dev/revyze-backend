package com.revyze.assistant.service;

import com.revyze.activity.service.ActivityService;
import com.revyze.assistant.dto.*;
import com.revyze.assistant.entity.ChatMessage;
import com.revyze.assistant.entity.ChatSession;
import com.revyze.assistant.repository.ChatMessageRepository;
import com.revyze.assistant.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AssistantService {

    private final ChatSessionRepository chatSessionRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ActivityService activityService;

    public AssistantService(ChatSessionRepository chatSessionRepository,
                            ChatMessageRepository chatMessageRepository,
                            ActivityService activityService) {
        this.chatSessionRepository = chatSessionRepository;
        this.chatMessageRepository = chatMessageRepository;
        this.activityService = activityService;
    }

    @Transactional
    public ChatResponseDto sendMessage(UUID userId, ChatRequest request) {
        ChatSession session;
        if (request.getSessionId() != null) {
            session = chatSessionRepository.findByIdAndUserId(request.getSessionId(), userId)
                    .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));
        } else {
            String title = deriveTitle(request.getMessage());
            session = chatSessionRepository.save(new ChatSession(userId, title));
        }

        // Save User Message
        ChatMessage userMsg = chatMessageRepository.save(
                new ChatMessage(session.getId(), "USER", request.getMessage().trim())
        );

        // Generate Academic Assistant Response
        String responseContent = generateAcademicResponse(request.getMessage().trim());

        // Save Assistant Message
        ChatMessage assistantMsg = chatMessageRepository.save(
                new ChatMessage(session.getId(), "ASSISTANT", responseContent)
        );

        // Update session timestamp
        session.preUpdate();
        chatSessionRepository.save(session);

        // Log recent activity
        String preview = request.getMessage().length() > 40
                ? request.getMessage().substring(0, 40) + "..."
                : request.getMessage();
        activityService.logActivity(userId, "STUDY_SESSION", "AI Academic Assistant", "Consulted on: " + preview);

        return new ChatResponseDto(
                session.getId(),
                session.getTitle(),
                mapToDto(userMsg),
                mapToDto(assistantMsg)
        );
    }

    @Transactional(readOnly = true)
    public List<ChatSessionDto> getSessions(UUID userId) {
        return chatSessionRepository.findByUserIdOrderByUpdatedAtDesc(userId).stream()
                .map(s -> new ChatSessionDto(s.getId(), s.getTitle(), s.getCreatedAt(), s.getUpdatedAt(), null))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChatSessionDto getSessionWithMessages(UUID userId, UUID sessionId) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        List<ChatMessageDto> messages = chatMessageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());

        return new ChatSessionDto(session.getId(), session.getTitle(), session.getCreatedAt(), session.getUpdatedAt(), messages);
    }

    @Transactional
    public void deleteSession(UUID userId, UUID sessionId) {
        ChatSession session = chatSessionRepository.findByIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Chat session not found"));

        chatMessageRepository.deleteBySessionId(session.getId());
        chatSessionRepository.delete(session);
    }

    private String deriveTitle(String message) {
        String cleaned = message.replaceAll("\\s+", " ").trim();
        if (cleaned.length() <= 40) {
            return cleaned;
        }
        return cleaned.substring(0, 37) + "...";
    }

    private String generateAcademicResponse(String query) {
        String lower = query.toLowerCase(Locale.ROOT);

        if (lower.contains("recursion") || lower.contains("recursive") || lower.contains("base case")) {
            return "### Understanding Recursion & Call Stacks\n\n"
                    + "Recursion occurs when a function calls itself to solve a smaller instance of the same problem.\n\n"
                    + "**Essential Components:**\n"
                    + "1. **Base Case**: The termination condition where the function returns immediately without a recursive call, preventing infinite recursion.\n"
                    + "2. **Recursive Step**: Progresses the problem toward the base case by reducing input size.\n\n"
                    + "> **Pro Study Tip:** In memory, each recursive call allocates a new stack frame on the call stack. For deep recursion, consider tail call optimization or converting to an iterative stack.\n\n"
                    + "*Try taking a quick 5-question Recursion Practice Quiz in Revyze to test your mastery!*";
        }

        if (lower.contains("sql") || lower.contains("database") || lower.contains("acid") || lower.contains("index")) {
            return "### Database Systems & ACID Guarantees\n\n"
                    + "Relational database management systems (RDBMS) ensure reliable transactions through **ACID** properties:\n\n"
                    + "- **Atomicity**: The transaction executes completely or rolls back entirely (\"all-or-nothing\").\n"
                    + "- **Consistency**: Transitions the database from one valid state to another, satisfying all schema constraints.\n"
                    + "- **Isolation**: Concurrent transactions execute without cross-contamination.\n"
                    + "- **Durability**: Committed data survives system crashes and power failures.\n\n"
                    + "**Indexing Strategy**: B-Tree indexes provide logarithmic lookup time for equality and range queries, though they incur an overhead on write operations.";
        }

        if (lower.contains("tree") || lower.contains("data structure") || lower.contains("hash") || lower.contains("array")) {
            return "### Core Data Structures & Asymptotic Trade-offs\n\n"
                    + "Choosing the optimal data structure depends on the expected access patterns:\n\n"
                    + "- **Dynamic Arrays**: Fast random access in constant time, but arbitrary insertion/deletion is linear time.\n"
                    + "- **Hash Tables**: Average constant time search, insertion, and deletion, provided good hash distribution.\n"
                    + "- **Binary Search Trees (BST)**: In-order traversal produces sorted keys. Balanced trees guarantee logarithmic operations.\n\n"
                    + "Keep track of worst-case vs. amortized complexities when reviewing for your exams!";
        }

        if (lower.contains("exam") || lower.contains("plan") || lower.contains("schedule") || lower.contains("study tips")) {
            return "### Revyze Active Recall & Spaced Repetition Strategy\n\n"
                    + "To maximize your exam performance and long-term memory retention:\n\n"
                    + "1. **Spaced Repetition**: Review challenging topics at intervals of 1, 3, and 7 days.\n"
                    + "2. **Active Retrieval**: Instead of passive re-reading, generate a practice quiz in the Practice tab.\n"
                    + "3. **Interleaving**: Mix related topics (e.g., Data Structures with Database Indexes) during a single 45-minute revision block.\n\n"
                    + "Would you like me to quiz you on a specific topic or explain any concept from your uploaded study materials?";
        }

        return "### Concept Overview & Revision Guidance\n\n"
                + "Here is an academic breakdown regarding **\"" + query + "\"**:\n\n"
                + "1. **Definition & Fundamentals**: Clarify the core principles and how they relate to your existing coursework.\n"
                + "2. **Key Problem Patterns**: Identify common problem formulations and standard solutions.\n"
                + "3. **Actionable Step**: Review your uploaded study notes in **My Materials**, then test your active recall with a customized practice quiz.\n\n"
                + "Feel free to ask a follow-up question or request a step-by-step example!";
    }

    private ChatMessageDto mapToDto(ChatMessage msg) {
        return new ChatMessageDto(
                msg.getId(),
                msg.getSessionId(),
                msg.getSender(),
                msg.getContent(),
                msg.getCreatedAt()
        );
    }
}
