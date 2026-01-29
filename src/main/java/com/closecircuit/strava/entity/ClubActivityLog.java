package com.closecircuit.strava.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
    name = "club_activity_log",
    uniqueConstraints = @UniqueConstraint(columnNames = "activity_id")
)
public class ClubActivityLog {

    // DB primary key (auto-generated)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Application-generated activity ID (hash-based)
    @Column(name = "activity_id", nullable = false, unique = true)
    private Long activityId;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(name = "athlete_name", nullable = false, length = 255)
    private String athleteName;

    @Column(nullable = false, length = 50)
    private String type;
    
    @Column(nullable = false, length = 50)
    private String sportType;

    @Column
    private Double distance;

    @Column(name = "moving_time")
    private Integer movingTime;
    
    @Column(name = "elapsed_time")
    private Integer elapsedTime;

    @Column(name = "elevation_gain")
    private Double elevationGain;

    @Column(name = "device_name", length = 100)
    private String deviceName;

    @Column(name = "collected_at", nullable = false)
    private Instant collectedAt;

    /** When the activity was performed (from Strava start_date). Used for date-range stats. */
    @Column(name = "start_date")
    private Instant startDate;

    // ===== Getters & Setters =====

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAthleteName() {
        return athleteName;
    }

    public void setAthleteName(String athleteName) {
        this.athleteName = athleteName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    
    public String getSportType() {
        return sportType;
    }

    public void setSportType(String sportType) {
        this.sportType = sportType;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public Integer getMovingTime() {
        return movingTime;
    }

    public void setMovingTime(Integer movingTime) {
        this.movingTime = movingTime;
    }
    
    public Integer getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(Integer elapsedTime) {
        this.elapsedTime = elapsedTime;
    }


    public Double getElevationGain() {
        return elevationGain;
    }

    public void setElevationGain(Double elevationGain) {
        this.elevationGain = elevationGain;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Instant getCollectedAt() {
        return collectedAt;
    }

    public void setCollectedAt(Instant collectedAt) {
        this.collectedAt = collectedAt;
    }

    public Instant getStartDate() {
        return startDate;
    }

    public void setStartDate(Instant startDate) {
        this.startDate = startDate;
    }
}
