package com.aranea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ExtensionFinderTest {

    @Test
    void ExtensionFinder() {
        ExtensionFinder ext = new ExtensionFinder("D:\\fdc@columbia.edu_files","css");
        assertEquals(Paths.get("D:\\fdc@columbia.edu_files"), ext.getFolderPath());
    }

    @Test
    void parse() {
        String[] str = {"D:\\fdc@columbia.edu_files\\default+en.css", "D:\\fdc@columbia.edu_files\\default.css", "D:\\fdc@columbia.edu_files\\fdc.css", "D:\\fdc@columbia.edu_files\\translateelement.css"};
        ExtensionFinder ext = new ExtensionFinder("D:\\fdc@columbia.edu_files","css");
        try {
            ext.parse();
            System.out.println("\n");
            for (int i=0;i<str.length;i++)
            {
                System.out.println(str[i]);
            }

            return;

        } catch (AraneaException e) {
            Assertions.fail("Exception caught while getting the extensions!!!");
        }
        Assertions.fail("Output differs!!!");
    }

    @Test
    void processFile() {
        ExtensionFinder ext = new ExtensionFinder("D:\\fdc@columbia.edu_files\\fdc.css","css");
        assertTrue(ext.processFile(ext.getFolderPath()));
    }

}