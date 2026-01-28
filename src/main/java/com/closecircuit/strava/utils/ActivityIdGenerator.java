package com.closecircuit.strava.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class ActivityIdGenerator {

    private ActivityIdGenerator() {}

    public static long generate(
            String name,
            String type,
            String sportType,
            Double distance,
            Integer movingTime,
            Integer elapsedTime,
            Double elevationGain,
            String deviceName
    ) {
        try {
            String source = String.join("|",
                    safe(name),
                    safe(type),
                    safe(sportType),
                    String.valueOf(distance),
                    String.valueOf(movingTime),
                    String.valueOf(elapsedTime),
                    String.valueOf(elevationGain),
                    safe(deviceName)
            );

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(source.getBytes(StandardCharsets.UTF_8));

            // Use first 8 bytes → long
            long value = 0;
            for (int i = 0; i < 8; i++) {
                value = (value << 8) | (hash[i] & 0xff);
            }

            // Ensure positive
            return Math.abs(value);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Unable to generate activityId", e);
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim().toLowerCase();
    }
}
