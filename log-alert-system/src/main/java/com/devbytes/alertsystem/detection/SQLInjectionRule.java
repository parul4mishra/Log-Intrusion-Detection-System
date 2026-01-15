package com.devbytes.alertsystem.detection;

import com.devbytes.alertsystem.model.*;
import com.devbytes.alertsystem.service.AlertService;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

@Component
public class SQLInjectionRule implements DetectionRules {

    private final AlertService alertService;

    private static final List<String> SQLI_PATTERNS = List.of(
            "' OR 1=1",
            "UNION SELECT",
            "--",
            "/*",
            "*/",
            "DROP TABLE"
    );

    public SQLInjectionRule(AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public void evaluate(LogEvent event) {

        if (!isRelevant(event)) {
            return;
        }

        String query = event.getAttributes().get("query");
        if (query == null) {
            return;
        }

        for (String pattern : SQLI_PATTERNS) {
            if (query.toUpperCase().contains(pattern)) {
                raiseAlert(event, pattern);
                break;
            }
        }
    }

    private boolean isRelevant(LogEvent event) {

        Map<String, String> attrs = event.getAttributes();

        return "SQL_ERROR".equals(attrs.get("event"))
                || event.getMessage().toLowerCase().contains("query failed");
    }

    private void raiseAlert(LogEvent event, String pattern) {

        String ip = event.getAttributes().getOrDefault("ip", "unknown");

        Actor actor = new Actor("IP", ip);

        Target target = new Target(
                event.getSource() != null
                        ? event.getSource()
                        : "unknown-service"
        );

        String description =
                "Possible SQL injection detected. Suspicious pattern: " + pattern;

        AlertEvent alert = new AlertEvent(
                "SQL_INJECTION",
                actor,
                target,
                description,
                System.currentTimeMillis()
        );

        alertService.publish(alert);
    }
}


