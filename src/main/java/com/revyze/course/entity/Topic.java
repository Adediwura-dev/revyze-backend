package com.revyze.course.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "topics")
public class Topic {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID courseId;

    @Column(nullable = false, length = 120)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private int defaultMasteryPercentage = 50;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    public Topic() {
    }

    public Topic(UUID courseId, String title, String description, int defaultMasteryPercentage) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.defaultMasteryPercentage = defaultMasteryPercentage;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDefaultMasteryPercentage() {
        return defaultMasteryPercentage;
    }

    public void setDefaultMasteryPercentage(int defaultMasteryPercentage) {
        this.defaultMasteryPercentage = defaultMasteryPercentage;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
