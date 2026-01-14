package com.devbytes.alertsystem.ingestion;

import java.io.IOException;
import java.util.stream.Stream;

public interface LogSource {
	
	Stream<String> readLines() throws IOException;

}
