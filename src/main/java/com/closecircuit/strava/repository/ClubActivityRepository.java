package com.closecircuit.strava.repository;

import com.closecircuit.strava.entity.ClubActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClubActivityRepository
        extends JpaRepository<ClubActivityLog, Long> {

    boolean existsByActivityId(Long activityId);

	boolean existsByNameAndAthleteNameAndDistanceAndMovingTime(String name, String athleteName, Double distance,
			Integer movingTime);
}
