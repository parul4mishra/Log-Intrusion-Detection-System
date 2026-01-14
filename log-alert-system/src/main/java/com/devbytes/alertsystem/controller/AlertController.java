package com.devbytes.alertsystem.controller;

import com.devbytes.alertsystem.model.AlertEvent;
import com.devbytes.alertsystem.service.AlertService;

import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertEvent> getAlerts() {
        return alertService.getAllAlerts();
    }

    @DeleteMapping
    public void clearAlerts() {
        alertService.clear();
    }
}

