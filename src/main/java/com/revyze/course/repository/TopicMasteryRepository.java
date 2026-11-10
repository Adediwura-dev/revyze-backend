package com.revyze.course.repository;

import com.revyze.course.entity.TopicMastery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TopicMasteryRepository extends JpaRepository<TopicMastery, UUID> {
    Optional<TopicMastery> findByUserIdAndTopicId(UUID userId, UUID topicId);
    List<TopicMastery> findByUserId(UUID userId);
}
