package com.devbytes.alertsystem.service;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.model.AlertEvent;

@Component
public class AlertService {

    private final List<AlertEvent> alerts = new CopyOnWriteArrayList<>();

    public void publish(AlertEvent alert) {
        alerts.add(alert);
    }

    public List<AlertEvent> getAllAlerts() {
        return List.copyOf(alerts);
    }

    public void clear() {
        alerts.clear();
    }
    
}
