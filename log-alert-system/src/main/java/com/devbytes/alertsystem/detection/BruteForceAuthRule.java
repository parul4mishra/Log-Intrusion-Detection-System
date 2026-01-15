package com.devbytes.alertsystem.detection;

import com.devbytes.alertsystem.detection.DetectionRules;
import com.devbytes.alertsystem.model.AlertEvent;
import com.devbytes.alertsystem.model.LogEvent;
import com.devbytes.alertsystem.model.LogLevel;
import com.devbytes.alertsystem.service.AlertService;

import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;

@Component
public class BruteForceAuthRule implements DetectionRules {
	
	/**
	 * Brute force authentication detection using
	 * a sliding time window per IP.
	 *
	 */

	
	private final AlertService alertService;
	
	public BruteForceAuthRule(AlertService alertService) {
        this.alertService = alertService;
    }


    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 60_000;

   
    private final Map<String, Deque<Long>> failuresByIp = new ConcurrentHashMap<>();

   
    private final Map<String, Long> lastAlertTimeByIp = new ConcurrentHashMap<>();

    @Override
    public void evaluate(LogEvent event) {

        if (!isRelevant(event)) {
            return;
        }

        String ip = event.getAttributes().get("ip");
        if (ip == null || ip.isBlank()) {
            return;
        }

        long eventTime = event.getTimestamp();

        Deque<Long> attempts =
                failuresByIp.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        attempts.addLast(eventTime);
        cleanupOldAttempts(attempts, eventTime);

        if (attempts.size() >= MAX_ATTEMPTS && shouldAlert(ip, eventTime)) {
            raiseAlert(ip, attempts.size());
            lastAlertTimeByIp.put(ip, eventTime);
        }
    }

    
    private boolean isRelevant(LogEvent event) {

        if (event.getLevel() == LogLevel.DEBUG) {
            return false;
        }

        Map<String, String> attrs = event.getAttributes();

        String eventType = attrs.get("event");
        String statusCode = attrs.get("statusCode");

        return "AUTH_FAILURE".equals(eventType)
                || "401".equals(statusCode)
                || "403".equals(statusCode);
    }

    /**
     * Sliding window cleanup
     */
    private void cleanupOldAttempts(Deque<Long> attempts, long now) {
        while (!attempts.isEmpty() && now - attempts.peekFirst() > WINDOW_MS) {
            attempts.pollFirst();
        }
    }

    /**
     * Ensures we don't spam alerts for the same IP
     */
    private boolean shouldAlert(String ip, long now) {
        Long lastAlertTime = lastAlertTimeByIp.get(ip);
        return lastAlertTime == null || now - lastAlertTime > WINDOW_MS;
    }


    
    private void raiseAlert(String ip, int attemptCount) {
    	
    	String description = attemptCount+" failed login attempts from IP:"+ ip+"  within " +WINDOW_MS / 1000+" seconds. ";

    	AlertEvent alert = new AlertEvent(
    	        "BRUTE_FORCE_AUTH",
    	        ip,
    	        description,
    	        System.currentTimeMillis()
    	    );

        alertService.publish(alert);
    }
}

