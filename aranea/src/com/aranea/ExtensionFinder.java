package com.aranea;

import com.aranea.AraneaException.CannotOpenException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExtensionFinder extends FolderParser {

    private final String searchedExtension;

    /**
     * @param folderPath
     * @param searchedExtension
     */
    public ExtensionFinder(String folderPath, String searchedExtension) {
        super(folderPath);
        this.searchedExtension = searchedExtension;
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
        return path.toString().trim().endsWith(searchedExtension);
    }

}
