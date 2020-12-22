package com.aranea;

import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class FolderParser {

    private final Path folderPath;

    /**
     * @param folderPath
     */
    public FolderParser(String folderPath) {
        this.folderPath = Paths.get(folderPath);
    }

    /**
     * @param path
     * @return
     */
    protected abstract boolean processFile(Path path);

    /**
     * @throws AraneaException
     */
    public abstract void parse() throws AraneaException;

    /**
     * @return
     */
    public Path getFolderPath() {
        return folderPath;
    }

    public class CannotOpenException extends AraneaException {
        public CannotOpenException() {
            super(AraneaLogger.AraneaLoggerLevels.ERROR, "The File cannot be opened.");
        }
    }

}
