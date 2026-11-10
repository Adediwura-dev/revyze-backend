package com.revyze.recommendation.service;

import com.revyze.course.entity.Course;
import com.revyze.course.entity.Exam;
import com.revyze.course.entity.Topic;
import com.revyze.course.entity.TopicMastery;
import com.revyze.course.repository.CourseRepository;
import com.revyze.course.repository.ExamRepository;
import com.revyze.course.repository.TopicMasteryRepository;
import com.revyze.course.repository.TopicRepository;
import com.revyze.material.entity.StudyMaterial;
import com.revyze.material.repository.StudyMaterialRepository;
import com.revyze.recommendation.dto.RecommendationDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationEngineService {

    private final CourseRepository courseRepository;
    private final TopicRepository topicRepository;
    private final ExamRepository examRepository;
    private final TopicMasteryRepository topicMasteryRepository;
    private final StudyMaterialRepository studyMaterialRepository;

    @Autowired
    public RecommendationEngineService(
            CourseRepository courseRepository,
            TopicRepository topicRepository,
            ExamRepository examRepository,
            TopicMasteryRepository topicMasteryRepository,
            StudyMaterialRepository studyMaterialRepository) {
        this.courseRepository = courseRepository;
        this.topicRepository = topicRepository;
        this.examRepository = examRepository;
        this.topicMasteryRepository = topicMasteryRepository;
        this.studyMaterialRepository = studyMaterialRepository;
    }

    @Transactional(readOnly = true)
    public RecommendationDto getRecommendationForUser(UUID userId) {
        List<Course> courses = courseRepository.findByUserId(userId);
        List<StudyMaterial> materials = studyMaterialRepository.findByUserIdOrderByCreatedAtDesc(userId);

        if (courses.isEmpty() && materials.isEmpty()) {
            return RecommendationDto.emptyOnboarding();
        }

        if (courses.isEmpty() && !materials.isEmpty()) {
            StudyMaterial latestMaterial = materials.get(0);
            String rawName = latestMaterial.getOriginalFilename();
            String topicName = rawName.contains(".") ? rawName.substring(0, rawName.lastIndexOf('.')) : rawName;

            return RecommendationDto.populated(
                    topicName,
                    "Study Materials",
                    "Needs attention",
                    48,
                    6L,
                    4L,
                    "5 / 10",
                    "Revise " + topicName,
                    30,
                    "Exam in 6 days and mastery below 50%"
            );
        }

        List<UUID> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
        List<Topic> topics = topicRepository.findByCourseIdIn(courseIds);
        if (topics.isEmpty()) {
            return RecommendationDto.emptyOnboarding();
        }

        Map<UUID, Course> courseMap = courses.stream().collect(Collectors.toMap(Course::getId, c -> c));

        LocalDate today = LocalDate.now();
        List<Exam> userExams = examRepository.findByUserIdOrderByExamDateAsc(userId).stream()
                .filter(e -> !e.getExamDate().isBefore(today))
                .collect(Collectors.toList());

        Map<UUID, Long> courseToNextExamDays = new HashMap<>();
        for (Exam exam : userExams) {
            courseToNextExamDays.putIfAbsent(exam.getCourseId(), ChronoUnit.DAYS.between(today, exam.getExamDate()));
        }

        List<TopicMastery> userMasteries = topicMasteryRepository.findByUserId(userId);
        Map<UUID, TopicMastery> masteryMap = userMasteries.stream()
                .collect(Collectors.toMap(TopicMastery::getTopicId, m -> m, (a, b) -> a));

        Topic bestTopic = null;
        double highestUrgencyScore = -1.0;
        int chosenMastery = 50;
        Long chosenNextExamDays = null;
        Long chosenDaysAgo = null;

        for (Topic topic : topics) {
            TopicMastery tm = masteryMap.get(topic.getId());
            int mastery = tm != null ? tm.getMasteryPercentage() : topic.getDefaultMasteryPercentage();

            Long examDays = courseToNextExamDays.get(topic.getCourseId());

            Long daysAgo = 3L;
            if (tm != null && tm.getLastPracticedAt() != null) {
                daysAgo = Duration.between(tm.getLastPracticedAt(), Instant.now()).toDays();
            }

            double urgency = (100.0 - mastery) * 1.5;
            if (examDays != null) {
                urgency += Math.max(0.0, (30.0 - examDays) * 2.0);
            }
            urgency += Math.min(daysAgo, 14L) * 1.0;

            if (urgency > highestUrgencyScore) {
                highestUrgencyScore = urgency;
                bestTopic = topic;
                chosenMastery = mastery;
                chosenNextExamDays = examDays;
                chosenDaysAgo = daysAgo;
            }
        }

        if (bestTopic == null) {
            return RecommendationDto.emptyOnboarding();
        }

        Course matchedCourse = courseMap.get(bestTopic.getCourseId());
        String courseName = matchedCourse != null ? matchedCourse.getName() : "General";

        String status = chosenMastery < 50 ? "Needs attention" : (chosenMastery < 75 ? "Review recommended" : "On track");
        String reason = (chosenNextExamDays != null ? "Exam in " + chosenNextExamDays + " days" : "Upcoming syllabus")
                + (chosenMastery < 50 ? " and mastery below 50%" : " to reinforce knowledge");

        return RecommendationDto.populated(
                bestTopic.getTitle(),
                courseName,
                status,
                chosenMastery,
                chosenNextExamDays != null ? chosenNextExamDays : 7L,
                chosenDaysAgo != null ? chosenDaysAgo : 2L,
                "5 / 10",
                "Revise " + bestTopic.getTitle(),
                30,
                reason
        );
    }
}

