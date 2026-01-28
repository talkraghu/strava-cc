package com.closecircuit.strava.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;


@Configuration
public class StravaClientConfig {

    @Bean
    public WebClient stravaWebClient(StravaProperties properties) {
    	System.out.println("Token: " +  properties.getAuth().getAccessToken());
        return WebClient.builder()
            .baseUrl(properties.getBaseUrl())
            .defaultHeader("Authorization", "Bearer " + properties.getAuth().getAccessToken())
            .build();
    }
}
