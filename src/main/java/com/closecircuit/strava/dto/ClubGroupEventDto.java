package com.closecircuit.strava.dto;

import java.util.List;

/**
 * DTO for Strava club group event (summary representation).
 * Used to count events and their occurrences in a date range.
 */
public class ClubGroupEventDto {

    private Long id;
    private String title;
    private String activityType;
    private List<String> upcomingOccurrences; // ISO datetime strings in UTC

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public List<String> getUpcomingOccurrences() {
        return upcomingOccurrences;
    }

    public void setUpcomingOccurrences(List<String> upcomingOccurrences) {
        this.upcomingOccurrences = upcomingOccurrences;
    }
}
