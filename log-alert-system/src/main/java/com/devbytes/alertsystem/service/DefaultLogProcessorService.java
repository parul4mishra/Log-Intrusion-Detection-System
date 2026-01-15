package com.devbytes.alertsystem.service;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.detection.DetectionService;
import com.devbytes.alertsystem.ingestion.LogSource;
import com.devbytes.alertsystem.model.LogEvent;
import com.devbytes.alertsystem.parser.LogParser;

@Component
public class DefaultLogProcessorService implements LogProcessingService {
	
	private static final Logger log = LoggerFactory.getLogger(DefaultLogProcessorService.class);

	private final LogSource logSource;
    private final LogParser logParser;
    private final DetectionService detectionService;

	public DefaultLogProcessorService(LogSource logSource, LogParser logParser, DetectionService detectionService) {
		this.logSource = logSource;
		this.logParser = logParser;
		this.detectionService = detectionService;
	}

	@EventListener(ApplicationReadyEvent.class)
    @Override
    public void processLogs() throws IOException {
        logSource.readLines()
                 .forEach(this::processLine);
    }

    private void processLine(String rawLine) {
           
        try {
        	rawLine = rawLine.replace("\uFEFF", "");
            if (!logParser.isParseble(rawLine)) {
            	log.debug("log.processor.line.skipped raw='{}' length={}", rawLine, rawLine.length());
                return;
            }

            LogEvent event = logParser.parse(rawLine);
            String traceId = event.getAttributes().get("traceId");
            detectionService.applyRules(event);

        } catch (Exception e) {
        	log.error(
                    "log.processor.line.failed rawLine='{}' error={}",
                    rawLine,
                    e.getMessage(),
                    e
                );
        }
    }
	
	

}
