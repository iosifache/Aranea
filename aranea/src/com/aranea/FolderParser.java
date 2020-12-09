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

    protected abstract void parse();

    public Path getFolderPath() {
        return folderPath;
    }

    public class CannotOpenException extends AraneaException {
        public CannotOpenException() {
            super(AraneaLogger.AraneaLoggerLevels.ERROR, "The File cannot be opened.");
        }
    }

}
