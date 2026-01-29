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
    private static final Instant DEFAULT_FROM = Instant.EPOCH;
    private static final Instant DEFAULT_TO = Instant.parse("2099-12-31T23:59:59Z");

    private final ClubActivityRepository repository;

    public ClubStatsService(ClubActivityRepository repository) {
        this.repository = repository;
    }

    /**
     * Get overall club statistics
     */
    public ClubStatsDto getClubStats(Instant fromDate, Instant toDate) {
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting club stats from {} to {}", from, to);

        ClubStatsDto stats = new ClubStatsDto();

        try {
            // Get overall stats
            Object[] overallStats = repository.getOverallStats(from, to);
            if (overallStats != null && overallStats.length >= 5) {
                stats.setTotalMembers(overallStats[0] != null ? ((Number) overallStats[0]).longValue() : 0L);
                stats.setTotalActivities(overallStats[1] != null ? ((Number) overallStats[1]).longValue() : 0L);
                stats.setTotalDistance(overallStats[2] != null ? ((Number) overallStats[2]).doubleValue() : 0.0);
                stats.setTotalMovingTime(overallStats[3] != null ? ((Number) overallStats[3]).intValue() : 0);
                stats.setTotalElevationGain(overallStats[4] != null ? ((Number) overallStats[4]).doubleValue() : 0.0);
            } else {
                // Initialize with zeros if no data
                stats.setTotalMembers(0L);
                stats.setTotalActivities(0L);
                stats.setTotalDistance(0.0);
                stats.setTotalMovingTime(0);
                stats.setTotalElevationGain(0.0);
            }

            // Get activity type breakdown (only Walk, Ride, Hike, Run)
            List<ActivityStatsDto> activityBreakdown = getActivityTypeStats(null, from, to);
            if (activityBreakdown != null) {
                activityBreakdown = activityBreakdown.stream()
                    .filter(a -> a.getActivityType() != null && java.util.Set.of("Walk", "Ride", "Hike", "Run").contains(a.getActivityType()))
                    .collect(Collectors.toList());
            }
            stats.setActivityTypeBreakdown(activityBreakdown != null ? activityBreakdown : new ArrayList<>());

            // Get all members by distance (for overview list)
            List<MemberStatsDto> topMembers = getTopMembers(Integer.MAX_VALUE, from, to);
            stats.setTopMembers(topMembers != null ? topMembers : new ArrayList<>());
        } catch (Exception e) {
            log.error("Error getting club stats", e);
            // Return empty stats instead of throwing
            stats.setTotalMembers(0L);
            stats.setTotalActivities(0L);
            stats.setTotalDistance(0.0);
            stats.setTotalMovingTime(0);
            stats.setTotalElevationGain(0.0);
            stats.setActivityTypeBreakdown(new ArrayList<>());
            stats.setTopMembers(new ArrayList<>());
        }

        return stats;
    }

    /**
     * Get statistics by activity type (Run, Ride, Walk, etc.)
     */
    public List<ActivityStatsDto> getActivityTypeStats(String sportType, Instant fromDate, Instant toDate) {
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting activity type stats for sportType: {}, from {} to {}", sportType, from, to);

        try {
            List<Object[]> results = repository.getStatsBySportType(sportType, from, to);
            List<ActivityStatsDto> list = results != null ? mapToActivityStatsDto(results) : new ArrayList<>();
            // Only return Walk, Ride, Hike, Run
            return list.stream()
                .filter(a -> a.getActivityType() != null && java.util.Set.of("Walk", "Ride", "Hike", "Run").contains(a.getActivityType()))
                .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting activity type stats", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get statistics for a specific member
     */
    public MemberStatsDto getMemberStats(String athleteName, Instant fromDate, Instant toDate) {
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting stats for member: {}, from {} to {}", athleteName, from, to);

        List<Object[]> results = repository.getStatsByAthlete(athleteName, from, to);
        
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

        // Get activity type breakdown for this member (only Walk, Ride, Hike, Run)
        List<Object[]> activityBreakdown = repository.getStatsByAthleteAndSportType(athleteName, from, to);
        List<ActivityStatsDto> breakdown = mapToActivityStatsDto(activityBreakdown).stream()
            .filter(a -> a.getActivityType() != null && java.util.Set.of("Walk", "Ride", "Hike", "Run").contains(a.getActivityType()))
            .collect(Collectors.toList());
        stats.setActivityTypeBreakdown(breakdown);

        return stats;
    }

    /**
     * Get all member statistics
     */
    public List<MemberStatsDto> getAllMemberStats(Instant fromDate, Instant toDate) {
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting all member stats from {} to {}", from, to);

        try {
            List<Object[]> results = repository.getStatsByAthlete(null, from, to);
            return results != null ? mapToMemberStatsDto(results, from, to) : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting all member stats", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get top N members by distance
     */
    public List<MemberStatsDto> getTopMembers(int limit, Instant fromDate, Instant toDate) {
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting top {} members from {} to {}", limit, from, to);

        try {
            List<Object[]> results = repository.getStatsByAthlete(null, from, to);
            if (results == null || results.isEmpty()) {
                return new ArrayList<>();
            }
            return mapToMemberStatsDto(results, from, to)
                    .stream()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error getting top members", e);
            return new ArrayList<>();
        }
    }

    /**
     * Get statistics for specific activity types (Run, Ride, Walk)
     */
    public ActivityStatsDto getStatsForActivityType(String activityType, Instant fromDate, Instant toDate) {
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting stats for activity type: {}, from {} to {}", activityType, from, to);

        List<ActivityStatsDto> stats = getActivityTypeStats(activityType, from, to);
        return stats.isEmpty() ? null : stats.get(0);
    }

    /**
     * Get distance per member for a given activity type (Walk, Ride, Hike, Run).
     */
    public List<MemberStatsDto> getMemberStatsByActivityType(String sportType, Instant fromDate, Instant toDate) {
        if (sportType == null || sportType.isBlank()) {
            return new ArrayList<>();
        }
        Instant from = fromDate != null ? fromDate : DEFAULT_FROM;
        Instant to = toDate != null ? toDate : DEFAULT_TO;
        log.debug("Getting member stats by activity type: {}, from {} to {}", sportType, from, to);

        try {
            List<Object[]> results = repository.getStatsByAthleteForSportType(sportType, from, to);
            return results != null ? mapToMemberStatsDtoSimple(results) : new ArrayList<>();
        } catch (Exception e) {
            log.error("Error getting member stats by activity type", e);
            return new ArrayList<>();
        }
    }

    // Helper methods

    private List<ActivityStatsDto> mapToActivityStatsDto(List<Object[]> results) {
        List<ActivityStatsDto> statsList = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return statsList;
        }

        for (Object[] row : results) {
            if (row == null || row.length < 5) {
                continue;
            }
            
            try {
                ActivityStatsDto stats = new ActivityStatsDto();
                stats.setActivityType(row[0] != null ? (String) row[0] : "Unknown");
                stats.setTotalActivities(row[1] != null ? ((Number) row[1]).longValue() : 0L);
                stats.setTotalDistance(row[2] != null ? roundTo2Decimals(((Number) row[2]).doubleValue()) : 0.0);
                stats.setTotalMovingTime(row[3] != null ? ((Number) row[3]).intValue() : 0);
                stats.setTotalElevationGain(row[4] != null ? roundTo2Decimals(((Number) row[4]).doubleValue()) : 0.0);
                
                if (row.length > 5) {
                    stats.setAverageDistance(row[5] != null ? roundTo2Decimals(((Number) row[5]).doubleValue()) : 0.0);
                    stats.setAverageMovingTime(row[6] != null ? ((Number) row[6]).intValue() : 0);
                    stats.setAverageElevationGain(row[7] != null ? roundTo2Decimals(((Number) row[7]).doubleValue()) : 0.0);
                }

                statsList.add(stats);
            } catch (Exception e) {
                log.warn("Error mapping activity stats row", e);
            }
        }

        return statsList;
    }

    private List<MemberStatsDto> mapToMemberStatsDtoSimple(List<Object[]> results) {
        List<MemberStatsDto> list = new ArrayList<>();
        if (results == null || results.isEmpty()) return list;
        for (Object[] row : results) {
            if (row == null || row.length < 5) continue;
            try {
                MemberStatsDto stats = new MemberStatsDto();
                stats.setAthleteName(row[0] != null ? (String) row[0] : "Unknown");
                stats.setTotalActivities(row[1] != null ? ((Number) row[1]).longValue() : 0L);
                stats.setTotalDistance(row[2] != null ? roundTo2Decimals(((Number) row[2]).doubleValue()) : 0.0);
                stats.setTotalMovingTime(row[3] != null ? ((Number) row[3]).intValue() : 0);
                stats.setTotalElevationGain(row[4] != null ? roundTo2Decimals(((Number) row[4]).doubleValue()) : 0.0);
                stats.setActivityTypeBreakdown(new ArrayList<>());
                list.add(stats);
            } catch (Exception e) {
                log.warn("Error mapping member stats row", e);
            }
        }
        return list;
    }

    private List<MemberStatsDto> mapToMemberStatsDto(List<Object[]> results, Instant fromDate, Instant toDate) {
        List<MemberStatsDto> memberStatsList = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return memberStatsList;
        }

        for (Object[] row : results) {
            if (row == null || row.length < 5) {
                continue;
            }
            
            try {
                String athleteName = row[0] != null ? (String) row[0] : "Unknown";
                MemberStatsDto stats = new MemberStatsDto();
                stats.setAthleteName(athleteName);
                stats.setTotalActivities(row[1] != null ? ((Number) row[1]).longValue() : 0L);
                stats.setTotalDistance(row[2] != null ? roundTo2Decimals(((Number) row[2]).doubleValue()) : 0.0);
                stats.setTotalMovingTime(row[3] != null ? ((Number) row[3]).intValue() : 0);
                stats.setTotalElevationGain(row[4] != null ? roundTo2Decimals(((Number) row[4]).doubleValue()) : 0.0);

                // Get activity type breakdown for this member
                try {
                    List<Object[]> activityBreakdown = repository.getStatsByAthleteAndSportType(athleteName, fromDate, toDate);
                    stats.setActivityTypeBreakdown(mapToActivityStatsDto(activityBreakdown));
                } catch (Exception e) {
                    log.warn("Error getting activity breakdown for member: {}", athleteName, e);
                    stats.setActivityTypeBreakdown(new ArrayList<>());
                }

                memberStatsList.add(stats);
            } catch (Exception e) {
                log.warn("Error mapping member stats row", e);
            }
        }

        return memberStatsList;
    }

    private double roundTo2Decimals(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
