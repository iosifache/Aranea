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

    //if list is empty returns null
    synchronized public URL remove() throws UninitializedQueueAraneaException {
        int localCounter = 0;

        if (urlSet.isEmpty()){
            throw new UninitializedQueueAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }
        for (URL retUrl : urlSet) {
            if (localCounter == maxElements){
                return null;
            }
            if (localCounter == counter) {
                counter++;
                return retUrl;
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

    public class UninitializedQueueAraneaException extends AraneaException {
        public UninitializedQueueAraneaException(AraneaLogger.AraneaLoggerLevels level) {
            super(level, "UrlQueue is not initialized");
        }
    }
}
