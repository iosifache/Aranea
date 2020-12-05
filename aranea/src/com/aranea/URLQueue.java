package com.aranea;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class URLQueue {

    private  static int counter;
    private static LinkedHashSet<URL> urlSet;
    private static Iterator<URL> itr;

    private static URLQueue Singleton;

    private URLQueue() {
        urlSet = new LinkedHashSet<URL>();
        itr = null;
        counter = 0;
    };

    public void PrintUrlQueue(){
        Iterator<URL> iterator;
        System.out.println("Printing Queue");

        for(URL url: urlSet){
            System.out.println(url.toString());
        }
        System.out.println("\n");
    }
    public static URLQueue getInstance(){
        if (urlSet == null){
            Singleton = new URLQueue();
        }
        return Singleton;
    }

    //if list is empty returns null
    synchronized public URL remove() throws AraneaException {
        int localCounter = 0;

         for(URL retUrl:urlSet){
             if( localCounter == counter){
                 System.out.println("Counter is:" + counter);
                 counter++;
                 return  retUrl;
             }
             localCounter++;
         }
         return  null;
    }

    public void add(URL new_url){
        synchronized (this) {
            urlSet.add(new_url);
        }
    }

}
