package com.aranea;

import com.aranea.FolderParser.CannotOpenException;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExtensionFinder extends FolderParser {

    private final String searchedExtension;

    public ExtensionFinder(String folderPath, String searchedExtension) {
        super(folderPath);
        this.searchedExtension = searchedExtension;
    }


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

    protected boolean processFile(Path path) {
        return path.toString().trim().endsWith(searchedExtension);
    }

}
