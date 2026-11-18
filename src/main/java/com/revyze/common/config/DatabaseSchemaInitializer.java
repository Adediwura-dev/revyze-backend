package com.revyze.common.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DatabaseSchemaInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaInitializer.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaInitializer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Checking database schema compatibility...");

        try {
            // Ensure analysis_status column exists on study_materials
            jdbcTemplate.execute(
                    "ALTER TABLE study_materials ADD COLUMN IF NOT EXISTS analysis_status VARCHAR(30) DEFAULT 'PENDING'"
            );
            log.info("Database schema: verified column analysis_status on study_materials.");
        } catch (Exception e) {
            log.warn("Database schema initialization note (analysis_status column): {}", e.getMessage());
        }

        try {
            // Ensure any legacy rows without analysis_status have default 'PENDING'
            jdbcTemplate.execute(
                    "UPDATE study_materials SET analysis_status = 'PENDING' WHERE analysis_status IS NULL"
            );
            log.info("Database schema: populated default analysis_status for existing rows.");
        } catch (Exception e) {
            log.warn("Database schema initialization note (backfill analysis_status): {}", e.getMessage());
        }
    }
}
