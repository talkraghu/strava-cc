package com.closecircuit.strava.service;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.closecircuit.strava.config.StravaProperties;
import com.closecircuit.strava.util.TokenPersistenceUtil;

@Service
public class StravaTokenService {

    private static final Logger log = LoggerFactory.getLogger(StravaTokenService.class);

    private final StravaProperties props;
    private final WebClient webClient;
    private final TokenPersistenceUtil tokenPersistence;

    public StravaTokenService(StravaProperties props, TokenPersistenceUtil tokenPersistence) {
        this.props = props;
        this.tokenPersistence = tokenPersistence;
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

    /**
     * Exchange authorization code for access and refresh tokens
     */
    public Map<String, Object> exchangeAuthorizationCode(String code) {
        Map<String, Object> response = webClient.post()
            .uri("/oauth/token")
            .body(BodyInserters.fromFormData("client_id", props.getClientId())
                .with("client_secret", props.getClientSecret())
                .with("code", code)
                .with("grant_type", "authorization_code"))
            .retrieve()
            .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
            .block();

        if (response != null) {
            // Update in-memory properties
            props.getAuth().setAccessToken((String) response.get("access_token"));
            props.getAuth().setRefreshToken((String) response.get("refresh_token"));
            props.getAuth().setExpiresAt(
                ((Number) response.get("expires_at")).longValue()
            );
        }

        return response;
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
        long expiresAt = ((Number) response.get("expires_at")).longValue();
        props.getAuth().setExpiresAt(expiresAt);

        // Persist tokens to application.properties
        tokenPersistence.saveTokens(
            props.getAuth().getAccessToken(),
            props.getAuth().getRefreshToken(),
            expiresAt
        );
    }
}
