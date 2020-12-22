package com.aranea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class PatternFinderTest {

    @Test
    void PatternFinder() {
        PatternFinder parr = new PatternFinder("D:\\fdc@columbia.edu_files","a instanceof D&&a.constructor");
        assertEquals(Paths.get("D:\\fdc@columbia.edu_files"), parr.getFolderPath());
    }
    @Test
    void parse() {
        String correct = "D:\\fdc@columbia.edu_files\\f.txt";
        PatternFinder parr = new PatternFinder("D:\\fdc@columbia.edu_files","a instanceof D&&a.constructor");
        try {
            parr.parse();
            System.out.println(correct);
            return;
        } catch (AraneaException e) {
            Assertions.fail("Error caught!!!");
        }
        Assertions.fail("The result is incorrect");
    }

    @Test
    void processFile() {
        PatternFinder parr = new PatternFinder("D:\\fdc@columbia.edu_files\\testpatt\\f(1).txt","{var e=k;break a}}catch(t){");
        assertTrue(parr.processFile(parr.getFolderPath()));
    }
}