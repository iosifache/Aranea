package com.aranea;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import com.aranea.AraneaException.CannotOpenException;
import com.aranea.AraneaException.CannotWriteException;

public class SitemapGenerator extends FolderParser {

    private final Path outputFile;
    private Map<String, List<String>> systemMap;

    /**
     * @param folderPath
     * @param outputFileName
     */
    public SitemapGenerator(String folderPath, String outputFileName) {
        super(folderPath);
        this.outputFile = Paths.get(outputFileName);
        systemMap = new HashMap<>();
    }

    /**
     * @throws AraneaException
     */
    @Override
    public void parse() throws AraneaException {
        try {
            Files.walk(this.getFolderPath())          // Recursively walk through the working directory
                    .forEach(this::processFile);
            printToOutputFile();
        } catch (Exception e) {
            throw new CannotOpenException();
        }

    }

    /**
     * @param path
     * @return
     */
    protected boolean processFile(Path path) {
        File currentFile = path.toFile();
        if (currentFile.isDirectory())
            systemMap.put(currentFile.getName(), fetchDirFiles(currentFile));
        return true;
    }

    /**
     * @param currentFile
     * @return
     */
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

    /**
     * @throws CannotWriteException
     */
    private void printToOutputFile() throws CannotWriteException {
        byte[] desiredMap = {};
        try {
            Files.createFile(outputFile);
        } catch (Exception e) {
            ;
        }
        systemMap.forEach((dirName, dirContent) -> {
            try {
                printDirName(dirName);
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }
            try {
                printDirFiles(dirContent);
            } catch (CannotWriteException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @param dirName
     * @throws CannotWriteException
     */
    private void printDirName(String dirName) throws CannotWriteException {
        byte[] formattedDirName = String.format("%s\n", dirName).getBytes();
        try {
            Files.write(outputFile, formattedDirName, StandardOpenOption.APPEND);
        } catch (Exception e) {
            throw new CannotWriteException();
        }
    }

    /**
     * @param dirContent
     * @throws CannotWriteException
     */
    private void printDirFiles(List<String> dirContent) throws CannotWriteException {
        dirContent.forEach(fileName -> {
            byte[] formattedFileName = String.format("\t%s\n", fileName).getBytes();
            try {
                Files.write(outputFile, formattedFileName, StandardOpenOption.APPEND);
            } catch (Exception e) {
                try {
                    throw new CannotWriteException();
                } catch (CannotWriteException cannotWriteException) {
                    cannotWriteException.printStackTrace();
                }
            }
        });
    }
}
