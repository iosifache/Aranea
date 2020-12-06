package com.aranea;

import java.io.File;

public class FolderParser {


    public FolderParser() { };
    public void listFilesForFolder( final File folder) {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                System.out.println(fileEntry.getName());
            }
        }
    }
}
