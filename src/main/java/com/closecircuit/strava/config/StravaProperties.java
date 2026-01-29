package com.closecircuit.strava.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "strava")
public class StravaProperties {

    /* ===== Root properties ===== */

    private String baseUrl;
    private Long clubId;

    /** OAuth client config */
    private String clientId;
    private String clientSecret;
    private String redirectUri;

    /** OAuth token state */
    private Auth auth = new Auth();

    /** Fetch behavior */
    private Fetch fetch = new Fetch();

    /* ===== Root Getters & Setters ===== */

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Long getClubId() {
        return clubId;
    }

    public void setClubId(Long clubId) {
        this.clubId = clubId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public void setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
    }

    public Auth getAuth() {
        return auth;
    }

    public void setAuth(Auth auth) {
        this.auth = auth;
    }

    public Fetch getFetch() {
        return fetch;
    }

    public void setFetch(Fetch fetch) {
        this.fetch = fetch;
    }

    /* ===================================================== */
    /* =================== Nested Classes ================== */
    /* ===================================================== */

    /**
     * OAuth token lifecycle state
     */
    public static class Auth {

        /** Current access token */
        private String accessToken;

        /** Refresh token (long lived) */
        private String refreshToken;

        /** Expiry time (epoch seconds) */
        private long expiresAt;

        public String getAccessToken() {
            return accessToken;
        }

        public void setAccessToken(String accessToken) {
            this.accessToken = accessToken;
        }

        public String getRefreshToken() {
            return refreshToken;
        }

        public void setRefreshToken(String refreshToken) {
            this.refreshToken = refreshToken;
        }

        public long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
        }
    }

    /**
     * Fetch behavior configuration
     */
    public static class Fetch {

        /**
         * Strava API page size (max allowed by Strava = 200)
         */
        private int perPage = 50;

        /**
         * Maximum pages to fetch (safety cap)
         */
        private int maxPages = 25;

        /**
         * Maximum total records per poll
         */
        private int maxRecords = 5000;

        /**
         * Poll interval in milliseconds
         */
        private long pollIntervalMs = 600_000;

        public int getPerPage() {
            return perPage;
        }

        public void setPerPage(int perPage) {
            this.perPage = perPage;
        }

        public int getMaxPages() {
            return maxPages;
        }

        public void setMaxPages(int maxPages) {
            this.maxPages = maxPages;
        }

        public int getMaxRecords() {
            return maxRecords;
        }

        public void setMaxRecords(int maxRecords) {
            this.maxRecords = maxRecords;
        }

        public long getPollIntervalMs() {
            return pollIntervalMs;
        }

        public void setPollIntervalMs(long pollIntervalMs) {
            this.pollIntervalMs = pollIntervalMs;
        }
    }

}
