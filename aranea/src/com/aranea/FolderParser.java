package com.aranea;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FolderParser {

    private final Path folderPath;

    public FolderParser(String folderPath) {
        this.folderPath = Paths.get(folderPath);
    }

    public abstract void processFile() throws IOException;

    protected void parse() {
        // Unclear what to do here
    }


    public Path getFolderPath() {
        return folderPath;
    }
}
