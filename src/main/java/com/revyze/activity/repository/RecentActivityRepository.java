package com.revyze.activity.repository;

import com.revyze.activity.entity.RecentActivity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RecentActivityRepository extends JpaRepository<RecentActivity, UUID> {
    List<RecentActivity> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
}
