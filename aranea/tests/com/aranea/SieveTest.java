package com.aranea;

import com.aranea.Sieve;
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
        assertNull(Sieve.checkInstance());
        Sieve.getInstance(extensions, 1000000000, "");
        assertNotNull(Sieve.checkInstance());
        assertEquals(Arrays.asList(extensions), Sieve.checkInstance().getallowedExtensions());
        assertEquals(1000000000, Sieve.checkInstance().getmaxSize());
        assertEquals("123", Sieve.checkInstance().getpattern());
    }

    @Test
    void addIgnoredPages() {
        String[] extensions = {"html", "css", "js"};
        Sieve.getInstance(extensions, 1000000000, "");
        assertNotNull(Sieve.checkInstance().getignoredPages());
        String[] pages = {"php"};
        Sieve.checkInstance().addIgnoredPages(pages);
        assertEquals(Arrays.asList(pages), Sieve.checkInstance().getignoredPages());
    }

    @Test
    void checkURL() {
        String url = "http://www.columbia.edu/~fdc/index.html";
        try {
            assertTrue(Sieve.checkInstance().checkURL(url));
        } catch (Sieve.FailedRequestException e) {
            Assertions.fail("There is an error with the URL");
        }
    }

    @Test
    void checkContent() {
        String[] extensions = {"html", "css", "js"};
        Sieve.getInstance(extensions, 1000000000, "");
        try {
            FileInputStream file= new FileInputStream("D:\\test.txt");
            try {
                Sieve.checkInstance().setPattern("123");
                System.out.println(Sieve.checkInstance().getpattern());
                assertFalse(Sieve.checkInstance().checkContent(file));
            } catch (Sieve.FailedFileReadException e) {
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
            Sieve.checkInstance().checkURL(url);
        } catch (Sieve.FailedRequestException e) {
            Assertions.fail("The URL was rejected by FailedRequestException beacuse is invalid!!!");
        }

        //Assertions.fail("The URL is not valid so it needs to be rejected!!!");
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