package com.closecircuit.strava.dto;

import java.util.List;

/**
 * DTO for member-specific statistics
 */
public class MemberStatsDto {
    
    private String athleteName;
    private Long totalActivities;
    private Double totalDistance; // in km
    private Integer totalMovingTime; // in minutes
    private Double totalElevationGain; // in km
    private List<ActivityStatsDto> activityTypeBreakdown;
    
    // Getters and Setters
    public String getAthleteName() {
        return athleteName;
    }
    
    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }
    
    public Long getTotalActivities() {
        return totalActivities;
    }
    
    public void setTotalActivities(Long totalActivities) {
        this.totalActivities = totalActivities;
    }
    
    public Double getTotalDistance() {
        return totalDistance;
    }
    
    public void setTotalDistance(Double totalDistance) {
        this.totalDistance = totalDistance;
    }
    
    public Integer getTotalMovingTime() {
        return totalMovingTime;
    }
    
    public void setTotalMovingTime(Integer totalMovingTime) {
        this.totalMovingTime = totalMovingTime;
    }
    
    public Double getTotalElevationGain() {
        return totalElevationGain;
    }
    
    public void setTotalElevationGain(Double totalElevationGain) {
        this.totalElevationGain = totalElevationGain;
    }
    
    public List<ActivityStatsDto> getActivityTypeBreakdown() {
        return activityTypeBreakdown;
    }
    
    public void setActivityTypeBreakdown(List<ActivityStatsDto> activityTypeBreakdown) {
        this.activityTypeBreakdown = activityTypeBreakdown;
    }
}
