package com.closecircuit.strava.controller;

import com.closecircuit.strava.dto.ActivityStatsDto;
import com.closecircuit.strava.dto.ClubStatsDto;
import com.closecircuit.strava.dto.MemberStatsDto;
import com.closecircuit.strava.service.ClubStatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/stats")
public class StatsController {

    private final ClubStatsService statsService;

    public StatsController(ClubStatsService statsService) {
        this.statsService = statsService;
    }

    /**
     * Get overall club statistics
     * GET /stats/club?fromDate=2024-01-01T00:00:00Z&toDate=2024-12-31T23:59:59Z
     */
    @GetMapping("/club")
    public ResponseEntity<ClubStatsDto> getClubStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        ClubStatsDto stats = statsService.getClubStats(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get statistics by activity type
     * GET /stats/activity-type?activityType=Run&fromDate=...&toDate=...
     */
    @GetMapping("/activity-type")
    public ResponseEntity<List<ActivityStatsDto>> getActivityTypeStats(
            @RequestParam(required = false) String activityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        List<ActivityStatsDto> stats = statsService.getActivityTypeStats(activityType, fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get statistics for a specific activity type (Run, Ride, Walk, Hike)
     * GET /stats/activity-type/Run?fromDate=...&toDate=...
     */
    @GetMapping("/activity-type/{activityType}")
    public ResponseEntity<ActivityStatsDto> getStatsForActivityType(
            @PathVariable String activityType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        ActivityStatsDto stats = statsService.getStatsForActivityType(activityType, fromDate, toDate);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * Get statistics for a specific member
     * GET /stats/member?athleteName=John Doe&fromDate=...&toDate=...
     */
    @GetMapping("/member")
    public ResponseEntity<MemberStatsDto> getMemberStats(
            @RequestParam String athleteName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        MemberStatsDto stats = statsService.getMemberStats(athleteName, fromDate, toDate);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    /**
     * Get statistics for all members
     * GET /stats/members?fromDate=...&toDate=...
     */
    @GetMapping("/members")
    public ResponseEntity<List<MemberStatsDto>> getAllMemberStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        List<MemberStatsDto> stats = statsService.getAllMemberStats(fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get top N members by distance
     * GET /stats/leaderboard?limit=10&fromDate=...&toDate=...
     */
    @GetMapping("/leaderboard")
    public ResponseEntity<List<MemberStatsDto>> getLeaderboard(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        List<MemberStatsDto> stats = statsService.getTopMembers(limit, fromDate, toDate);
        return ResponseEntity.ok(stats);
    }

    /**
     * Quick stats endpoints for common queries
     */
    @GetMapping("/running")
    public ResponseEntity<ActivityStatsDto> getRunningStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        ActivityStatsDto stats = statsService.getStatsForActivityType("Run", fromDate, toDate);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/riding")
    public ResponseEntity<ActivityStatsDto> getRidingStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        ActivityStatsDto stats = statsService.getStatsForActivityType("Ride", fromDate, toDate);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/walking")
    public ResponseEntity<ActivityStatsDto> getWalkingStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        ActivityStatsDto stats = statsService.getStatsForActivityType("Walk", fromDate, toDate);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/hiking")
    public ResponseEntity<ActivityStatsDto> getHikingStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant toDate) {
        
        ActivityStatsDto stats = statsService.getStatsForActivityType("Hike", fromDate, toDate);
        if (stats == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(stats);
    }
}
