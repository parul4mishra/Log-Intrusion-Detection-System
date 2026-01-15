package com.devbytes.alertsystem.detection;

import com.devbytes.alertsystem.model.LogEvent;

public interface DetectionRules {
	
	void evaluate(LogEvent event);

}
