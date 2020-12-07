package com.aranea;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PatternFinder extends FolderParser {

    private final String pattern;

    public PatternFinder(String folderPath, String pattern) {
        super(folderPath);
        this.pattern = pattern;
    }

    @Override
    public void processFile() throws IOException {
        Files.walk(this.getFolderPath())          // Recursively walk through the working directory
                .filter(this::fileFitsPattern)   // Filter them by the searched extension
                .forEach(this::printFileName);             // Print the filtered collection
    }

    private void printFileName(Path path) {
        System.out.println(path.toFile().getName());
    }

    private boolean fileFitsPattern(Path path) {
        try {
            return Files.readAllLines(path)
                    .stream()
                    .anyMatch(line -> line.contains(pattern));
        } catch (IOException e) {
            return false;
        }
    }
}
