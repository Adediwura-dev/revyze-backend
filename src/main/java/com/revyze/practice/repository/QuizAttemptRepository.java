package com.revyze.practice.repository;

import com.revyze.practice.entity.QuizAttempt;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, UUID> {
    List<QuizAttempt> findByUserIdOrderByCompletedAtDesc(UUID userId, Pageable pageable);
    List<QuizAttempt> findByUserId(UUID userId);
    long countByUserId(UUID userId);
}
