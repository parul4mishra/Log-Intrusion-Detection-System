package com.devbytes.alertsystem.service;

import java.io.IOException;

import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.detection.DetectionService;
import com.devbytes.alertsystem.ingestion.LogSource;
import com.devbytes.alertsystem.model.LogEvent;
import com.devbytes.alertsystem.parser.LogParser;

@Component
public class DefaultLogProcessorService implements LogProcessingService {

	private final LogSource logSource;
    private final LogParser logParser;
    private final DetectionService detectionService;

    public DefaultLogProcessorService(
            LogSource logSource,
            LogParser logParser,
            DetectionService detectionService
    ) {
        this.logSource = logSource;
        this.logParser = logParser;
        this.detectionService = detectionService;
    }

    @Override
    public void processLogs() throws IOException {
        logSource.readLines()
                 .forEach(this::processLine);
    }

    private void processLine(String rawLine) {
        try {
            if (!logParser.supports(rawLine)) {
                return; // skip unsupported format
            }

            LogEvent event = logParser.parse(rawLine);
            detectionService.applyRules(event);

        } catch (Exception e) {
            // Fail-safe: never break the pipeline
            // log and move on
            System.err.println("Failed to process log line: " + rawLine);
        }
    }
	
	

}
