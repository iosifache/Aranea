package com.aranea;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CrawlerTest {

    Crawler testCrawler;
    URLQueue q;
    Path path;
    List<String> allPaths;
    int queueLen = 4;
    long waitingTime = 500;
    String mainStr = "10.0.0.1/";
    String saveDir = "../SaveDir/";
    List<URL> urlList, limitURLlist;

    //initialization class
    @BeforeEach
    public void initialization() {
        q = URLQueue.getInstance(queueLen);
        urlList = new ArrayList<URL>();
        limitURLlist = new ArrayList<URL>();
        testCrawler = new Crawler(saveDir, "Aranea", null, 1, waitingTime);
    }

    //TEST getSavePath()
    void insertLimitURLs() {

        URL url1 = null, url2 = null, url3 = null, url4 = null, url5 = null;

        try {
            url1 = new URL("https://withoutWWW.dot/path");
            url2 = new URL("https://www.withoutWWW.dot/path.com");
            url3 = new URL("https://withoutWWW.dot.com/path.com");
            url4 = new URL("https://withoutWWW.dot/");
            url5 = new URL("https://withoutWWW.dot.com/blabalan&?2somerandomasdStuffss");
        } catch (MalformedURLException e) {
            Assertions.fail("URL is not valid");
        }
        limitURLlist.add(url1);
        limitURLlist.add(url2);
        limitURLlist.add(url3);
        limitURLlist.add(url4);
        limitURLlist.add(url5);
    }

    // check if user does not insert save directory with "/"
    @Test
    void getSavePathWithoutBackslashSaveDir() throws AraneaException {
        insertLimitURLs();
        URL url = limitURLlist.get(0);
        assertEquals(saveDir + "withoutWWW.dot/path", Crawler.getSavePath(url));
    }

    //test save path with different urls
    @Test
    void getSavePathMalformedURL() throws AraneaException {
        insertLimitURLs();
        URL e = limitURLlist.get(0);
        assertEquals(saveDir + "withoutWWW.dot/path", Crawler.getSavePath(e));

        e = limitURLlist.get(1);
        assertEquals(saveDir + "withoutWWW.dot/path.com", Crawler.getSavePath(e));

        e = limitURLlist.get(2);
        assertEquals(saveDir + "withoutWWW.dot/path.com", Crawler.getSavePath(e));

        e = limitURLlist.get(3);
        assertEquals(saveDir + "withoutWWW.dot/index.html", Crawler.getSavePath(e));

        e = limitURLlist.get(4);
        assertEquals(saveDir + "withoutWWW.dot/blabalan&", Crawler.getSavePath(e));
    }

    //TEST downloadNextUrl()

    //basic testing with local hosted website
    @Test
    void downloadNextUrlLocalBasic() throws MalformedURLException, AraneaException {

        q.add(new URL("http://10.0.0.1/"));
        testCrawler.run();

        //test if file exists in path
        allPaths = addToList(saveDir, mainStr, true);

        for (String s : allPaths) {
            path = Paths.get(s);
            if (!Files.exists(path)) {
                Assert.fail("Failed to download resource");
            }
        }

    }

    //multithreading testing with local hosted website
    @Test
    void downloadNextUrlLocalMultiThreading() throws MalformedURLException, InterruptedException {

        q.add(new URL("http://10.0.0.1/"));

        for (int i = 0; i < 4; i++) {
            Crawler crawler = new Crawler(saveDir, "CustomUserAgent", null, 4, 0);
            crawler.start();
        }

        //test if file exists in path
        allPaths = addToList(saveDir, mainStr, true);

        for (String s : allPaths) {
            path = Paths.get(s);
            if (!Files.exists(path)) {
                Assert.fail("Failed to download resource");
            }
        }
    }

    //mutithreading test with online website
    @Test
    void downloadNextUrlPublicWebsiteMultiThreading() throws MalformedURLException, InterruptedException {

        q.add(new URL("https://www.w3schools.com/"));

        for (int i = 0; i < 4; i++) {
            Crawler crawler = new Crawler(saveDir, "CustomUserAgent", null, 4, 0);
            crawler.start();
            //sleep in order to wait other threads finsih their job
            Thread.sleep(1000);
        }

        //test if files exist in path
        allPaths = addToList(saveDir, "", false);
        for (String s : allPaths) {
            path = Paths.get(s);
            if (!Files.exists(path)) {
                Assert.fail("Failed to download resource for file: " + path.toString());
            }
        }

    }

    //basic testing with online website
    @Test
    void downloadNextUrlPublicWebsiteBasic() throws MalformedURLException {

        testCrawler = new Crawler(saveDir, "AraneaBot", null, 1, 0);
        q.add(new URL("https://www.w3schools.com/"));

        testCrawler.run();

        //test if file exists in path

        allPaths = addToList(saveDir, "", false);

        for (String s : allPaths) {
            path = Paths.get(s);
            if (!Files.exists(path)) {
                Assert.fail("Failed to download resource");
            }
        }
    }

    //pretty hard to determine, it depens on the internet speed
    @Test
    void downloadNextUrlSleep() throws MalformedURLException {

        q.add(new URL("https://www.google.com/doodles/"));
        q.add(new URL("https://www.google.com/"));
        q.add(new URL("https://kaiowas.biz/RailNationEngineCompare.htm?EN#8H"));
        q.add(new URL("https://www.guru99.com/junit-test-framework.html"));

        long startTime = System.currentTimeMillis();
        testCrawler = new Crawler(saveDir, "AraneaBot", null, 1, 0);
        testCrawler.run();
        long endTime = System.currentTimeMillis();

        long timeElapsed = endTime - startTime;

        if (timeElapsed > (2 * waitingTime + waitingTime / 5) || timeElapsed < (2 * waitingTime + waitingTime / 5)) {
            System.out.println("Execution time in milliseconds: " + timeElapsed);
            assertEquals(queueLen, Crawler.TestCounter);
            //Assert.fail("Didn't sleep properly; time spend: " + timeElapsed + " should spend " + 2*waitingTime);
        }
    }

    List<String> addToList(String saveDir, String mainStr, boolean test) {
        String pathString;
        List<String> allPaths = new ArrayList<String>();

        if (test) {
            pathString = saveDir + mainStr + "index.html";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "elements.html";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "generic.html";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "/images/pic01.jpg";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "/images/pic02.jpg";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "/images/pic03.jpg";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "/images/pic04.jpg";
            allPaths.add(pathString);
        } else {
            pathString = saveDir + mainStr + "/instagram/w3schools.com_official";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "/w3schools/index.html";
            allPaths.add(pathString);

            pathString = saveDir + mainStr + "/w3schools/images/img_logo_small.png";
            allPaths.add(pathString);

        }
        return allPaths;

    }


}