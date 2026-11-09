package com.revyze.health;

import com.revyze.common.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private final DataSource dataSource;

    @Autowired
    public HealthController(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkHealth() {
        Map<String, Object> healthInfo = new HashMap<>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "revyze-backend");
        healthInfo.put("version", "1.0.0");
        healthInfo.put("timestamp", Instant.now().toString());

        boolean dbConnected = false;
        try (Connection connection = dataSource.getConnection()) {
            dbConnected = connection.isValid(2);
            healthInfo.put("database", dbConnected ? "CONNECTED" : "DISCONNECTED");
        } catch (Exception e) {
            healthInfo.put("database", "ERROR: " + e.getMessage());
        }

        if (dbConnected) {
            return ResponseEntity.ok(ApiResponse.ok("System healthy", healthInfo));
        } else {
            return ResponseEntity.status(503).body(new ApiResponse<>(false, "Database connection failed", healthInfo));
        }
    }
}

