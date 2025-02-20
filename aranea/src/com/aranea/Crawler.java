package com.aranea;

import com.aranea.AraneaException.FailedRequestException;
import com.aranea.AraneaLogger.AraneaLoggerLevels;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.aranea.AraneaException.ConnectionAraneaException;
import com.aranea.AraneaException.CreateDirecoryAraneaException;
import com.aranea.AraneaException.InsertFailAraneaException;
import com.aranea.AraneaException.InterruptedAraneaException;
import com.aranea.AraneaException.InterruptedSleepAraneaException;
import com.aranea.AraneaException.MalformedURLAraneaException;
import com.aranea.AraneaException.PageRetrieveAraneaException;
import com.aranea.AraneaException.RemoveFailAraneaException;
import com.aranea.AraneaException.UnsuccessfullResponseAraneaException;
import com.aranea.AraneaException.WriteToFileAraneaException;

/**
 * Class implementing the crawling functionality of Aranea Project
 *
 * The main functionality of the class if retrieving HHTPS response,
 * converting url to relative paths and saving the response in a file.
 *
 * @author Apostolescu Stefan
 */
public class Crawler extends Thread {

    public static int counter = 0;
    public static boolean isCounterInited = false;
    public static int TestCounter;
    private static Pattern relativePattern;
    private static Pattern pattern;
    private static String finalDirectory;
    private static AraneaLogger logger;
    private final URLQueue urlQueue;
    private final String userAgent;
    private final long sleepTime;
    private final Sieve sieveInstance;


    public Crawler(String saveDirectory, String usedUserAgent, Sieve usedSieveInstance, int ThreadNumbers, long initializeSleep, int maxDownloadedPages) {

        finalDirectory = saveDirectory;
        urlQueue = URLQueue.getInstance(maxDownloadedPages);
        logger = AraneaLogger.getInstance();
        synchronized (this) {
            if (!isCounterInited){
                counter = ThreadNumbers;
                isCounterInited = true;
            }
        }
        userAgent = usedUserAgent;
        sleepTime = initializeSleep;
        sieveInstance = usedSieveInstance;
        TestCounter = 0;

        String urlRegex = "((https?|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        String urlRelative = "(href=|src=)\"(.*?)\"";
        relativePattern = Pattern.compile(urlRelative, Pattern.CASE_INSENSITIVE);
    }

    /**
     * Finds and extacts urls from the response
     *
     * @param urlResponse stringBuffer response
     * @param scrapUrl the url that has been scrapped
     * @param untouchedUrl will contain urls inside src or href
     * @return a list of extracted links
     */
    private static List<URL> extractUrls(StringBuffer urlResponse, URL scrapUrl, Set<String> untouchedUrl) throws AraneaException {
        URL strUrl = null;
        String output;
        String finalString;
        String[] splitted;

        List<URL> containedUrls = new ArrayList<URL>();
        Matcher urlMatcher = pattern.matcher(urlResponse);

        //extract all hhtps
        while (urlMatcher.find()) {
            output = urlResponse.substring(urlMatcher.start(0),
                    urlMatcher.end(0));

            try {
                strUrl = new URL(output);
            } catch (MalformedURLException e) {
                throw new MalformedURLAraneaException();
            }

            containedUrls.add(strUrl);
        }

        //extract links that start with href
        Matcher urlRelative = relativePattern.matcher(urlResponse);
        while (urlRelative.find()) {

            output = urlRelative.group(0);
            splitted = output.split("\"");

            if (splitted.length > 1) {
                output = splitted[1];
                //if the string is relative path we concatenate with the url
                if (!output.startsWith("#")) {
                    if (!output.startsWith("https://")) {

                        if (!scrapUrl.toString().endsWith("/") && !output.startsWith("/")) {
                            output = "/" + output;
                        }
                        finalString = scrapUrl.toString() + output;
                        if (output.length() > 2) {
                            untouchedUrl.add(output);
                        }
                    } else {
                        finalString = output;
                    }
                    try {
                        strUrl = new URL(finalString);
                    } catch (MalformedURLException e) {
                        throw new MalformedURLAraneaException();
                    }
                    containedUrls.add(strUrl);
                }
            }
        }

        return containedUrls;
    }

