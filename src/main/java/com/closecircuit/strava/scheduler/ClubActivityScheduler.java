package com.closecircuit.strava.scheduler;

import com.closecircuit.strava.service.ClubActivityService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ClubActivityScheduler {

    private final ClubActivityService service;

    public ClubActivityScheduler(ClubActivityService service) {
        this.service = service;
    }

    @Scheduled(fixedDelayString = "${strava.fetch.poll-interval-ms}")
    public void pollClubActivities() {
        service.collectClubActivities();
    }
}
