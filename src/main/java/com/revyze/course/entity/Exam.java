package com.revyze.course.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "exams")
public class Exam {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID courseId;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(nullable = false)
    private LocalDate examDate;

    @Column(nullable = false)
    private int priorityWeight = 1;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Exam() {
    }

    public Exam(UUID courseId, UUID userId, String title, LocalDate examDate, int priorityWeight) {
        this.courseId = courseId;
        this.userId = userId;
        this.title = title;
        this.examDate = examDate;
        this.priorityWeight = priorityWeight;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getCourseId() {
        return courseId;
    }

    public void setCourseId(UUID courseId) {
        this.courseId = courseId;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public int getPriorityWeight() {
        return priorityWeight;
    }

    public void setPriorityWeight(int priorityWeight) {
        this.priorityWeight = priorityWeight;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
