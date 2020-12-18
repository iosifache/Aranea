package com.aranea;

import org.junit.Assert;
import org.junit.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Crawler extends Thread {

    public static int counter = 0;
    private String userAgent;
    private long sleepTime;
    private static Pattern relativePattern;
    private static Pattern pattern;
    private static String finalDirectory;
    private final AraneaLogger logger;
    private final URLQueue urlQueue;
    private Sieve sieveInstance;

    //test variable
    public static int TestCounter;

    public Crawler(String saveDirectory, String usedUserAgent, Sieve usedSieveInstance, int ThreadNumbers, long initializeSleep) {

        finalDirectory = saveDirectory;
        urlQueue = URLQueue.getInstance(20);
        logger = AraneaLogger.getInstance();
        counter = ThreadNumbers;
        userAgent = usedUserAgent;
        sleepTime = initializeSleep;
        sieveInstance = usedSieveInstance;
        TestCounter = 0;

        String urlRegex = "((https?|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        String urlRelative = "(href=|src=)\"(.*?)\"";
        relativePattern = Pattern.compile(urlRelative, Pattern.CASE_INSENSITIVE);
    }

    //finds urls contained in the parsed response
    private static List<URL> extractUrls(StringBuffer urlResponse, URL scrapUrl, Set<String> untouchedUrl) throws AraneaException {
        URL strUrl = null;
        String output;
        String finalString;
        String[] splitted;

        List<URL> containedUrls = new ArrayList<URL>();
        Matcher urlMatcher = pattern.matcher(urlResponse);


        while (urlMatcher.find()) {
            output = urlResponse.substring(urlMatcher.start(0),
                    urlMatcher.end(0));

            try {
                strUrl = new URL(output);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            containedUrls.add(strUrl);
        }
        Matcher urlRelative = relativePattern.matcher(urlResponse);
        while (urlRelative.find()) {

            output = urlRelative.group(0);
            splitted = output.split("\"");

            if (splitted.length > 1) {
                output = splitted[1];

                //it the string is relative path we concatenate with the url
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
                        throw new AraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Error converting string to URL");
                    }
                    containedUrls.add(strUrl);
                }
            }
        }

        return containedUrls;
    }

    public void run() {
        boolean check, previousCheck;

        check = true;

        while (counter > 0) {
            previousCheck = check;
            try {
                check = downloadNextUrl();
                if( check){
                    try {
                        System.out.println("SLeeping time: " + sleepTime);
                        synchronized (Thread.currentThread()){
                            TestCounter++;
                        }
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } catch (AraneaException e) {
                if (e.getLevel() == AraneaLogger.AraneaLoggerLevels.WARNING) {
                    check = true;
                } else {
                    try {
                        throw e;
                    } catch (AraneaException ex) {
                        //logger.log(AraneaLogger.AraneaLoggerLevels.ERROR, "Error " + ex.getMessage());
                    }
                }
                //logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Error " + e.getMessage());
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

    //call it with "." to save to current directory
    public boolean downloadNextUrl() throws AraneaException {

        int responseCode;
        Image img = null;
        URL scrapUrl;
        String savePath;
        String replacedResponse;
        HttpURLConnection con;
        List<URL> extractedUrls;
        Set<String> untouchedUrl;
        untouchedUrl = new HashSet<String>();

        try {
            scrapUrl = urlQueue.remove();
            //logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Next URL to scrap is: " + scrapUrl.toString());
        } catch (AraneaException e) {
            throw new RemoveFailAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }

        //queue is empty
        if (scrapUrl == null) {
            //logger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Queue is empty");
            return false;
        }

        if( sieveInstance != null) {
            if (!sieveInstance.checkURL(scrapUrl.toString()))
                return true;
        }

        //check if it an image
        if (scrapUrl.toString().endsWith(".jpg") || scrapUrl.toString().endsWith(".jpeg")){

            try {
                savePath = getSavePath(scrapUrl);
                Path checkFile = Paths.get(savePath);

                if (!Files.exists(checkFile)) {
                    InputStream in = scrapUrl.openStream();
                    Files.copy(in, Paths.get(savePath));
                }
                return true;
            }
            catch (IOException e){
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

                //create directories and get the save path
                savePath = getSavePath(scrapUrl);

                //filter extracted URLs using Sieve
                if (sieveInstance != null) {
                    extractedUrls = checkSieve(extractedUrls);
                }
                //convert html links to relative paths
                replacedResponse = replaceToLocal(response, extractedUrls, untouchedUrl);

                //save the modify page to the save path
                saveResponse(replacedResponse, savePath);

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

    //generates and creates the directories where the content will be saved
    public static String getSavePath(URL url) throws AraneaException {

        String hostName;
        String finalPath = finalDirectory;
        String path;

        hostName = url.getHost();
        path = url.getPath();

        //check if finalPath ends with "/"
        if (!finalPath.endsWith("/")){
            finalPath = finalPath + "/";
        }

        //process hostName
        if (hostName.startsWith("www.")) {
            hostName = hostName.replace("www.", "");
        }

        if (hostName.endsWith(".com")) {
            hostName = hostName.replace(".com", "");
        }

        if (path.equals("/")) {
            path = "/index.html";
        }

        finalPath += hostName;
        finalPath += path;

        Path pathToFile = Paths.get(finalPath);
        try {
            Files.createDirectories(pathToFile.getParent());
        } catch (IOException e) {
            throw new CreateDirecoryAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }

        return finalPath;
    }

    //is supposed to replace URL with local reference - doesn't work as supposed
    private String replaceToLocal(StringBuffer response, List<URL> LoL, Set<String> untouchedUrl) {
        //LoL - List of Links :D
        int intIndex;
        String relPath;
        String replaced;

        for (URL l : LoL) {
            intIndex = 1;

            while(intIndex != -1) {
                intIndex = response.indexOf(l.toString());

                if (intIndex != -1) {
                    relPath = l.getPath();
                    response = response.replace(intIndex, intIndex + l.toString().length(), relPath);
                }
            }
        }
        replaced = response.toString();
        return replaced;
    }

    //writes response to file path
    private void saveResponse(String response, String path) throws AraneaException {

        Path checkFile = Paths.get(path);
        if(!Files.exists(checkFile)) {
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

    //checks the list of urls with sieve
    private List<URL> checkSieve(List<URL> currentList) throws Sieve.FailedRequestException {
        List<URL> newList = new ArrayList<URL>();

        if( sieveInstance == null){
            return currentList;
        }

        for(URL u:currentList){
            if(sieveInstance.checkURL(u.toString())){
                newList.add(u);
            }
        }

        return newList;
    }

    private void exampleOfUse() throws AraneaException {
        int thNumber = 4;
        URLQueue urlQ = URLQueue.getInstance(20);
        URL url2;

        // Init sieve
        String extensions[] = {"*"};
        Sieve sieve = Sieve.getInstance(extensions, 1000000, "*");

        try {
            url2 = new URL("https://webscraper.io/");
        } catch (MalformedURLException e) {
            throw  new AraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Malformed URL");
        }
        urlQ.add(url2);

        for (int i=0; i<thNumber; i++){
            Crawler crawler = new Crawler("./newDir","CustomUserAgent", sieve,1, 1);
            crawler.start();
        }
    }

    //exceptions
    public class ConnectionAraneaException extends AraneaException {
        public ConnectionAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Error opening connection");
        }
    }

    public static class CreateDirecoryAraneaException extends AraneaException {
        public CreateDirecoryAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Error creating the directory");
        }
    }

    public class PageRetrieveAraneaException extends AraneaException {
        public PageRetrieveAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Error retrieving the page");
        }
    }

    public class WriteToFileAraneaException extends AraneaException {
        public WriteToFileAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Error writing content to the directory");
        }
    }

    public class UnsuccessfullResponseAraneaException extends AraneaException {
        public UnsuccessfullResponseAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "The response was not sucesfull");
        }
    }

    public class InsertFailAraneaException extends AraneaException {
        public InsertFailAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Failed to insert in URLQueue");
        }
    }

    public class RemoveFailAraneaException extends AraneaException {
        public RemoveFailAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Failed to remove from URLQueue");
        }
    }

    public class InterruptedSleepAraneaException extends AraneaException{
        public InterruptedSleepAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "Sleep was interrupted");
        }
    }

}

