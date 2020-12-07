package com.aranea;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class SitemapGenerator extends FolderParser {

    private final Path outputFile;
    private Map<String, List<String>> systemMap;

    public SitemapGenerator(String folderPath, String outputFileName) {
        super(folderPath);
        this.outputFile = Paths.get(outputFileName);
        systemMap = new HashMap<>();
    }

    @Override
    public void processFile() throws IOException {
        Files.walk(this.getFolderPath())          // Recursively walk through the working directory
                .forEach(this::registerDirToSystemMap);
        printToOutputFile();
    }

    private void registerDirToSystemMap(Path path) {
        File currentFile = path.toFile();
        if (currentFile.isDirectory())
            systemMap.put(currentFile.getName(), fetchDirFiles(currentFile));
    }

    private List<String> fetchDirFiles(File currentFile) {
        File[] files = currentFile.listFiles();
        List<String> dirFileNames = new ArrayList<>();
        boolean isEmptyDir = files == null || files.length < 1;
        if (!isEmptyDir) {
            for (File file : files) {
                dirFileNames.add(file.getName());
            }
        } else {
            dirFileNames.add("Empty directory");
        }
        return dirFileNames;
    }

    private void printToOutputFile() {
        byte[] desiredMap = "The desired map".getBytes();
        try {
            Files.write(outputFile, desiredMap, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            System.out.println(String.format("There was a problem while truncating file %s", outputFile.getFileName()));
        }
        systemMap.forEach((dirName, dirContent) -> {
            printDirName(dirName);
            printDirFiles(dirContent);
        });
    }

    private void printDirName(String dirName) {
        byte[] formattedDirName = String.format("%s\n", dirName).getBytes();
        try {
            Files.write(outputFile, formattedDirName, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.out.println(String.format("There was a problem while writing Directory %s content", dirName));
        }
    }

    private void printDirFiles(List<String> dirContent) {
        dirContent.forEach(fileName -> {
            byte[] formattedFileName = String.format("\t%s\n", fileName).getBytes();
            try {
                Files.write(outputFile, formattedFileName, StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println(String.format("There was a problem while writing File %s", fileName));
            }
        });
    }
}
