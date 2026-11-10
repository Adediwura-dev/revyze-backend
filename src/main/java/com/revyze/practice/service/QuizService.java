package com.revyze.practice.service;

import com.revyze.activity.service.ActivityService;
import com.revyze.course.entity.TopicMastery;
import com.revyze.course.repository.TopicMasteryRepository;
import com.revyze.course.repository.TopicRepository;
import com.revyze.practice.dto.*;
import com.revyze.practice.entity.Quiz;
import com.revyze.practice.entity.QuizAttempt;
import com.revyze.practice.entity.QuizQuestion;
import com.revyze.practice.repository.QuizAttemptRepository;
import com.revyze.practice.repository.QuizQuestionRepository;
import com.revyze.practice.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizQuestionRepository questionRepository;
    private final QuizAttemptRepository attemptRepository;
    private final QuizGenerationService generationService;
    private final ActivityService activityService;
    private final TopicMasteryRepository masteryRepository;
    private final TopicRepository topicRepository;

    @Autowired
    public QuizService(
            QuizRepository quizRepository,
            QuizQuestionRepository questionRepository,
            QuizAttemptRepository attemptRepository,
            QuizGenerationService generationService,
            ActivityService activityService,
            TopicMasteryRepository masteryRepository,
            TopicRepository topicRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.attemptRepository = attemptRepository;
        this.generationService = generationService;
        this.activityService = activityService;
        this.masteryRepository = masteryRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional
    public QuizDto generateQuiz(UUID userId, QuizGenerateRequest request) {
        String topic = request.getTopic().trim();
        String title = "Smart Practice: " + topic;

        Quiz quiz = new Quiz(userId, title, topic);
        Quiz savedQuiz = quizRepository.save(quiz);

        List<QuizQuestion> questions = generationService.generateQuestions(
                savedQuiz.getId(),
                topic,
                request.getQuestionCount()
        );
        questionRepository.saveAll(questions);

        List<QuizQuestionDto> questionDtos = questions.stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());

        return QuizDto.fromEntity(savedQuiz, questionDtos);
    }

    @Transactional(readOnly = true)
    public QuizDto getQuiz(UUID userId, UUID quizId) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + quizId));

        List<QuizQuestionDto> questionDtos = questionRepository.findByQuizIdOrderByQuestionIndexAsc(quizId)
                .stream()
                .map(QuizQuestionDto::fromEntity)
                .collect(Collectors.toList());

        return QuizDto.fromEntity(quiz, questionDtos);
    }

    @Transactional
    public QuizResultDto submitQuiz(UUID userId, UUID quizId, QuizSubmitRequest request) {
        Quiz quiz = quizRepository.findByIdAndUserId(quizId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found with ID: " + quizId));

        List<QuizQuestion> questions = questionRepository.findByQuizIdOrderByQuestionIndexAsc(quizId);
        if (questions.isEmpty()) {
            throw new IllegalArgumentException("Quiz has no questions");
        }

        Map<Integer, String> userAnswers = new HashMap<>();
        if (request != null && request.getAnswers() != null) {
            for (QuizSubmitRequest.AnswerSubmission sub : request.getAnswers()) {
                userAnswers.put(sub.getQuestionIndex(), sub.getSelectedOption());
            }
        }

        int score = 0;
        List<QuestionResultDto> resultList = new ArrayList<>();

        for (QuizQuestion q : questions) {
            String selected = userAnswers.get(q.getQuestionIndex());
            boolean isCorrect = selected != null && selected.equalsIgnoreCase(q.getCorrectOption());
            if (isCorrect) {
                score++;
            }

            List<String> options = Arrays.asList(q.getOptionA(), q.getOptionB(), q.getOptionC(), q.getOptionD());
            resultList.add(new QuestionResultDto(
                    q.getQuestionIndex(),
                    q.getQuestionText(),
                    options,
                    selected,
                    q.getCorrectOption(),
                    isCorrect,
                    q.getExplanation()
            ));
        }

        int totalQuestions = questions.size();
        int percentage = Math.round(((float) score / totalQuestions) * 100);

        QuizAttempt attempt = new QuizAttempt(quizId, userId, score, totalQuestions, percentage);
        QuizAttempt savedAttempt = attemptRepository.save(attempt);

        // Update Topic Mastery
        updateMastery(userId, quiz.getTopic(), percentage);

        // Log recent activity
        activityService.logActivity(
                userId,
                "QUIZ_COMPLETED",
                "Completed " + quiz.getTitle(),
                "Scored " + score + " / " + totalQuestions + " (" + percentage + "%)"
        );

        String feedback;
        if (percentage >= 80) {
            feedback = "Excellent mastery! You have a solid grasp of this topic.";
        } else if (percentage >= 60) {
            feedback = "Good effort! A quick review of the explanations will solidify your understanding.";
        } else {
            feedback = "More practice recommended. Focus on the explanations to reinforce key concepts.";
        }

        return new QuizResultDto(
                savedAttempt.getId(),
                quizId,
                score,
                totalQuestions,
                percentage,
                feedback,
                resultList
        );
    }

    private void updateMastery(UUID userId, String topicName, int quizPercentage) {
        topicRepository.findAll().stream()
                .filter(t -> t.getTitle().equalsIgnoreCase(topicName) || topicName.toLowerCase().contains(t.getTitle().toLowerCase()))
                .findFirst()
                .ifPresent(topic -> {
                    TopicMastery mastery = masteryRepository.findByUserIdAndTopicId(userId, topic.getId())
                            .orElseGet(() -> new TopicMastery(userId, topic.getId(), 50));

                    int newMastery = Math.round((mastery.getMasteryPercentage() * 0.6f) + (quizPercentage * 0.4f));
                    mastery.setMasteryPercentage(Math.min(100, Math.max(0, newMastery)));
                    mastery.setLastPracticedAt(Instant.now());
                    masteryRepository.save(mastery);
                });
    }

    @Transactional(readOnly = true)
    public List<QuizAttemptDto> getQuizHistory(UUID userId) {
        List<QuizAttempt> attempts = attemptRepository.findByUserIdOrderByCompletedAtDesc(userId, PageRequest.of(0, 20));
        Map<UUID, String> quizTitleMap = quizRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .collect(Collectors.toMap(Quiz::getId, Quiz::getTitle, (a, b) -> a));

        return attempts.stream()
                .map(a -> QuizAttemptDto.fromEntity(a, quizTitleMap.getOrDefault(a.getQuizId(), "Practice Quiz")))
                .collect(Collectors.toList());
    }
}
