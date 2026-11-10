package com.revyze.progress.service;

import com.revyze.activity.entity.RecentActivity;
import com.revyze.activity.repository.RecentActivityRepository;
import com.revyze.course.entity.Course;
import com.revyze.course.entity.Exam;
import com.revyze.course.entity.Topic;
import com.revyze.course.entity.TopicMastery;
import com.revyze.course.repository.CourseRepository;
import com.revyze.course.repository.ExamRepository;
import com.revyze.course.repository.TopicMasteryRepository;
import com.revyze.course.repository.TopicRepository;
import com.revyze.material.repository.StudyMaterialRepository;
import com.revyze.practice.entity.QuizAttempt;
import com.revyze.practice.repository.QuizAttemptRepository;
import com.revyze.progress.dto.ProgressSummaryDto;
import com.revyze.progress.dto.TopicProgressDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProgressService {

    private final StudyMaterialRepository studyMaterialRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final RecentActivityRepository recentActivityRepository;
    private final CourseRepository courseRepository;
    private final ExamRepository examRepository;
    private final TopicMasteryRepository topicMasteryRepository;
    private final TopicRepository topicRepository;

    public ProgressService(StudyMaterialRepository studyMaterialRepository,
                           QuizAttemptRepository quizAttemptRepository,
                           RecentActivityRepository recentActivityRepository,
                           CourseRepository courseRepository,
                           ExamRepository examRepository,
                           TopicMasteryRepository topicMasteryRepository,
                           TopicRepository topicRepository) {
        this.studyMaterialRepository = studyMaterialRepository;
        this.quizAttemptRepository = quizAttemptRepository;
        this.recentActivityRepository = recentActivityRepository;
        this.courseRepository = courseRepository;
        this.examRepository = examRepository;
        this.topicMasteryRepository = topicMasteryRepository;
        this.topicRepository = topicRepository;
    }

    @Transactional(readOnly = true)
    public ProgressSummaryDto getProgressSummary(UUID userId) {
        long totalMaterials = studyMaterialRepository.countByUserId(userId);
        long totalQuizzes = quizAttemptRepository.countByUserId(userId);

        List<RecentActivity> activities = recentActivityRepository.findByUserId(userId);
        int totalStudyMinutes = (int) (totalQuizzes * 15 + activities.size() * 10);

        List<Course> courses = courseRepository.findByUserId(userId);
        List<Exam> exams = examRepository.findByUserIdOrderByExamDateAsc(userId);
        LocalDate today = LocalDate.now();
        long upcomingExamsCount = exams.stream()
                .filter(e -> !e.getExamDate().isBefore(today))
                .count();

        List<TopicMastery> masteries = topicMasteryRepository.findByUserId(userId);
        int overallMastery = 0;
        if (!masteries.isEmpty()) {
            double avg = masteries.stream().mapToInt(TopicMastery::getMasteryPercentage).average().orElse(0.0);
            overallMastery = (int) Math.round(avg);
        }

        return new ProgressSummaryDto(
                totalMaterials,
                totalQuizzes,
                totalStudyMinutes,
                overallMastery,
                courses.size(),
                upcomingExamsCount
        );
    }

    @Transactional(readOnly = true)
    public List<TopicProgressDto> getTopicProgress(UUID userId) {
        List<TopicMastery> masteries = topicMasteryRepository.findByUserId(userId);

        if (masteries.isEmpty()) {
            return List.of(
                    new TopicProgressDto(UUID.randomUUID(), "Recursion & Call Stacks", "Computer Science", 0, 0, null, "Needs Attention"),
                    new TopicProgressDto(UUID.randomUUID(), "Databases & SQL Transactions", "Data Systems", 0, 0, null, "Needs Attention"),
                    new TopicProgressDto(UUID.randomUUID(), "Data Structures & Complexity", "Algorithms", 0, 0, null, "Needs Attention")
            );
        }

        Map<UUID, Topic> topicMap = topicRepository.findAllById(
                masteries.stream().map(TopicMastery::getTopicId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Topic::getId, t -> t));

        Map<UUID, Course> courseMap = courseRepository.findAllById(
                topicMap.values().stream().map(Topic::getCourseId).collect(Collectors.toSet())
        ).stream().collect(Collectors.toMap(Course::getId, c -> c));

        long totalAttempts = quizAttemptRepository.countByUserId(userId);

        return masteries.stream().map(m -> {
            Topic topic = topicMap.get(m.getTopicId());
            String topicName = topic != null ? topic.getTitle() : "Core Topic";
            String courseName = "General Studies";
            if (topic != null && courseMap.containsKey(topic.getCourseId())) {
                courseName = courseMap.get(topic.getCourseId()).getName();
            }

            int mastery = m.getMasteryPercentage();
            String status;
            if (mastery >= 80) {
                status = "Mastered";
            } else if (mastery >= 50) {
                status = "In Progress";
            } else {
                status = "Needs Attention";
            }

            return new TopicProgressDto(
                    m.getTopicId(),
                    topicName,
                    courseName,
                    mastery,
                    (int) totalAttempts,
                    m.getLastPracticedAt(),
                    status
            );
        }).sorted(Comparator.comparingInt(TopicProgressDto::getMasteryPercentage))
          .collect(Collectors.toList());
    }
}
