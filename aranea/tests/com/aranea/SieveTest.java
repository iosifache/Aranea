package com.aranea;

import com.aranea.AraneaException.FailedFileReadException;
import com.aranea.AraneaException.FailedRequestException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SieveTest {

    @Test
    void getInstance() {
        String[] extensions = {"html", "css", "js"};
        assertNull(Sieve.getInstance(extensions, 1000000000, ""));
        Sieve.getInstance(extensions, 1000000000, "");
        assertNotNull(Sieve.getInstance());
    }

    @Test
    void addIgnoredPages() {
        String[] extensions = {"html", "css", "js"};
        Sieve.getInstance(extensions, 1000000000, "");
        String[] pages = {"php"};
        Sieve.getInstance().addIgnoredPages(pages);
    }

    @Test
    void checkURL() {
        String url = "http://www.columbia.edu/~fdc/index.html";
        try {
            assertTrue(Sieve.getInstance().checkURL(url));
        } catch (FailedRequestException e) {
            Assertions.fail("There is an error with the URL");
        }
    }

    @Test
    void checkContent() {
        String[] extensions = {"html", "css", "js"};
        Sieve.getInstance(extensions, 1000000000, "123");
        try {
            FileInputStream file= new FileInputStream("D:\\test.txt");
            try {
                assertFalse(Sieve.getInstance().checkContent(file));
            } catch (FailedFileReadException e) {
                Assertions.fail("There is an error with the URL");
            }
        } catch (FileNotFoundException e) {
            Assertions.fail("There is an error while opening the file!!!");
        }

    }

    @Test
    void testcheckURLexceptions() {
        String url = "http://www.combia.edu/~fdc/index.html";
        String[] extensions = {"html", "css", "js"};
        Sieve.getInstance(extensions, 1000000000, "1234");
        try {
            Sieve.getInstance().checkURL(url);
        } catch (FailedRequestException e) {
            Assertions.fail("The URL was rejected by FailedRequestException beacuse is invalid!!!");
        }
    }

    @Test
    void testcheckContentexceptions() {
        String[] extensions = {"html", "css", "js"};
        Sieve.getInstance(extensions, 1000000000, "1234");
        try {
            FileInputStream file= new FileInputStream("D:\\test2.txt");
        } catch (FileNotFoundException e) {
            Assertions.fail("The file need to exist to check the content!!!");
        }
    }
}