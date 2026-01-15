package com.devbytes.alertsystem.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlertEvent {

	private final String type;
    private final Actor actor;
    private final Target target;
    private final String description;
    private final long timestamp;

}
