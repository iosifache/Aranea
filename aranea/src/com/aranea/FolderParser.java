package com.aranea;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FolderParser {

    private final Path folderPath;

    public FolderParser(String folderPath) {
        this.folderPath = Paths.get(folderPath);
    }

    protected abstract boolean processFile(Path path);

    public abstract void parse() throws CannotOpenException;

    public Path getFolderPath() {
        return folderPath;
    }

    public class CannotOpenException extends AraneaException {
        public CannotOpenException() {
            super(AraneaLogger.AraneaLoggerLevels.ERROR, "The File cannot be opened.");
        }
    }

}
