package com.devbytes.alertsystem.ingestion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class FileLogSource implements LogSource {

	 private final Path logDir = Paths.get("/logs");

	    @Override
	    public Stream<String> readLines() throws IOException {
	    	if (!Files.exists(logDir)) 
	    		return Stream.empty();
	    	
	    	return Files.list(logDir).flatMap(path -> {
                try {
                    return Files.lines(path);
                } catch (IOException e) {
                    //throw new RuntimeException("Failed to read log file: " + path, e);
                	return Stream.empty();
                }
            		
    	
    	
    });
	    	
	    	
	    }
}
