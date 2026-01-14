package com.devbytes.alertsystem.detection;

import java.util.Optional;

import com.devbytes.alertsystem.model.AlertEvent;
import com.devbytes.alertsystem.model.LogEvent;

public interface DetectionRules {
	
	Optional<AlertEvent> evaluate(LogEvent event);

}
