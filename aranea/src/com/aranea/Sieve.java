package com.aranea;

public class Sieve {

    private static  Sieve sieve = null;

    private Sieve() { };

    public static Sieve getInstance() {
        if(sieve == null)
            sieve = new Sieve();
        return  sieve;
    }

    public static void addIgnoredPages(String file) {
        //
    };

    //@path = calea catre files; Ex: ./robotx.txt
    public static void addFileIgnoredPages(String path) {
        /*trebuie parcurs fisierulul linie cu linie
         * si adaugate paginele in array-ul de ignoredPages
         */

    }

    /**
    * @extensions = array string continand extensiile acceptate;
    * Ex: extensions[0] = "doc", extensions[1] = "txt", ...
    * Caz special: Daca se extension[i]="*" -> se permit toate tipurile de exceptii
    */

    public static void addAllowedExtension(String [] extensions) {

    }

    /**
    *return false if @stringUrl is not in ignoredPages
    */
    public static boolean checkIgnoredPages(String stringUrl) {
        //
        return false;
    }

    public static boolean checkContent() {
        return true;
    }



}
