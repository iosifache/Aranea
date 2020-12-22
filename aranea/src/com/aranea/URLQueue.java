package com.aranea;

import java.net.URL;
import java.util.Iterator;
import java.util.LinkedHashSet;
import com.aranea.AraneaException.UninitializedQueueAraneaException;

/**
 * Class implementing the URLQueue class of Aranea project.
 * <p>
 * Implements a singleton class of LinkedHashSet multithreaded safe.
 *
 * @author Apostolescu Stefan
 */
public class URLQueue {

    private static int counter;
    private static LinkedHashSet<URL> urlSet;
    private static Iterator<URL> itr;
    private static int maxElements;
    private static URLQueue Singleton;

    /**
     * Constructor of singleton class URLQueue
     *
     * @param maxElem maximum size of the queue
     */
    private URLQueue(int maxElem) {
        urlSet = new LinkedHashSet<URL>();
        itr = null;
        maxElements = maxElem;
        counter = 0;
    }

    /**
     * Returns an instance of the object of the sigleton class URLQueue
     *
     * @param maxElements maximum size of the queue
     *                    *                if the queue was already initialized will not overwrite
     * @return instance of object
     */
    public static URLQueue getInstance(int maxElements) {
        if (urlSet == null) {
            Singleton = new URLQueue(maxElements);
        }
        return Singleton;
    }

    //if list is empty returns null

    /**
     * Extract one element from the queue.
     *
     * @return extracted element
     * null when queue empty
     * @throws UninitializedQueueAraneaException
     */
    synchronized public URL remove() throws UninitializedQueueAraneaException {
        int localCounter = 0;

        if (urlSet.isEmpty()) {
            throw new UninitializedQueueAraneaException(AraneaLogger.AraneaLoggerLevels.ERROR);
        }
        for (URL retUrl : urlSet) {
            if (localCounter == maxElements) {
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

    /**
     * Add one element to the queue
     *
     * @param new_url the url to add
     */
    public void add(URL new_url) {
        synchronized (this) {
            urlSet.add(new_url);
        }
    }
}
