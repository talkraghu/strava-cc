package com.closecircuit.strava.repository;

import com.closecircuit.strava.entity.ClubActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;

public interface ClubActivityRepository
        extends JpaRepository<ClubActivityLog, Long> {

    @Modifying
    @Query(value = "UPDATE club_activity_log SET start_date = collected_at WHERE start_date IS NULL", nativeQuery = true)
    int backfillStartDateFromCollectedAt();

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
    @Query(value = "SELECT " +
           "cal.sport_type, " +
           "COUNT(cal.id), " +
           "COALESCE(SUM(cal.distance), 0), " +
           "COALESCE(SUM(cal.moving_time), 0), " +
           "COALESCE(SUM(cal.elevation_gain), 0), " +
           "COALESCE(AVG(cal.distance), 0), " +
           "COALESCE(AVG(cal.moving_time), 0), " +
           "COALESCE(AVG(cal.elevation_gain), 0) " +
           "FROM club_activity_log cal " +
           "WHERE (:sportType IS NULL OR :sportType = '' OR cal.sport_type = :sportType) " +
           "AND COALESCE(cal.start_date, cal.collected_at) >= :fromDate " +
           "AND COALESCE(cal.start_date, cal.collected_at) <= :toDate " +
           "GROUP BY cal.sport_type", nativeQuery = true)
    List<Object[]> getStatsBySportType(@Param("sportType") String sportType,
                                       @Param("fromDate") Instant fromDate,
                                       @Param("toDate") Instant toDate);

    // Stats by athlete
    @Query(value = "SELECT " +
           "cal.athlete_name, " +
           "COUNT(cal.id), " +
           "COALESCE(SUM(cal.distance), 0), " +
           "COALESCE(SUM(cal.moving_time), 0), " +
           "COALESCE(SUM(cal.elevation_gain), 0) " +
           "FROM club_activity_log cal " +
           "WHERE (:athleteName IS NULL OR :athleteName = '' OR cal.athlete_name = :athleteName) " +
           "AND COALESCE(cal.start_date, cal.collected_at) >= :fromDate " +
           "AND COALESCE(cal.start_date, cal.collected_at) <= :toDate " +
           "GROUP BY cal.athlete_name " +
           "ORDER BY COALESCE(SUM(cal.distance), 0) DESC", nativeQuery = true)
    List<Object[]> getStatsByAthlete(@Param("athleteName") String athleteName,
                                     @Param("fromDate") Instant fromDate,
                                     @Param("toDate") Instant toDate);

    // Stats by athlete and sport type
    @Query(value = "SELECT " +
           "cal.sport_type, " +
           "COUNT(cal.id), " +
           "COALESCE(SUM(cal.distance), 0), " +
           "COALESCE(SUM(cal.moving_time), 0), " +
           "COALESCE(SUM(cal.elevation_gain), 0), " +
           "COALESCE(AVG(cal.distance), 0), " +
           "COALESCE(AVG(cal.moving_time), 0), " +
           "COALESCE(AVG(cal.elevation_gain), 0) " +
           "FROM club_activity_log cal " +
           "WHERE cal.athlete_name = :athleteName " +
           "AND COALESCE(cal.start_date, cal.collected_at) >= :fromDate " +
           "AND COALESCE(cal.start_date, cal.collected_at) <= :toDate " +
           "GROUP BY cal.sport_type", nativeQuery = true)
    List<Object[]> getStatsByAthleteAndSportType(@Param("athleteName") String athleteName,
                                                   @Param("fromDate") Instant fromDate,
                                                   @Param("toDate") Instant toDate);

    // Overall club stats
    @Query(value = "SELECT " +
           "COUNT(DISTINCT cal.athlete_name), " +
           "COUNT(cal.id), " +
           "COALESCE(SUM(cal.distance), 0), " +
           "COALESCE(SUM(cal.moving_time), 0), " +
           "COALESCE(SUM(cal.elevation_gain), 0) " +
           "FROM club_activity_log cal " +
           "WHERE COALESCE(cal.start_date, cal.collected_at) >= :fromDate " +
           "AND COALESCE(cal.start_date, cal.collected_at) <= :toDate", nativeQuery = true)
    Object[] getOverallStats(@Param("fromDate") Instant fromDate,
                             @Param("toDate") Instant toDate);

    // Distance per member for a given activity type (Walk, Ride, Hike, Run)
    @Query(value = "SELECT " +
           "cal.athlete_name, " +
           "COUNT(cal.id), " +
           "COALESCE(SUM(cal.distance), 0), " +
           "COALESCE(SUM(cal.moving_time), 0), " +
           "COALESCE(SUM(cal.elevation_gain), 0) " +
           "FROM club_activity_log cal " +
           "WHERE cal.sport_type = :sportType " +
           "AND COALESCE(cal.start_date, cal.collected_at) >= :fromDate " +
           "AND COALESCE(cal.start_date, cal.collected_at) <= :toDate " +
           "GROUP BY cal.athlete_name " +
           "ORDER BY COALESCE(SUM(cal.distance), 0) DESC", nativeQuery = true)
    List<Object[]> getStatsByAthleteForSportType(@Param("sportType") String sportType,
                                                 @Param("fromDate") Instant fromDate,
                                                 @Param("toDate") Instant toDate);
}
