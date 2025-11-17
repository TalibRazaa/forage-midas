package com.jpmc.midascore;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class FileLoader {

    public String[] loadStrings(String path) {
        try {
            ClassPathResource res = new ClassPathResource(path);
            return Files.readAllLines(res.getFile().toPath()).toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load file: " + path, e);
        }
    }
}
