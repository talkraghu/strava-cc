package com.closecircuit.strava.service;

import com.closecircuit.strava.client.StravaClient;
import com.closecircuit.strava.dto.StravaActivityDto;
import com.closecircuit.strava.entity.ClubActivityLog;
import com.closecircuit.strava.repository.ClubActivityRepository;
import com.closecircuit.strava.utils.ActivityIdGenerator;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class ClubActivityService {

    private static final Logger log = LoggerFactory.getLogger(ClubActivityService.class);

    private final StravaClient client;
    private final ClubActivityRepository repository;

    public ClubActivityService(StravaClient client, ClubActivityRepository repository) {
        this.client = client;
        this.repository = repository;
    }

    public void collectClubActivities() {

        List<StravaActivityDto> activities = client.fetchClubActivitiesRaw();
        log.info("Processing {} activities", activities.size());

        for (StravaActivityDto dto : activities) {
            try {
                saveSingleActivity(dto);
            } catch (Exception ex) {
                log.warn("Skipping invalid activity: {}", dto.getName(), ex);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void saveSingleActivity(StravaActivityDto dto) {

        try {
			String athleteName = dto.getAthlete() != null
			        ? dto.getAthlete().getFirstname() + " " + dto.getAthlete().getLastname()
			        : "Unknown Athlete";

			ClubActivityLog entity = map(dto);
			entity.setAthleteName(athleteName);
		
			repository.save(entity);
		} catch (ConstraintViolationException e1) {

		}
        catch (DataIntegrityViolationException e2) {

		}
    }

    
    private ClubActivityLog map(StravaActivityDto dto) {
        ClubActivityLog logEntity = new ClubActivityLog();

        long activityId = ActivityIdGenerator.generate(
                dto.getName(),
                dto.getType(),
                dto.getSportType(),
                dto.getDistance(),
                dto.getMovingTime(),
                dto.getElapsedTime(),
                dto.getTotalElevationGain(),
                dto.getDeviceName()
        );

        logEntity.setActivityId(activityId);
        logEntity.setName(dto.getName());
        logEntity.setType(dto.getType());
        logEntity.setSportType(dto.getType());
        
        double distanceKm = dto.getDistance() / 1000.0;
        distanceKm = Math.round(distanceKm * 100.0) / 100.0;
        logEntity.setDistance(distanceKm);
        
        logEntity.setMovingTime(dto.getMovingTime()/60);
        logEntity.setElapsedTime(dto.getElapsedTime()/60);
        
        double elevationGain = dto.getTotalElevationGain() / 1000.0;
        elevationGain = Math.round(elevationGain * 100.0) / 100.0;
        logEntity.setElevationGain(elevationGain);
        
        logEntity.setDeviceName(dto.getDeviceName());
        logEntity.setCollectedAt(Instant.now());

        return logEntity;
    }


}
