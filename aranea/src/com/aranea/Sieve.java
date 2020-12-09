package main.java.arena;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class Sieve {

    private List<String> ignoredPages;
    private List<String> allowedExtensions;
    private String pattern;
    private Path pathToRobotsFile;
    private long maxSize;

    public Sieve(String[] allowedExtensions, String pattern, long maxSize, String pathToRobotsFile) {
        this.allowedExtensions = Arrays.asList(allowedExtensions);
        this.pattern = pattern;
        this.pathToRobotsFile = Paths.get(pathToRobotsFile);
        this.maxSize = maxSize;
        ignoredPages = new ArrayList<>();
    }

    private void addIgnoredPages(String pageToIgnore) {
        ignoredPages.add(pageToIgnore);
    }

    private boolean checkURL(String url) {
        File fileToCheck = Paths.get(url).toFile();
        String fileName = fileToCheck.getName();

        boolean fileHasRightExtension = fileHasRightExtension(fileName);

        boolean fileHasToBeIgnored = fileHasToBeIgnored(fileName);

        boolean fileHasAllowedSize = fileToCheck.length() < maxSize;

        return fileHasAllowedSize && fileHasRightExtension && !fileHasToBeIgnored;
    }

    private boolean fileHasRightExtension(String fileName) {
        return getFileExtension(fileName).filter(extension -> allowedExtensions.contains(extension))
                .isPresent();
    }

    private boolean fileHasToBeIgnored(String fileName) {
        try {
            return Files.readAllLines(pathToRobotsFile)
                    .stream()
                    .anyMatch(line -> line.contains(fileName));
        } catch (IOException e) {
            return false;
        }
    }

    private Optional<String> getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    private boolean checkContent(FileInputStream file) {
        String path;
        try {
            Field field = file.getClass().getDeclaredField("path");
            field.setAccessible(true);
            path = (String) field.get(file);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            System.out.println("There was a problem while checking the content!");
            return false;
        }
        try {
            return Files.readAllLines(Paths.get(path))
                    .stream()
                    .anyMatch(line -> line.contains(pattern));
        } catch (IOException e) {
            return false;
        }
    }
}
