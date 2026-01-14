package com.devbytes.alertsystem.parser;

import com.devbytes.alertsystem.model.LogEvent;

public interface LogParser {
	
	boolean supports(String rawLine);
	 LogEvent parse(String rawLine);

}
