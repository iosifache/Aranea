package com.aranea;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.lang.System.out;


public class Crawler {

    private static String urlRegex = "((https?|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    private static Pattern pattern;
    private static final String USER_AGENT = "Aranea";
    private AraneaLogger logger;
    private URLQueue urlQueue;

    public  Crawler() {
        urlQueue = URLQueue.getInstance();
        logger = AraneaLogger.getInstance();
        pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
    }

    public void downloadNextUrl() throws AraneaException {

        int responseCode;
        URL scrapUrl;
        String savePath;
        String replacedResponse;
        HttpURLConnection con;
        List<URL> extractedUrls;

        try {
            scrapUrl = urlQueue.remove();
            System.out.println("Next URL is: " + scrapUrl.toString());
        }
        catch (AraneaException e){
            System.out.println("Empty queue\n");
            return;
        }

        try {
            con = (HttpURLConnection) scrapUrl.openConnection();
            con.setRequestMethod("GET");
        }
        catch (IOException e){
            throw new AraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Error opening connection");
        }

        con.setRequestProperty("User-Agent", USER_AGENT);

        try {
            responseCode = con.getResponseCode();
        }
        catch (IOException e){
            throw new AraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Error retriving the response");
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

                extractedUrls = extractUrls(response);
                savePath = getSavePath(scrapUrl);

                //not finished yet
                //replacedResponse = replaceToLocal(response);
                //saveResponse(replacedResponse, savePath);

                try{
                    for (URL url : extractedUrls) {
                        urlQueue.add(url);
                    }
                }
                catch (Exception e){
                    throw new AraneaException(AraneaLogger.AraneaLoggerLevels.WARNING, "Failed to insert in list");
                }

            } else {
                throw new AraneaException(AraneaLogger.AraneaLoggerLevels.WARNING, "Response was not successful,error code: " + responseCode);
            }
        }
        catch (IOException e){
            throw new AraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Response was not successful");
        }
    }

    //finds urls contained in the parsed response
    private static List<URL> extractUrls(StringBuffer urlResponse) {
        URL strUrl = null;
        String output;

        List<URL> containedUrls = new ArrayList<URL>();
        Matcher urlMatcher = pattern.matcher(urlResponse);

        while (urlMatcher.find())
        {
            output = urlResponse.substring(urlMatcher.start(0),
                    urlMatcher.end(0));

            try {
                strUrl = new URL(output);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            containedUrls.add(strUrl);
        }

        return containedUrls;
    }

    //generates and creates the directories where the content will be saved - kinda works
    private String getSavePath(URL url) throws IOException {

        String urlString = url.toString();
        String pathString = "./";
        String[] splitted;
        String lastElement, finalPath;
        Path path;
        int index;

        splitted = urlString.split("/");
        pathString = splitted[2];

        if (splitted[2].startsWith("www.")){
            pathString = pathString.replace("www.","");
        }

        if (pathString.endsWith(".com")){
            pathString = pathString.replace(".com","");
        }

        pathString = "./"+ pathString;

        for(int i=3;i<splitted.length-1;i++){
            pathString += splitted[i];
            pathString += "/";

            path = Paths.get(pathString);

            if(! Files.exists(path)){
                Files.createDirectories(path);
                System.out.println("New directory created, path is:" + pathString);
            }
        }
        if (splitted.length > 3) {
            lastElement = splitted[splitted.length - 1];
            index = lastElement.lastIndexOf(".");
            System.out.println("index is : " + index + "string len is: " + lastElement.length() + "string is: " + lastElement);

            if (index == lastElement.length() - 4) {
                pathString = pathString + lastElement;
                //save in the path with this name
            } else if (lastElement.endsWith("/")) {
                pathString = pathString + "index.html";
            } else {//try split by ?
                String[] new_elem = lastElement.split("\\?");
                String elem = new_elem[0];
                pathString = pathString + elem;
                //save with this name
            }
        }
        System.out.println("Final path is: "+ pathString);
        return  pathString;
    }

    //is supposed to replace URL with local reference - doesn't work as supposed
    private String replaceToLocal(StringBuffer response) throws AraneaException, IOException {
        String bufString = response.toString();
        Matcher urlMatcher = pattern.matcher(bufString);
        String output;
        int index;

        while (urlMatcher.find())
        {
            String[] splitted;
            String pathString = "./" ;
            String lastElement;

            output = bufString.substring(urlMatcher.start(0),
                    urlMatcher.end(0));
            System.out.println("String found is: "+ output);
            splitted = output.split("/");

            pathString = splitted[2];

            if (splitted[2].startsWith("www.")){
                pathString = pathString.replace("www.","");
            }

            if (pathString.endsWith(".com")){
                pathString = pathString.replace(".com","");
            }

            pathString = "./"+ pathString;

            for(int i=3;i<splitted.length-1;i++){
                pathString += splitted[i];
                pathString += "/";
            }
            if ( splitted.length > 2) {
                lastElement = splitted[splitted.length - 1];
                index = lastElement.lastIndexOf(".");

                if (index == lastElement.length() - 4) {
                    pathString += lastElement;
                } else if (lastElement.endsWith("/")) {
                    pathString += "index.html";
                } else {
                    String[] new_elem = lastElement.split("\\?");
                    String elem = new_elem[0];
                    pathString += elem;
                }
            }
            System.out.println("Local path is: "+ pathString);
            bufString = bufString.replace(output, pathString);
        }
        return  bufString;

    }

    //writes response to file path
    private void saveResponse(String response, String path) {

        try {
            BufferedWriter bwr = new BufferedWriter(new FileWriter(new File(path)));
            bwr.write(response);
            bwr.flush();
            bwr.close();
        }
        catch (IOException e){
            System.out.println("PLM ceva exceptie");
        }
    }

    //how to use it
    private void ExampleOfUse() throws  AraneaException {

        Crawler newCrawler = new Crawler();
        URL obj1;
        URL obj;

        //convert initial strings to URL objects
        try {
            obj1 = new URL("https://webscraper.io");
            obj = new URL("https://www.w3schools.com/java/java_constructors.asp");
        }
        catch (MalformedURLException e){
            throw new AraneaException(AraneaLogger.AraneaLoggerLevels.ERROR, "Error creating URL objects");
        }

        //get singleton instance
        URLQueue urlQueue = URLQueue.getInstance();

        //add initial URLs to queue
        urlQueue.add(obj1);
        urlQueue.add(obj);

        urlQueue.PrintUrlQueue();

        try {
            newCrawler.downloadNextUrl();
        } catch (AraneaException e) {
            e.printStackTrace();
        }

        urlQueue.PrintUrlQueue();

        try {
            newCrawler.downloadNextUrl();
        } catch (AraneaException e) {
            e.printStackTrace();
        }

    }

}