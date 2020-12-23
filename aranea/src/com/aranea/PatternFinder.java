package com.aranea;

import java.nio.file.Files;
import java.nio.file.Path;
import com.aranea.AraneaException.CannotOpenException;
import java.util.regex.Pattern;

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
            throw new CannotOpenException();
        }

    }

    /**
     * @param path
     * @return
     */
    protected boolean processFile(Path path) {
        try {
            Pattern patternObject = Pattern.compile(this.pattern, Pattern.CASE_INSENSITIVE);
            return Files.readAllLines(path)
                    .stream()
                    .anyMatch(line -> patternObject.matcher(line).find());
        } catch (Exception e) {
            return false;
        }
    }
}
