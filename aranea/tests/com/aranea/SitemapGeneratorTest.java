package com.aranea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class SitemapGeneratorTest {

    @Test
    void SitemapGenerator() {
        SitemapGenerator site = new SitemapGenerator("D:\\fdc@columbia.edu_files", "D:\\SiteMap");
        assertEquals(Paths.get("D:\\fdc@columbia.edu_files"), site.getFolderPath());
        assertEquals(Paths.get("D:\\SiteMap"), site.getPath());
    }

    @Test
    void parse() {
        SitemapGenerator site = new SitemapGenerator("D:\\fdc@columbia.edu_files", "D:\\SiteMap");
        try {
            site.parse();
            try {
                byte[] ex = Files.readAllBytes(Paths.get("D:\\SiteMapCheck"));
                byte[] got = Files.readAllBytes(Paths.get("D:\\SiteMap"));
                String expected = new String(ex);
                String output = new String(got);
                assertEquals(expected, output);
            } catch (IOException e) {
                Assertions.fail("Error while working with files!!!");
            }

        } catch (AraneaException e) {
            Assertions.fail("Error while working with files!!!");
        }
    }

    @Test
    void CheckCannotWriteException() {
        SitemapGenerator site = new SitemapGenerator("D:\\fdc@columbia.edu_files", "D:\\SiteMap2");
        try {
            site.parse();
        } catch (AraneaException e) {
            Assertions.fail("Error while parsing!!!");
        }
        try {
            site.printToOutputFile();
        } catch (SitemapGenerator.CannotWriteException e) {
            Assertions.fail("Error writing to file!!!");
        }
    }

}