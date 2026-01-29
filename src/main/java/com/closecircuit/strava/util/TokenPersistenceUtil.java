package com.closecircuit.strava.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

@Component
public class TokenPersistenceUtil {

    private static final Logger log = LoggerFactory.getLogger(TokenPersistenceUtil.class);
    private static final String PROPERTIES_FILE = "src/main/resources/application.properties";

    /**
     * Save tokens to application.properties file
     */
    public boolean saveTokens(String accessToken, String refreshToken, long expiresAt) {
        try {
            // Try to find the properties file in the project directory
            Path path = Paths.get(PROPERTIES_FILE);
            
            // If not found in project root, try to find it via classpath
            if (!Files.exists(path)) {
                try {
                    Resource resource = new ClassPathResource("application.properties");
                    if (resource.exists()) {
                        // Get the actual file path from the resource
                        File file = resource.getFile();
                        path = file.toPath();
                    } else {
                        log.warn("application.properties not found. Tokens will not be persisted.");
                        return false;
                    }
                } catch (Exception e) {
                    log.warn("Could not locate application.properties file: {}", e.getMessage());
                    return false;
                }
            }

            // Read existing properties
            Properties props = new Properties();
            try (InputStream input = Files.newInputStream(path)) {
                props.load(input);
            }

            // Update token values
            props.setProperty("strava.auth.access-token", accessToken);
            props.setProperty("strava.auth.refresh-token", refreshToken);
            props.setProperty("strava.auth.expires-at", String.valueOf(expiresAt));

            // Write back
            try (OutputStream output = Files.newOutputStream(path)) {
                props.store(output, "Updated by OAuth flow - " + java.time.LocalDateTime.now());
            }

            log.info("Tokens successfully saved to application.properties");
            return true;
        } catch (Exception e) {
            log.error("Failed to persist tokens to application.properties", e);
            return false;
        }
    }
}
