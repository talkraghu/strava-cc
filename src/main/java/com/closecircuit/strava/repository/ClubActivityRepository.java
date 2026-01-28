package com.closecircuit.strava.repository;

import com.closecircuit.strava.entity.ClubActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ClubActivityRepository
        extends JpaRepository<ClubActivityLog, Long> {

    boolean existsByActivityId(Long activityId);

	boolean existsByNameAndAthleteNameAndDistanceAndMovingTime(String name, String athleteName, Double distance,
			Integer movingTime);

    // Find activities by sport type
    List<ClubActivityLog> findBySportType(String sportType);

    // Find activities by athlete name
    List<ClubActivityLog> findByAthleteName(String athleteName);

    // Find activities by sport type and athlete
    List<ClubActivityLog> findBySportTypeAndAthleteName(String sportType, String athleteName);

    // Get distinct athlete names
    @Query("SELECT DISTINCT a.athleteName FROM ClubActivityLog a")
    List<String> findDistinctAthleteNames();

    // Get distinct sport types
    @Query("SELECT DISTINCT a.sportType FROM ClubActivityLog a")
    List<String> findDistinctSportTypes();

    // Stats by sport type
    @Query("SELECT " +
           "a.sportType as activityType, " +
           "COUNT(a) as totalActivities, " +
           "COALESCE(SUM(a.distance), 0) as totalDistance, " +
           "COALESCE(SUM(a.movingTime), 0) as totalMovingTime, " +
           "COALESCE(SUM(a.elevationGain), 0) as totalElevationGain, " +
           "COALESCE(AVG(a.distance), 0) as averageDistance, " +
           "COALESCE(AVG(a.movingTime), 0) as averageMovingTime, " +
           "COALESCE(AVG(a.elevationGain), 0) as averageElevationGain " +
           "FROM ClubActivityLog a " +
           "WHERE (:sportType IS NULL OR a.sportType = :sportType) " +
           "AND (:fromDate IS NULL OR a.collectedAt >= :fromDate) " +
           "AND (:toDate IS NULL OR a.collectedAt <= :toDate) " +
           "GROUP BY a.sportType")
    List<Object[]> getStatsBySportType(@Param("sportType") String sportType,
                                       @Param("fromDate") Instant fromDate,
                                       @Param("toDate") Instant toDate);

    // Stats by athlete
    @Query("SELECT " +
           "a.athleteName, " +
           "COUNT(a) as totalActivities, " +
           "COALESCE(SUM(a.distance), 0) as totalDistance, " +
           "COALESCE(SUM(a.movingTime), 0) as totalMovingTime, " +
           "COALESCE(SUM(a.elevationGain), 0) as totalElevationGain " +
           "FROM ClubActivityLog a " +
           "WHERE (:athleteName IS NULL OR a.athleteName = :athleteName) " +
           "AND (:fromDate IS NULL OR a.collectedAt >= :fromDate) " +
           "AND (:toDate IS NULL OR a.collectedAt <= :toDate) " +
           "GROUP BY a.athleteName " +
           "ORDER BY totalDistance DESC")
    List<Object[]> getStatsByAthlete(@Param("athleteName") String athleteName,
                                     @Param("fromDate") Instant fromDate,
                                     @Param("toDate") Instant toDate);

    // Stats by athlete and sport type
    @Query("SELECT " +
           "a.sportType as activityType, " +
           "COUNT(a) as totalActivities, " +
           "COALESCE(SUM(a.distance), 0) as totalDistance, " +
           "COALESCE(SUM(a.movingTime), 0) as totalMovingTime, " +
           "COALESCE(SUM(a.elevationGain), 0) as totalElevationGain, " +
           "COALESCE(AVG(a.distance), 0) as averageDistance, " +
           "COALESCE(AVG(a.movingTime), 0) as averageMovingTime, " +
           "COALESCE(AVG(a.elevationGain), 0) as averageElevationGain " +
           "FROM ClubActivityLog a " +
           "WHERE a.athleteName = :athleteName " +
           "AND (:fromDate IS NULL OR a.collectedAt >= :fromDate) " +
           "AND (:toDate IS NULL OR a.collectedAt <= :toDate) " +
           "GROUP BY a.sportType")
    List<Object[]> getStatsByAthleteAndSportType(@Param("athleteName") String athleteName,
                                                   @Param("fromDate") Instant fromDate,
                                                   @Param("toDate") Instant toDate);

    // Overall club stats
    @Query("SELECT " +
           "COUNT(DISTINCT a.athleteName) as totalMembers, " +
           "COUNT(a) as totalActivities, " +
           "COALESCE(SUM(a.distance), 0) as totalDistance, " +
           "COALESCE(SUM(a.movingTime), 0) as totalMovingTime, " +
           "COALESCE(SUM(a.elevationGain), 0) as totalElevationGain " +
           "FROM ClubActivityLog a " +
           "WHERE (:fromDate IS NULL OR a.collectedAt >= :fromDate) " +
           "AND (:toDate IS NULL OR a.collectedAt <= :toDate)")
    Object[] getOverallStats(@Param("fromDate") Instant fromDate,
                             @Param("toDate") Instant toDate);
}
