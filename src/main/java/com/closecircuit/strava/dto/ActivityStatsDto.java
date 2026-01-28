package com.closecircuit.strava.dto;

/**
 * DTO for activity statistics by type
 */
public class ActivityStatsDto {
    
    private String activityType;
    private Long totalActivities;
    private Double totalDistance; // in km
    private Integer totalMovingTime; // in minutes
    private Double totalElevationGain; // in km
    private Double averageDistance;
    private Integer averageMovingTime;
    private Double averageElevationGain;
    
    // Getters and Setters
    public String getActivityType() {
        return activityType;
    }
    
    public void setActivityType(String activityType) {
        this.activityType = activityType;
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
    
    public Double getAverageDistance() {
        return averageDistance;
    }
    
    public void setAverageDistance(Double averageDistance) {
        this.averageDistance = averageDistance;
    }
    
    public Integer getAverageMovingTime() {
        return averageMovingTime;
    }
    
    public void setAverageMovingTime(Integer averageMovingTime) {
        this.averageMovingTime = averageMovingTime;
    }
    
    public Double getAverageElevationGain() {
        return averageElevationGain;
    }
    
    public void setAverageElevationGain(Double averageElevationGain) {
        this.averageElevationGain = averageElevationGain;
    }
}
