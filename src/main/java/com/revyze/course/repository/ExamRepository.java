package com.revyze.course.repository;

import com.revyze.course.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ExamRepository extends JpaRepository<Exam, UUID> {
    List<Exam> findByUserIdOrderByExamDateAsc(UUID userId);
    List<Exam> findByCourseId(UUID courseId);
}

