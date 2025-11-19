package com.jpmc.midascore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Test-scoped FileLoader that returns String[] because several tests expect an array.
 * Annotated with @Component so tests can @Autowired it.
 */
@Component
public class FileLoader {

    private static final Logger logger = LoggerFactory.getLogger(FileLoader.class);

    /**
     * Loads the resource from the classpath and returns its lines as a String[].
     * Returns an empty array if the resource is missing or unreadable.
     */
    public String[] loadStrings(String path) {
        Objects.requireNonNull(path, "path must not be null");

        Resource resource = new ClassPathResource(path);
        if (!resource.exists()) {
            logger.warn("Test resource not found: {}", path);
            return new String[0];
        }

        List<String> lines = new ArrayList<>();
        try (InputStream is = resource.getInputStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {

            String line;
            while ((line = br.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            logger.error("Error reading test resource '{}'", path, e);
            return new String[0];
        }

        return lines.toArray(new String[0]);
    }
}
