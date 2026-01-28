package com.closecircuit.strava.service;

import com.closecircuit.strava.dto.ActivityStatsDto;
import com.closecircuit.strava.dto.ClubStatsDto;
import com.closecircuit.strava.dto.MemberStatsDto;
import com.closecircuit.strava.repository.ClubActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubStatsService {

    private static final Logger log = LoggerFactory.getLogger(ClubStatsService.class);

    private final ClubActivityRepository repository;

    public ClubStatsService(ClubActivityRepository repository) {
        this.repository = repository;
    }

    /**
     * Get overall club statistics
     */
    public ClubStatsDto getClubStats(Instant fromDate, Instant toDate) {
        log.debug("Getting club stats from {} to {}", fromDate, toDate);

        ClubStatsDto stats = new ClubStatsDto();

        // Get overall stats
        Object[] overallStats = repository.getOverallStats(fromDate, toDate);
        if (overallStats != null && overallStats.length >= 5) {
            stats.setTotalMembers(((Number) overallStats[0]).longValue());
            stats.setTotalActivities(((Number) overallStats[1]).longValue());
            stats.setTotalDistance(((Number) overallStats[2]).doubleValue());
            stats.setTotalMovingTime(((Number) overallStats[3]).intValue());
            stats.setTotalElevationGain(((Number) overallStats[4]).doubleValue());
        }

        // Get activity type breakdown
        List<ActivityStatsDto> activityBreakdown = getActivityTypeStats(null, fromDate, toDate);
        stats.setActivityTypeBreakdown(activityBreakdown);

        // Get top 10 members by distance
        List<MemberStatsDto> topMembers = getTopMembers(10, fromDate, toDate);
        stats.setTopMembers(topMembers);

        return stats;
    }

    /**
     * Get statistics by activity type (Run, Ride, Walk, etc.)
     */
    public List<ActivityStatsDto> getActivityTypeStats(String sportType, Instant fromDate, Instant toDate) {
        log.debug("Getting activity type stats for sportType: {}, from {} to {}", sportType, fromDate, toDate);

        List<Object[]> results = repository.getStatsBySportType(sportType, fromDate, toDate);
        return mapToActivityStatsDto(results);
    }

    /**
     * Get statistics for a specific member
     */
    public MemberStatsDto getMemberStats(String athleteName, Instant fromDate, Instant toDate) {
        log.debug("Getting stats for member: {}, from {} to {}", athleteName, fromDate, toDate);

        List<Object[]> results = repository.getStatsByAthlete(athleteName, fromDate, toDate);
        
        if (results.isEmpty()) {
            return null;
        }

        Object[] memberStats = results.get(0);
        MemberStatsDto stats = new MemberStatsDto();
        stats.setAthleteName((String) memberStats[0]);
        stats.setTotalActivities(((Number) memberStats[1]).longValue());
        stats.setTotalDistance(roundTo2Decimals(((Number) memberStats[2]).doubleValue()));
        stats.setTotalMovingTime(((Number) memberStats[3]).intValue());
        stats.setTotalElevationGain(roundTo2Decimals(((Number) memberStats[4]).doubleValue()));

        // Get activity type breakdown for this member
        List<Object[]> activityBreakdown = repository.getStatsByAthleteAndSportType(athleteName, fromDate, toDate);
        stats.setActivityTypeBreakdown(mapToActivityStatsDto(activityBreakdown));

        return stats;
    }

    /**
     * Get all member statistics
     */
    public List<MemberStatsDto> getAllMemberStats(Instant fromDate, Instant toDate) {
        log.debug("Getting all member stats from {} to {}", fromDate, toDate);

        List<Object[]> results = repository.getStatsByAthlete(null, fromDate, toDate);
        return mapToMemberStatsDto(results, fromDate, toDate);
    }

    /**
     * Get top N members by distance
     */
    public List<MemberStatsDto> getTopMembers(int limit, Instant fromDate, Instant toDate) {
        log.debug("Getting top {} members from {} to {}", limit, fromDate, toDate);

        List<Object[]> results = repository.getStatsByAthlete(null, fromDate, toDate);
        return mapToMemberStatsDto(results, fromDate, toDate)
                .stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Get statistics for specific activity types (Run, Ride, Walk)
     */
    public ActivityStatsDto getStatsForActivityType(String activityType, Instant fromDate, Instant toDate) {
        log.debug("Getting stats for activity type: {}, from {} to {}", activityType, fromDate, toDate);

        List<ActivityStatsDto> stats = getActivityTypeStats(activityType, fromDate, toDate);
        return stats.isEmpty() ? null : stats.get(0);
    }

    // Helper methods

    private List<ActivityStatsDto> mapToActivityStatsDto(List<Object[]> results) {
        List<ActivityStatsDto> statsList = new ArrayList<>();

        for (Object[] row : results) {
            ActivityStatsDto stats = new ActivityStatsDto();
            stats.setActivityType((String) row[0]);
            stats.setTotalActivities(((Number) row[1]).longValue());
            stats.setTotalDistance(roundTo2Decimals(((Number) row[2]).doubleValue()));
            stats.setTotalMovingTime(((Number) row[3]).intValue());
            stats.setTotalElevationGain(roundTo2Decimals(((Number) row[4]).doubleValue()));
            
            if (row.length > 5) {
                stats.setAverageDistance(roundTo2Decimals(((Number) row[5]).doubleValue()));
                stats.setAverageMovingTime(((Number) row[6]).intValue());
                stats.setAverageElevationGain(roundTo2Decimals(((Number) row[7]).doubleValue()));
            }

            statsList.add(stats);
        }

        return statsList;
    }

    private List<MemberStatsDto> mapToMemberStatsDto(List<Object[]> results, Instant fromDate, Instant toDate) {
        List<MemberStatsDto> memberStatsList = new ArrayList<>();

        for (Object[] row : results) {
            String athleteName = (String) row[0];
            MemberStatsDto stats = new MemberStatsDto();
            stats.setAthleteName(athleteName);
            stats.setTotalActivities(((Number) row[1]).longValue());
            stats.setTotalDistance(roundTo2Decimals(((Number) row[2]).doubleValue()));
            stats.setTotalMovingTime(((Number) row[3]).intValue());
            stats.setTotalElevationGain(roundTo2Decimals(((Number) row[4]).doubleValue()));

            // Get activity type breakdown for this member
            List<Object[]> activityBreakdown = repository.getStatsByAthleteAndSportType(athleteName, fromDate, toDate);
            stats.setActivityTypeBreakdown(mapToActivityStatsDto(activityBreakdown));

            memberStatsList.add(stats);
        }

        return memberStatsList;
    }

    private double roundTo2Decimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
