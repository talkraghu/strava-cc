package com.closecircuit.strava.dto;

import java.util.List;

/**
 * DTO for overall club statistics
 */
public class ClubStatsDto {
    
    private Long totalMembers;
    private Long totalActivities;
    private Double totalDistance; // in km
    private Integer totalMovingTime; // in minutes
    private Double totalElevationGain; // in km
    private List<ActivityStatsDto> activityTypeBreakdown;
    private List<MemberStatsDto> topMembers; // Top members by distance
    
    // Getters and Setters
    public Long getTotalMembers() {
        return totalMembers;
    }
    
    public void setTotalMembers(Long totalMembers) {
        this.totalMembers = totalMembers;
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
    
    public List<MemberStatsDto> getTopMembers() {
        return topMembers;
    }
    
    public void setTopMembers(List<MemberStatsDto> topMembers) {
        this.topMembers = topMembers;
    }
}
