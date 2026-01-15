package com.devbytes.alertsystem.parser;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.model.LogEvent;
import com.devbytes.alertsystem.model.LogLevel;

@Component
public class DefaultFormatLogParser implements LogParser {
	
	 private static final Logger log = LoggerFactory.getLogger(DefaultFormatLogParser.class);
	
		@Override
		public boolean isParseble(String rawLine) {
			if (rawLine == null || rawLine.isBlank()) {
				return false;
			}
			//return rawLine.matches("^\\d{4}-\\d{2}-\\d{2}T.*");
		    return true;

		}
	

	@Override
	public LogEvent parse(String rawLine) {

		log.debug("log.parser.parse.start rawLine='{}'", rawLine);

		try {
			String[] parts = rawLine.split(" ", 4);

			long timestamp = Instant.parse(parts[0]).toEpochMilli();
			LogLevel level = LogLevel.valueOf(parts[1]);
			String source = parts[2];

			String rest = parts.length > 3 ? parts[3] : "";
			Map<String, String> attributes = new HashMap<>();

			String traceId = UUID.randomUUID().toString();
			attributes.put("traceId", traceId);

			String message = extractMessageAndAttributes(rest, attributes);
			log.debug("log.parser.parse.success traceId={} level={} message={}", traceId, level, message);

			return new LogEvent(level, message, timestamp, source, attributes);
			
		} catch (Exception e) {
			log.warn("log.parser.parse.failed rawLine='{}' error={}", rawLine, e.getMessage());
			return fallbackEvent(rawLine);
		}
	}

	private LogEvent fallbackEvent(String rawLine) {
		 return new LogEvent(
                    LogLevel.UNKNOWN,
                    rawLine,
                    Instant.now().toEpochMilli(),
                    "unknown",
                    Map.of("traceId", UUID.randomUUID().toString())
                );
	}

	private String extractMessageAndAttributes(String text, Map<String, String> attributes) {
		StringBuilder msg = new StringBuilder();

		for (String token : text.split(" ")) {
			if (token.contains("=")) {
				String[] kv = token.split("=");
				attributes.put(kv[0], kv[1]);
			} else {
				msg.append(token).append(" ");
			}
		}

		return msg.toString().trim();
	}
	
	


}
