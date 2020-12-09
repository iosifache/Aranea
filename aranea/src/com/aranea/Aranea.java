package com.aranea;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class implementing the main class of Aranea Project.
 * Class contains instance of each class describe in project architecture
 * for accomplish all task.
 *
 * <p>
 * This method is based on a configuration file and its purpose is
 * to download specific web pages.
 * It read a user custom file input for loading page in order to be crawled.
 * <p>
 * A side of this purpose it has multiple features that can help user
 * to identify specific information.
 * For more information please read user manual.
 * </p>
 *
 * @author Bajan Ionut
 */

public class Aranea {

    /**
     * Instance of class Crawler
     * This object is responsible for web crawling.
     */
    static Aranea araneaInstance = null;
    private Crawler[] crawlerArray;
    private URLQueue urlQueue = null;
    static private Sieve sieve = null;
    private ExtensionFinder extensionFinder = null;
    private PatternFinder patternFinder = null;
    private SitemapGenerator sitemapGenerator = null;

    static private AraneaLogger araneaLogger = null;
    static private AraneaConfiguration araneaConfiguration = null;

    /**
     * @param file a specific path to file contains configuration data
     *             any key not properly configured generates a WARNING message
     */
    static private void initConfiguration(String file)
            throws AraneaConfiguration.ConfigurationOpenFileException,
            AraneaConfiguration.ConfigurationMissingKeysException {
        try {

            araneaConfiguration = new AraneaConfiguration(file);
            //araneaLogger.log(AraneaLogger.AraneaLoggerLevels.INFO,"Configuration file:'"+file+"' was loaded.");
        } catch (Exception e) {

            System.out.printf("This should not be displayed");
        }


    }

    /**
     * assign private member araneaLogger to corresponding
     * Singleton class AraneaLogger
     */
    static private void initLogger() throws AraneaException {

        try {

            araneaLogger = AraneaLogger.getInstance();
            araneaLogger.setMinLevel(araneaConfiguration.getLogLevel());
            araneaLogger.setOutputFile(araneaConfiguration.getLogFile());

        } catch (AraneaException e) {
            throw (new AraneaException(AraneaLogger.AraneaLoggerLevels.CRITICAL, "Logger could not be configured."));
        }


    }

    /**
     * assign private member sieve to corresponding
     * Singlenton class Sieve
     */
    static private void initSieve() {

        Sieve sieve = Sieve.getInstance();

        //check if robots file si allowed
        if (!araneaConfiguration.isSkipRobotsdottxtFiles())
            sieve.addFileIgnoredPages("robots.txt");

        //add extension form Configuration to Sieve
        sieve.addAllowedExtension(araneaConfiguration.getAllowedExtensions());

    }

    /**
     * assign private member urlQueue to corresponding
     * Singlenton class URLQueue
     */
    private void initUrlQueue() {

        urlQueue = URLQueue.getInstance();
    }


    /**
     * This method read content of a specific file line by line
     *
     * @param file
     * @return an String Array
     * @Throw an error if file not found
     */
    private List<String> readFileContent(String file) throws FileNotFoundException {

        List<String> lines = new ArrayList<>();
        try {
            Scanner scanner = new Scanner(new File(file));
            while (scanner.hasNext()) {
                lines.add(scanner.nextLine());
            }
            scanner.close();
        } catch (FileNotFoundException e) {

            araneaLogger.log(AraneaLogger.AraneaLoggerLevels.WARNING, "File: '" + file + "' not found");

        }

        return lines;
    }

    /**
     * Split a specific string and return an objet array
     * The delimiter is ' '
     *
     * @param line string to split
     * @return String List of ever word
     */
    private List<String> lineParser(String line) {

        List<String> commands = new ArrayList<>();
        for (String word : line.split(" ")) {
            commands.add(word);
        }
        return commands;
    }

    /**
     * This module read user input and execute specific command
     * If command is not defined a specific message will be display
     */
    public void interactiveMode()
            throws AraneaException {

        Scanner scanner = new Scanner(System.in);
        String userInput = null;
        boolean interactiveMode = true;

        try {

            do {

                System.out.printf("aranea>> ");
                userInput = scanner.nextLine();
                if (userInput.contains("exit"))
                    interactiveMode = false;
                else
                    executeCommand(lineParser(userInput).toArray(new String[0]));

            } while (interactiveMode);

        } catch (AraneaException e) {

            e.logException(araneaLogger);
        }
    }

