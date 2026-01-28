package com.closecircuit.strava.client;

import com.closecircuit.strava.config.StravaProperties;
import com.closecircuit.strava.dto.StravaActivityDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class StravaClient {

    private static final Logger log = LoggerFactory.getLogger(StravaClient.class);

    private final WebClient stravaWebClient;
    private final StravaProperties properties;

    public StravaClient(WebClient stravaWebClient, StravaProperties properties) {
        this.stravaWebClient = stravaWebClient;
        this.properties = properties;
    }

    /**
     * Step 1: Fetch raw data from Strava API
     */
    
    public List<StravaActivityDto> fetchClubActivitiesRaw() {

        Long clubId = properties.getClubId();
        int perPage = Math.min(properties.getFetch().getPerPage(), 200);
        int maxPages = properties.getFetch().getMaxPages();
        int maxRecords = properties.getFetch().getMaxRecords();

        log.info(
            "Fetching Strava club activities (clubId={}, perPage={}, maxPages={}, maxRecords={})",
            clubId, perPage, maxPages, maxRecords
        );

        List<StravaActivityDto> result = new ArrayList<>();

        try {
            for (int page = 1; page <= maxPages; page++) {
            	final int finalPage = page;

                if (result.size() >= maxRecords) {
                    log.info("Reached maxRecords={}", maxRecords);
                    break;
                }

                log.debug("Fetching page {}", page);

                List<Map<String, Object>> pageData = stravaWebClient
                        .get()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path("/clubs/{id}/activities")
                                        .queryParam("per_page", perPage)
                                        .queryParam("page", finalPage)
                                        .build(clubId)
                        )
                        .retrieve()
                        .bodyToFlux(new ParameterizedTypeReference<Map<String, Object>>() {})
                        .collectList()
                        .block();

                if (pageData == null || pageData.isEmpty()) {
                    log.info("No more data returned at page {}", page);
                    break;
                }

                // 🔹 Convert Map → DTO here
                for (Map<String, Object> raw : pageData) {
                    if (result.size() >= maxRecords) {
                        break;
                    }
                    result.add(mapToDto(raw));
                }

                if (pageData.size() < perPage) {
                    log.info("Last page detected at page {}", page);
                    break;
                }
            }

            log.info("Fetched total {} activities", result.size());
            return result;

        } catch (WebClientResponseException ex) {
            log.error(
                "Strava API error: status={}, body={}",
                ex.getStatusCode(),
                ex.getResponseBodyAsString()
            );
            throw ex;
        }
    }
    
    @SuppressWarnings("unchecked")
    private StravaActivityDto mapToDto(Map<String, Object> raw) {

        StravaActivityDto dto = new StravaActivityDto();

        dto.setName((String) raw.get("name"));
        dto.setType((String) raw.get("type"));
        dto.setSportType((String) raw.get("sport_type"));

        if (raw.get("distance") instanceof Number n) {
            dto.setDistance(n.doubleValue());
        }

        if (raw.get("moving_time") instanceof Number n) {
            dto.setMovingTime(n.intValue());
        }

        if (raw.get("elapsed_time") instanceof Number n) {
            dto.setElapsedTime(n.intValue());
        }

        if (raw.get("total_elevation_gain") instanceof Number n) {
            dto.setTotalElevationGain(n.doubleValue());
        }

        dto.setDeviceName((String) raw.get("device_name"));

        Map<String, Object> athleteMap = (Map<String, Object>) raw.get("athlete");
        if (athleteMap != null) {
            StravaActivityDto.Athlete athlete = new StravaActivityDto.Athlete();
            athlete.setFirstname((String) athleteMap.get("firstname"));
            athlete.setLastname((String) athleteMap.get("lastname"));
            dto.setAthlete(athlete);
        }

        return dto;
    }


    /**
     * Step 2: Map raw data to DTO
     */
    @SuppressWarnings("unchecked")
    public List<StravaActivityDto> mapRawToDto(List<Map> rawActivities) {
        if (rawActivities == null || rawActivities.isEmpty()) {
            return List.of();
        }

        return rawActivities.stream()
                .map(raw -> {
                    StravaActivityDto dto = new StravaActivityDto();

                    dto.setName((String) raw.get("name"));
                    dto.setType((String) raw.get("type"));
                    dto.setSportType((String) raw.get("sport_type"));
                    dto.setDistance(raw.get("distance") instanceof Number ? ((Number) raw.get("distance")).doubleValue() : null);
                    dto.setMovingTime(raw.get("moving_time") instanceof Number ? ((Number) raw.get("moving_time")).intValue() : null);
                    dto.setElapsedTime(raw.get("elapsed_time") instanceof Number ? ((Number) raw.get("elapsed_time")).intValue() : null);
                    dto.setTotalElevationGain(raw.get("total_elevation_gain") instanceof Number ? ((Number) raw.get("total_elevation_gain")).doubleValue() : null);

                    dto.setDeviceName((String) raw.get("device_name"));

                    Map<String, Object> athleteMap = (Map<String, Object>) raw.get("athlete");
                    if (athleteMap != null) {
                        StravaActivityDto.Athlete athlete = new StravaActivityDto.Athlete();
                        athlete.setFirstname((String) athleteMap.get("firstname"));
                        athlete.setLastname((String) athleteMap.get("lastname"));
                        dto.setAthlete(athlete);
                    }

                    return dto;
                })
                .collect(Collectors.toList());
    }
}
