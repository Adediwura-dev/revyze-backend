package com.revyze.course.repository;

import com.revyze.course.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TopicRepository extends JpaRepository<Topic, UUID> {
    List<Topic> findByCourseId(UUID courseId);
    List<Topic> findByCourseIdIn(List<UUID> courseIds);
}

