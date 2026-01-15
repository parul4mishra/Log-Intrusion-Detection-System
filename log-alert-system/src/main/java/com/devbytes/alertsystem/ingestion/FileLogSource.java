package com.devbytes.alertsystem.ingestion;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileLogSource implements LogSource {
	
	private static final Logger log = LoggerFactory.getLogger(FileLogSource.class);

	 //private final Path logDir = Paths.get("/logs");
	private final Path logDir = Paths.get("logs");


		@Override
		public Stream<String> readLines() throws IOException {

			log.info("log.ingestion.start directory={}", logDir);

			if (!Files.exists(logDir)) {
				log.warn("log.ingestion.dir_missing dir={}", logDir);
				return Stream.empty();
			}

			return Files.list(logDir).flatMap(path -> {
				try {
					log.info("log.ingestion.file_start file={}", path.getFileName());
					return Files.lines(path);
				} catch (IOException e) {
					log.error("log.ingestion.file_failed file={} error={}", path.getFileName(), e.getMessage(), e);
					return Stream.empty();
				}
    	
    	
    });
	    	
	    	
	    }
}
