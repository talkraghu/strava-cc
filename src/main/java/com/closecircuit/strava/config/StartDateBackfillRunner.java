package com.closecircuit.strava.config;

import com.closecircuit.strava.repository.ClubActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Order(1)
public class StartDateBackfillRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(StartDateBackfillRunner.class);

    private final ClubActivityRepository repository;

    public StartDateBackfillRunner(ClubActivityRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        try {
            int updated = repository.backfillStartDateFromCollectedAt();
            if (updated > 0) {
                log.info("Backfilled start_date for {} existing activity rows (set to collected_at)", updated);
            }
        } catch (Exception e) {
            log.warn("Start-date backfill skipped or failed (e.g. column already populated): {}", e.getMessage());
        }
    }
}
