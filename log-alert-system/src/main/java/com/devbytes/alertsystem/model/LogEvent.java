package com.devbytes.alertsystem.model;

import java.util.Map;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LogEvent {

    private final LogLevel level;
    private final String message;
    private final long timestamp;
    private final String source;
    private final Map<String, String> attributes;
}

