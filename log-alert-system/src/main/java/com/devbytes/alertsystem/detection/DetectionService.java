package com.devbytes.alertsystem.detection;

import java.util.List;

import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.model.LogEvent;

@Component
public class DetectionService {
	
	private final List<DetectionRules> rules;
	
	public DetectionService(List<DetectionRules> rules ) {
        this.rules = rules;
    }


    public void applyRules(LogEvent event) {
        for (DetectionRules rule : rules) {
            rule.evaluate(event);
        }
    }

}
