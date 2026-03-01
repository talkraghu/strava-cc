package com.closecircuit.strava.controller;

import com.closecircuit.strava.entity.ClubActivityLog;
import com.closecircuit.strava.repository.ClubActivityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ClubActivityRepository repository;

    @GetMapping
    public List<ClubActivityLog> getAllActivities() {
        return repository.findAll();
    }

    @GetMapping("/search")
    public List<ClubActivityLog> searchActivities(
            @RequestParam(required = false) String athleteName,
            @RequestParam(required = false) Double minDistance,
            @RequestParam(required = false) Double maxDistance,
            @RequestParam(required = false) Integer minTime,
            @RequestParam(required = false) Integer maxTime) {
        
        List<ClubActivityLog> activities = repository.findAll();
        
        return activities.stream()
            .filter(a -> athleteName == null || a.getAthleteName().toLowerCase().contains(athleteName.toLowerCase()))
            .filter(a -> minDistance == null || a.getDistance() >= minDistance)
            .filter(a -> maxDistance == null || a.getDistance() <= maxDistance)
            .filter(a -> minTime == null || a.getMovingTime() >= minTime)
            .filter(a -> maxTime == null || a.getMovingTime() <= maxTime)
            .collect(Collectors.toList());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteActivity(@PathVariable Long id) {
        if (repository.existsById(id)) {
            ClubActivityLog activity = repository.findById(id).orElse(null);
            repository.deleteById(id);
            return ResponseEntity.ok("Deleted activity: " + 
                (activity != null ? activity.getName() + " by " + activity.getAthleteName() : "ID " + id));
        }
        return ResponseEntity.notFound().build();
    }
}
