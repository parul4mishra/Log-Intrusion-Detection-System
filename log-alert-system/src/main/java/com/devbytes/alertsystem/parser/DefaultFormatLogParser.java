package com.devbytes.alertsystem.parser;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Component;

import com.devbytes.alertsystem.model.LogEvent;
import com.devbytes.alertsystem.model.LogLevel;

@Component
public class DefaultFormatLogParser implements LogParser {
	
	@Override
	public boolean supports(String rawLine) {
		return true;
	}

	//2026-01-14T12:30:22Z ERROR AUTH_401 Login failed ip=192.168.1.10 user=john
    //192.168.1.10 - - [14/Jan/2026:10:15:32 +0000] "POST /login HTTP/1.1" 401 234
	//{"timestamp":"2026-01-14T10:15:32Z","level":"ERROR","msg":"Login failed","ip":"192.168.1.10"}
	private Map<String, String> attributes = new HashMap<>();


	@Override
	public LogEvent parse(String rawLine) {

		String[] parts = rawLine.split(" ", 4);

		long timestamp = Instant.parse(parts[0]).toEpochMilli();
		LogLevel level = LogLevel.valueOf(parts[1]);

		String rest = parts[3];
		String message = extractMessageAndAttributes(rest, attributes);

		return new LogEvent(level, message, timestamp, "application-log", attributes);
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
	
	public Optional<String> getAttribute(String key) {
	    return Optional.ofNullable(attributes.get(key));
	}


}