    /**
     * Display files name with a specific extension from download Dir
     *
     * @param extension file extension
     */
    private void listExtension(String extension) {

        ExtensionFinder extFinder =
                new ExtensionFinder(araneaConfiguration.getDownloadDir(), extension);

    }

    /**
     * Display files name containing that specific pattern
     *
     * @param pattern keyword
     */

    private void searchPattern(String pattern) {

        patternFinder =
                new PatternFinder(araneaConfiguration.getDownloadDir(), pattern);

    }

    /**
     * Start processing urls and download file to local directory
     * Before downloading process starts, check sieve for ignored Pages
     *
     * @param file path to file conaints urls
     * @throws FileNotFoundException if file does not exists
     * @throws MalformedURLException if url can be initialised from string
     */
    private void startCrawling(String file)
            throws FileNotFoundException, MalformedURLException {

        String[] links = null;
        links = readFileContent(file).toArray(new String[0]);
        int queueSize = 0;
        for (String link : links) {

            if (!sieve.checkIgnoredPages(link)) {

                try {
                    araneaLogger.log(AraneaLogger.AraneaLoggerLevels.INFO, "LINK:'" + link + "' added to Queue");
                    urlQueue.add(new URL(link));
                    queueSize += 1;
                } catch (Exception e) {
                    araneaLogger.log(AraneaLogger.AraneaLoggerLevels.WARNING, "LINK:'" + link + "' couldn't be added to Queue");

                }

            }
        }

        /**
         *Crawlers JOB START HERE
         *@queueSize = exact number of URL in QUEUE
         * TO DO: set dir_downloads;
         * @TODO: Va fi implemenat dupa ce finalizarea clasei crawler
         */

    }


    /**
     * Giving a specific array string contains keyword
     * This function select a specific task
     *
     * @param commands command keyword and specific arguments
     * @throws AraneaException if invalid command was entered
     */
    private void executeCommand(String[] commands) throws AraneaException {

        try {

            if (commands != null)
                switch (commands[0]) {

                    //extension finder
                    case "list":
                        for (int i = 1; i < commands.length; i++) {
                            System.out.println("List exention " + commands[i]);
                            listExtension(commands[i]);
                        }
                        break;

                    //generete sitemap
                    case "sitemap":
                        sitemapGenerator = new SitemapGenerator(araneaConfiguration.getDownloadDir(), "sitemap.txt");
                        break;

                    //download url from files
                    case "get":
                        for (int i = 1; i < commands.length; i++) {
                            startCrawling(commands[i]);
                        }
                        break;

                    // set configuration
                    case "set":
                        araneaConfiguration = new AraneaConfiguration(commands[1]);
                        break;

                    //pattern finder
                    case "search":
                        for (int i = 1; i < commands.length; i++) {
                            searchPattern(commands[i]);
                        }
                        break;

                    //show help
                    case "--help":
                        //@TODO: Will be implemented in next versions
                        System.out.println("Aranea COMAND " + commands[0]);
                        break;

                    default:

                        if (commands.length == 0)
                            System.out.println("Aranea option not found");
                        break;
                }
            else {
                System.out.println("use --help for more information");
            }
        } catch (Exception e) {

            araneaLogger.log(AraneaLogger.AraneaLoggerLevels.INFO, "Invalid or incomplete command");
            System.out.println("Invalid command");
        }
    }

    /**
     * Constructor implements interaction methods
     * User can interact live with program or
     * Static by inserting a specific commmand
     *
     * @param commands string array of keyword
     * @throws AraneaException if command does not exist
     */
    private Aranea(String[] commands) throws AraneaException {

        //default init
        //@TODO: Best to create an emplty constructor for default conf
        initConfiguration("conf.txt");
        //init of Logger
        try {
            initLogger();
        } catch (AraneaException e) {
            e.logException(araneaLogger);
        }

        initSieve();
        initUrlQueue();

        try {
            if (commands.length != 0)
                executeCommand(commands);
            else {

                interactiveMode();
            }
        } catch (AraneaException e) {
            e.logException(araneaLogger);
        }

    }

    public static Aranea getInstance(String[] argv) {

        try {
            if (araneaInstance == null) {
                araneaInstance = new Aranea(argv);
            }
        } catch (AraneaException e) {
            e.logException(araneaLogger);
        }

        return araneaInstance;
    }

    /**
     * Main class
     */
    public static void main(String[] argv)
            throws AraneaException,
            MalformedURLException, FileNotFoundException {


        Aranea aranea = Aranea.getInstance(argv);

        System.out.println("Aranea says BYE :)");
    }


}