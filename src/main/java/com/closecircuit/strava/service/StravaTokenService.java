package com.closecircuit.strava.service;

import java.time.Instant;
import java.util.Map;

import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.closecircuit.strava.config.StravaProperties;

import ch.qos.logback.classic.Logger;

@Service
public class StravaTokenService {


    private final StravaProperties props;
    private final WebClient webClient;

    public StravaTokenService(StravaProperties props) {
        this.props = props;
        this.webClient = WebClient.create("https://www.strava.com");
    }

    public synchronized String getValidAccessToken() {

        long now = Instant.now().getEpochSecond();

        // refresh 1 minute before expiry
        if (props.getAuth().getExpiresAt() - 60 > now) {
            return props.getAuth().getAccessToken();
        }

        refreshToken();
        return props.getAuth().getAccessToken();
    }

    private void refreshToken() {


        Map<String, Object> response = webClient.post()
            .uri("/oauth/token")
            .body(BodyInserters.fromFormData("client_id", props.getClientId())
                .with("client_secret", props.getClientSecret())
                .with("grant_type", "refresh_token")
                .with("refresh_token", props.getAuth().getRefreshToken()))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        props.getAuth().setAccessToken((String) response.get("access_token"));
        props.getAuth().setRefreshToken((String) response.get("refresh_token"));
        props.getAuth().setExpiresAt(
            ((Number) response.get("expires_at")).longValue()
        );

        
        // TODO: Persist to DB / file (next step)
    }
}
