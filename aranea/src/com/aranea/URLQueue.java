package com.aranea;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class URLQueue {

    private  static int counter;
    private static LinkedHashSet<URL> urlSet;
    private static Iterator<URL> itr;
    private static int maxElements;
    private static URLQueue Singleton;

    private URLQueue(int maxElem) {
        urlSet = new LinkedHashSet<URL>();
        itr = null;
        maxElements = maxElem;
        counter = 0;
    }

    public static URLQueue getInstance(int maxElements) {
        if (urlSet == null) {
            Singleton = new URLQueue(maxElements);
        }
        return Singleton;
    }

    public void PrintUrlQueue(){
        Iterator<URL> iterator;
        System.out.println("Printing Queue");

        for(URL url: urlSet){
            System.out.println(url.toString());
        }
        System.out.println("\n");
    }

    //if list is empty returns null
    synchronized public URL remove() throws AraneaException {
        int localCounter = 0;

        for (URL retUrl : urlSet) {
            if (localCounter == counter) {
                counter++;
                return retUrl;
            }
            if (localCounter == maxElements){
                return null;
            }
            localCounter++;
        }
        return null;
    }

    public void add(URL new_url){
        synchronized (this) {
            urlSet.add(new_url);
        }
    }

}