    /**
     * generates and creates the directories where the content will be saved
     * @param url the extracted URL
     * @return the path to the save directory
     * @throws AraneaException
     */
    synchronized public static String getSavePath(URL url) throws AraneaException {

        String hostName;
        String finalPath = finalDirectory;
        String path;
        boolean check = false;
        File newFile = null;

        hostName = url.getHost();
        path = url.getPath();
        String urlString = url.toString();

        //check if finalPath ends with "/"
        if (!finalPath.endsWith("/")) {
            finalPath = finalPath + "/";
        }

        //process hostName
        if (hostName.startsWith("www.")) {
            hostName = hostName.replace("www.", "");
        }

        if (hostName.endsWith(".com")) {
            hostName = hostName.replace(".com", "");
        }

        if (path.equals("/") || url.toString().endsWith("/")) {
            path = "/index.html";
        }


        finalPath += hostName;
        finalPath += path;

        Path pathToFile = Paths.get(finalPath);
        logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Path to file: " + pathToFile.getParent().toString());

        try {
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException e) {
            throw new CreateDirecoryAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Error creating directory");
        }

        return finalPath;
    }

    /**
     * Overrides Thread run method
     *
     * Implements multithreading.
     */
    public void run() {
        boolean check, previousCheck;

        check = true;

        while (counter > 0) {
            previousCheck = check;
            try {
                check = downloadNextUrl();
                if (check) {
                    try {
                        synchronized (Thread.currentThread()) {
                            TestCounter++;
                        }
                        logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Thread " + Thread.currentThread().toString() + " is sleeping");
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        throw new InterruptedSleepAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
                    }
                }
            } catch (AraneaException e) {
                if (e.getLevel() == AraneaLogger.AraneaLoggerLevels.WARNING) {
                    check = true;
                } else {
                    try {
                        throw e;
                    } catch (AraneaException ex) {
                        logger.log(AraneaLogger.AraneaLoggerLevels.ERROR, "Error " + ex.getMessage());
                    }
                }
                logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Error " + e.getMessage());
            }

            if (!previousCheck && check) {
                synchronized (this) {
                    counter++;
                }
            }

            if (!check && previousCheck) {
                synchronized (this) {
                    counter--;
                }
            }
        }
    }

    /**
     * The main method of the crawler.
     * Pops url from URLQueue, retrieves HTTPS response,
     * and calls method for formatting and saving the answer
     * @throws AraneaException
     */
    public boolean downloadNextUrl() throws AraneaException {

        int responseCode;
        URL scrapUrl;
        String savePath;
        String replacedResponse;
        HttpURLConnection con;
        List<URL> extractedUrls;
        Set<String> untouchedUrl;
        untouchedUrl = new HashSet<String>();

        try {
            scrapUrl = urlQueue.remove();
            if (scrapUrl != null) {
                logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Next URL to scrap is: " + scrapUrl.toString());
            }
        } catch (AraneaException e) {
            throw new RemoveFailAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }

        //queue is empty
        if (scrapUrl == null) {
            return false;
        }

        if (sieveInstance != null) {
            if (!sieveInstance.checkURL(scrapUrl.toString()))
                return true;
        }

        //check if it an image
        if (scrapUrl.toString().endsWith(".jpg") || scrapUrl.toString().endsWith(".jpeg")) {
            try {
                savePath = getSavePath(scrapUrl);
                Path checkFile = Paths.get(savePath);

                if (!Files.exists(checkFile)) {
                    InputStream in = scrapUrl.openStream();
                    Files.copy(in, Paths.get(savePath));
                }
                return true;
            } catch (IOException e) {
                logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Failed to download image: " + scrapUrl.toString());
                return true;
            }
        }

        try {
            con = (HttpURLConnection) scrapUrl.openConnection();
            con.setRequestMethod("GET");
        } catch (IOException e) {
            throw new ConnectionAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }

        con.setRequestProperty("User-Agent", userAgent);

        try {
            responseCode = con.getResponseCode();
        } catch (IOException e) {
            throw new PageRetrieveAraneaException(AraneaLogger.AraneaLoggerLevels.WARNING);
        }

        try {
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                //extract a list of urls from the current page
                extractedUrls = extractUrls(response, scrapUrl, untouchedUrl);

                //filter extracted URLs using Sieve
                if (sieveInstance != null) {
                    extractedUrls = checkSieve(extractedUrls);
                }

                if (sieveInstance.checkContent(response.toString())) {

                    try{

                        //create directories and get the save path
                        savePath = getSavePath(scrapUrl);

                        // convert html links to relative paths
                        replacedResponse = replaceToLocal(response, extractedUrls, untouchedUrl);

                        // save the modify page to the save path
                        saveResponse(replacedResponse, savePath);

                    }
                    catch (Exception e){
                        logger.log(AraneaLoggerLevels.WARNING, "Cannot save URL: " + scrapUrl);
                    }

                }

                try {
                    for (URL url : extractedUrls) {
                        urlQueue.add(url);
                    }
                } catch (Exception e) {
                    throw new InsertFailAraneaException(AraneaLogger.AraneaLoggerLevels.WARNING);
                }

            } else {
                throw new UnsuccessfullResponseAraneaException(AraneaLogger.AraneaLoggerLevels.WARNING);
            }
        } catch (IOException e) {
            throw new UnsuccessfullResponseAraneaException(AraneaLogger.AraneaLoggerLevels.WARNING);
        }

