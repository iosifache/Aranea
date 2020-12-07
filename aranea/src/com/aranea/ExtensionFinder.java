package com.aranea;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ExtensionFinder extends FolderParser {

    private final String searchedExtension;

    public ExtensionFinder(String folderPath, String searchedExtension) {
        super(folderPath);
        this.searchedExtension = searchedExtension;
    }


    @Override
    public void processFile() throws IOException {
        Files.walk(this.getFolderPath())          // Recursively walk through the working directory
                .filter(this::endsWithSearchedExtension)   // Filter them by the searched extension
                .forEach(System.out::println);             // Print the filtered collection
    }

    private boolean endsWithSearchedExtension(Path path) {
        return path.toString().trim().endsWith(searchedExtension);
    }

}
