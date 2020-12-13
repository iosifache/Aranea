package com.aranea;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler extends Thread {

    public static int counter = 0;
    private String userAgent;
    private static Pattern relativePattern;
    private static Pattern pattern;
    private static String finalDirectory;
    private final AraneaLogger logger;
    private final URLQueue urlQueue;
    private Sieve sieveInstance;


    public Crawler(String saveDirectory, String usedUserAgent, Sieve usedSieveInstance, int ThreadNumbers) {

        finalDirectory = saveDirectory;
        urlQueue = URLQueue.getInstance(20);
        logger = AraneaLogger.getInstance();
        counter = ThreadNumbers;
        userAgent = usedUserAgent;
        sieveInstance = usedSieveInstance;

        String urlRegex = "((https?|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
        String urlRelative = "href=\"(.*?)\"";
        relativePattern = Pattern.compile(urlRelative, Pattern.CASE_INSENSITIVE);
    }

    //finds urls contained in the parsed response
    private static List<URL> extractUrls(StringBuffer urlResponse, URL scrapUrl, Set<String> untouchedUrl) throws AraneaException {
        URL strUrl = null;
        String output;
        String finalString;

        List<URL> containedUrls = new ArrayList<URL>();
        Matcher urlMatcher = pattern.matcher(urlResponse);
        Matcher urlRelative = relativePattern.matcher(urlResponse);

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

        untouchedUrl.clear();
        while (urlRelative.find()) {
            output = urlResponse.substring(urlRelative.start(0),
                    urlRelative.end(0));

            output = output.substring(6, output.length() - 1);

            //it the string is relative path we concatenate with the url
            if (!output.startsWith("#")) {
                if (!output.startsWith("https://")) {
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
        return containedUrls;
    }

    public void run() {
        boolean check, previousCheck;

        check = true;

        while (counter > 0) {
            previousCheck = check;
            try {
                check = downloadNextUrl();
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

        if (!sieveInstance.checkURL(scrapUrl.toString()))
            return true;

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
        String[] splitted;
        Path checkPath;

        StringBuilder relativePath = new StringBuilder();

        hostName = url.getHost();
        path = url.getPath();

        //process hostName
        if (hostName.startsWith("www.")) {
            hostName = hostName.replace("www.", "");
        }

        if (hostName.endsWith(".com")) {
            hostName = hostName.replace(".com", "");
        }

        if (path.equals("/")) {
            path = "\\index.html";
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
        String strinResponse = response.toString();
        String replaced;
        String out;

        for (URL l : LoL) {
            intIndex = response.indexOf(l.toString());

            if (intIndex != -1) {
                relPath = l.getPath();
                response = response.replace(intIndex, intIndex + l.toString().length(), relPath);
            }
        }
        replaced = response.toString();

        Iterator<String> iterator = untouchedUrl.iterator();

        while (iterator.hasNext()) {
            String element = iterator.next();
            String replacementString = element;
            replaced = replaced.replaceAll(element, replacementString);
            iterator.remove();
        }

        untouchedUrl.clear();
        return replaced;
    }

    //writes response to file path
    private void saveResponse(String response, String path) throws AraneaException {

        try {
            BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(path)));
            bwr.write(response);
            bwr.flush();
            bwr.close();
        } catch (IOException e) {
            throw new WriteToFileAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }
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
            Crawler crawler = new Crawler("./newDir","CustomUserAgent", sieve,1);
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

}

