package com.aranea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class URLQueueTest {

    URLQueue q;

    @BeforeEach
    void init() {
        q = URLQueue.getInstance(3);
        assertNotNull(q);
    }

    @Test
    void getInstance() {
        assertNotNull(URLQueue.getInstance(20));
    }

    @Test
    void removeElementFromEmptyList() {
        try {
            q.remove();
        } catch (URLQueue.UninitializedQueueAraneaException e) {
            Assertions.fail("URLQueue is empty");
        }
    }

    @Test
    void removeMoreThanInserted() {
        URL u = null;
        try {
            u = new URL("https://www.test.com");
        } catch (MalformedURLException e) {
            Assertions.fail("URL is not valid");
        }

        for (int i = 0; i < 4; i++) {
            q.add(u);
        }
        try {
            for (int i = 0; i < 5; i++) {
                q.remove();
            }
        } catch (URLQueue.UninitializedQueueAraneaException e) {
            Assertions.fail("Removed Failed");
        }
    }


}