package com.devbytes.alertsystem.detection;

import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.model.AlertEvent;
import com.devbytes.alertsystem.model.LogEvent;
import com.devbytes.alertsystem.model.LogLevel;
import com.devbytes.alertsystem.service.AlertService;

@Component
public class SqlInjectionRule implements DetectionRules {

    private final AlertService alertService;

    public SqlInjectionRule(AlertService alertService) {
        this.alertService = alertService;
    }

    @Override
    public void evaluate(LogEvent event) {

        if (event.getLevel() != LogLevel.ERROR) {
            return;
        }

        String msg = event.getMessage().toLowerCase();

        if (msg.contains("union select") || msg.contains("' or 1=1")) {

            AlertEvent alert = new AlertEvent(
                "SQL_INJECTION",
                event.getSource(),
                "Possible SQL injection pattern detected in logs",
                System.currentTimeMillis()
            );

            alertService.publish(alert);
        }
    }
}

