package com.revyze.progress.dto;

public class ProgressSummaryDto {

    private long totalMaterials;
    private long totalQuizzesTaken;
    private int totalStudyMinutes;
    private int overallMasteryPercentage;
    private long activeCoursesCount;
    private long upcomingExamsCount;

    public ProgressSummaryDto() {
    }

    public ProgressSummaryDto(long totalMaterials, long totalQuizzesTaken, int totalStudyMinutes,
                              int overallMasteryPercentage, long activeCoursesCount, long upcomingExamsCount) {
        this.totalMaterials = totalMaterials;
        this.totalQuizzesTaken = totalQuizzesTaken;
        this.totalStudyMinutes = totalStudyMinutes;
        this.overallMasteryPercentage = overallMasteryPercentage;
        this.activeCoursesCount = activeCoursesCount;
        this.upcomingExamsCount = upcomingExamsCount;
    }

    public long getTotalMaterials() {
        return totalMaterials;
    }

    public long getTotalQuizzesTaken() {
        return totalQuizzesTaken;
    }

    public int getTotalStudyMinutes() {
        return totalStudyMinutes;
    }

    public int getOverallMasteryPercentage() {
        return overallMasteryPercentage;
    }

    public long getActiveCoursesCount() {
        return activeCoursesCount;
    }

    public long getUpcomingExamsCount() {
        return upcomingExamsCount;
    }
}

