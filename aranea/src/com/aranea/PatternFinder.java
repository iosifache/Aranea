package com.aranea;


import com.aranea.FolderParser.CannotOpenException;

import java.nio.file.Files;
import java.nio.file.Path;

public class PatternFinder extends FolderParser {

    private final String pattern;

    /**
     * @param folderPath
     * @param pattern
     */
    public PatternFinder(String folderPath, String pattern) {
        super(folderPath);
        this.pattern = pattern;
    }

    /**
     * @throws AraneaException
     */
    @Override
    public void parse() throws AraneaException {
        try {
            Files.walk(this.getFolderPath())          // Recursively walk through the working directory
                    .filter(this::processFile)   // Filter them by the searched extension
                    .forEach(System.out::println);             // Print the filtered collection
        } catch (Exception e) {
            throw new FolderParser.CannotOpenException();
        }

    }

    /**
     * @param path
     * @return
     */
    protected boolean processFile(Path path) {
        try {
            return Files.readAllLines(path)
                    .stream()
                    .anyMatch(line -> line.contains(pattern));
        } catch (Exception e) {
            return false;
        }
    }
}