        return true;
    }

    /**
     * Replaces URLs with local references
     *
     * @param response the HTTPS response
     * @param LoL list of links to be replaced
     * @param untouchedUrl links of links contained in href, src
     * @return replaced response
     */
    private String replaceToLocal(StringBuffer response, List<URL> LoL, Set<String> untouchedUrl) {
        //LoL - List of Links :D
        int intIndex;
        String relPath;
        String replaced;
        String localConverted;
        String finalResponse;

        for (URL l : LoL) {
            intIndex = 1;

            while (intIndex != -1) {
                intIndex = response.indexOf(l.toString());

                if (intIndex != -1) {
                    relPath = l.getPath();
                    response = response.replace(intIndex, intIndex + l.toString().length(), relPath);
                }
            }
        }
        replaced = response.toString();
        finalResponse = replaced;

        for (String local : untouchedUrl) {
            localConverted = local;

            if (!localConverted.startsWith("./")) {
                if (!localConverted.startsWith("/")) {
                    localConverted = "./" + localConverted;
                } else {
                    localConverted = "." + localConverted;
                }
            }

            try {
                local = URLEncoder.encode(local, StandardCharsets.UTF_8.name());
                localConverted = URLEncoder.encode(localConverted, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                logger.log(AraneaLogger.AraneaLoggerLevels.ERROR, "Eroare URLEncoder");
            }
            finalResponse = finalResponse.replaceAll(local, localConverted);


        }
        return finalResponse;
    }

    /**
     * Writes response to file
     * @param response  the HTTPS modified response
     * @param path saving path
     * @throws AraneaException
     */

    private void saveResponse(String response, String path) throws AraneaException {
        Path checkFile = Paths.get(path);

        if (!path.matches("(.*)?\\.[a-z]{0,4}$")) {
            logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Path matched : " + path);

            if (response.contains("<!DOCTYPE html>")) {
                Path newPath = Paths.get(path + ".html");
                path = path + ".html";
                checkFile = newPath;
            }
        }

        if (!Files.exists(checkFile)) {
            try {
                BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(path)));
                bwr.write(response);
                bwr.flush();
                bwr.close();
            } catch (IOException e) {
                throw new WriteToFileAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
            }
        }
    }

    /**
     * Checks the list of URLs against the sieve
     *
     * @param currentList extracted links
     * @return the filtered list
     * @throws FailedRequestException
     */
    private List<URL> checkSieve(List<URL> currentList) throws FailedRequestException {
        List<URL> newList = new ArrayList<URL>();

        if (sieveInstance == null) {
            return currentList;
        }

        for (URL u : currentList) {
            try {
                if (sieveInstance.checkURL(u.toString())) {
                    newList.add(u);
                }
            } catch (FailedRequestException e) {
                e.logException(logger);
            }
        }

        return newList;
    }

    private void exampleOfUse() throws AraneaException {
        int thNumber = 4;
        long sleepTime = 0;
        URLQueue urlQ = URLQueue.getInstance(20);
        URL url2;
        List<Crawler> crwList = new ArrayList<Crawler>();

        // Init sieve
        String[] extensions = {"*"};
        Sieve sieve = Sieve.getInstance(extensions, 1000000, "*", false);

        //create URL
        try {
            url2 = new URL("https://webscraper.io/");
        } catch (MalformedURLException e) {
            throw new MalformedURLAraneaException();
        }

        urlQ.add(url2);

        //Create Threads and start them
        for (int i = 0; i < thNumber; i++) {
            Crawler crw = new Crawler("../SaveDir", "Aranea", null, thNumber, sleepTime, 20);
            crwList.add(crw);
        }

        for (int i = 0; i < 4; i++) {
            crwList.get(i).start();
        }

        try {
            for (int i = 0; i < 4; i++) {
                crwList.get(i).join();
            }
        } catch (InterruptedException e) {
            throw new InterruptedAraneaException();
        }

    }

}

