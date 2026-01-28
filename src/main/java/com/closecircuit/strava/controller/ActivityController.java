package com.closecircuit.strava.controller;

import com.closecircuit.strava.entity.ClubActivityLog;
import com.closecircuit.strava.repository.ClubActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ClubActivityRepository repository = null;

    @GetMapping
    public List<ClubActivityLog> getAllActivities() {
        return repository.findAll();
    }
}
