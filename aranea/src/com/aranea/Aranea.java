package com.aranea;

import com.aranea.AraneaLogger.AraneaLoggerLevels;
import com.aranea.Crawler.ConnectionAraneaException;
import com.aranea.Crawler.PageRetrieveAraneaException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Class implementing the main class of Aranea Project. Class contains instance of each class
 * describe in project architecture for accomplish all task.
 *
 * <p>This method is based on a configuration file and its purpose is to download specific web
 * pages. It read a user custom file input for loading page in order to be crawled.
 *
 * <p>A side of this purpose it has multiple features that can help user to identify specific
 * information. For more information please read user manual.
 *
 * @author Bajan Ionut
 */
public class Aranea {

  /** Instance of class Crawler This object is responsible for web crawling. */
  static Aranea araneaInstance = null;

  private Crawler[] crawlerArray;
  private static URLQueue urlQueue = null;
  private static Sieve sieve = null;
  private ExtensionFinder extensionFinder = null;
  private PatternFinder patternFinder = null;
  private SitemapGenerator sitemapGenerator = null;
  private static AraneaLogger araneaLogger = null;
  private static AraneaConfiguration araneaConfiguration = null;

  private static final String USER_AGENT = "AraneaBot";

  /**
   * @param file a specific path to file contains configuration data any key not properly configured
   *     generates a WARNING message
   */
  private static void initConfiguration(String file)
      throws AraneaConfiguration.ConfigurationOpenFileException,
          AraneaConfiguration.ConfigurationMissingKeysException {
    try {
      araneaConfiguration = new AraneaConfiguration(file);
    } catch (AraneaException e) {
      e.logException(araneaLogger);
      System.exit(1);
    }
  }

  /** assign private member araneaLogger to corresponding Singleton class AraneaLogger */
  private static void initLogger() throws AraneaException {
    araneaLogger = AraneaLogger.getInstance();
  }

  /** assign private member sieve to corresponding Singlenton class Sieve */
  private static void initSieve() throws AraneaException {
    sieve =
        Sieve.getInstance(
            araneaConfiguration.getAllowedExtensions(),
            araneaConfiguration.getAllowedMaxSize(),
            araneaConfiguration.getAllowedPattern());
  }

  /** assign private member urlQueue to corresponding Singlenton class URLQueue */
  private void initUrlQueue() {
    urlQueue = URLQueue.getInstance(64);
  }

  /**
   * This method read content of a specific file line by line
   *
   * @param file
   * @return an String Array @Throw an error if file not found
   */
  private static List<String> readFileContent(String file) throws FileNotFoundException {
    List<String> lines = new ArrayList<>();
    Scanner scanner = new Scanner(new File(file));
    while (scanner.hasNext()) {
      lines.add(scanner.nextLine());
    }
    scanner.close();
    return lines;
  }

  /**
   * Split a specific string and return an objet array The delimiter is ' '
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
   * This module read user input and execute specific command If command is not defined a specific
   * message will be display
   */
  public void interactiveMode() throws AraneaException {

    Scanner scanner = new Scanner(System.in);
    String userInput = null;
    boolean interactiveMode = true;

    try {
      do {
        System.out.printf("aranea >> ");
        userInput = scanner.nextLine();
        if (userInput.contains("exit")) interactiveMode = false;
        else executeCommand(lineParser(userInput).toArray(new String[0]));
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
    ExtensionFinder extFinder = new ExtensionFinder(System.getProperty("user.dir"), extension);
    try {
      extFinder.parse();
    } catch (AraneaException e) {
      e.logException(araneaLogger);
    }
  }

  /**
   * Display files name containing that specific pattern
   *
   * @param pattern keyword
   */
  private void searchPattern(String pattern) {
    patternFinder = new PatternFinder(System.getProperty("user.dir"), pattern);
    try {
      patternFinder.parse();
    } catch (AraneaException e) {
      e.logException(araneaLogger);
    }
  }

  private static void addIgnoredPagesToSieve(String url){
    try {
      HttpURLConnection con = (HttpURLConnection) (new URL(url + "robots.txt")).openConnection();
      con.setRequestMethod("GET");
      con.setRequestProperty("User-Agent", USER_AGENT);
      int responseCode = con.getResponseCode();
      if (responseCode == HttpURLConnection.HTTP_OK) {
        BufferedReader in = new BufferedReader(new InputStreamReader(
            con.getInputStream()));
        String inputLine;
        List<String> ignoredPages = new ArrayList<String>();
        while ((inputLine = in.readLine()) != null) {
          ignoredPages.add(url + inputLine);
        }
        String ignoredPagesAsArray[] = ignoredPages.toArray(new String[0]);
        sieve.addIgnoredPages(ignoredPagesAsArray);
      }
    } catch (Exception e) {
      ;
    }
  }

  /**
   * Start processing urls and download file to local directory Before downloading process starts,
   * check sieve for ignored Pages
   *
   * @param file path to file conaints urls
   * @throws FileNotFoundException if file does not exists
   * @throws MalformedURLException if url can be initialised from string
   */
  private static void startCrawling(String file)
      throws FileNotFoundException, MalformedURLException, Sieve.FailedRequestException {

    List<String> list = readFileContent(file);
    for (String link : list) {
      try {
        if (sieve.checkURL(link) == true) {
          urlQueue.add(new URL(link));
          addIgnoredPagesToSieve(link);
        }
      } catch (Sieve.FailedRequestException e) {
        e.logException(araneaLogger);
      }
    }

    List<Crawler> crawlers = new ArrayList<Crawler>(araneaConfiguration.getMaxThreads());
    for (int i = 0; i < araneaConfiguration.getMaxThreads(); i++){
      Crawler currentCrawler = new Crawler(araneaConfiguration.getDownloadDir(), USER_AGENT, sieve, araneaConfiguration.getMaxThreads());
      currentCrawler.start();
      crawlers.add(currentCrawler);
    }

    for (Crawler crawler : crawlers){
      try {
        crawler.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    if (araneaConfiguration.isSitemapGenerated()){
      for (String link : list) {
        try {
          String downloadPath = Paths.get(Crawler.getSavePath(new URL(link))).getParent().toString();
          SitemapGenerator sitemapGenerator = new SitemapGenerator(downloadPath, downloadPath + "\\sitemap.txt");
          sitemapGenerator.parse();
        } catch (AraneaException e) {
          e.logException(araneaLogger);
        }
      }
    }
  }

  private List<String> parseCommand(String[] commands) {

    String fullCommand = new String();
    for (int i = 1; i < commands.length; i++) {
      fullCommand = commands[i];
      if (i + 1 < commands.length) fullCommand = fullCommand + " ";
    }

    List<String> listCommand = new ArrayList<String>();
    for (String command : fullCommand.split("'")) {

      listCommand.add(command);
    }
    // System.out.println(listCommand);
    return listCommand;
  }

  /**
   * Giving a specific array string contains keyword This function select a specific task
   *
   * @param commands command keyword and specific arguments
   * @throws AraneaException if invalid command was entered
   */
  private void executeCommand(String[] commands) throws AraneaException {

    try {

      switch (commands[0]) {
        case "crawl" -> {
          araneaConfiguration = new AraneaConfiguration(commands[2]);
          try {

            araneaLogger.setOutputFile(araneaConfiguration.getLogFile());
            araneaLogger.setMinLevel(araneaConfiguration.getLogLevel());

            initSieve();
            initUrlQueue();

          } catch (AraneaException e) {
            e.logException(araneaLogger);
            return;
          }
          startCrawling(commands[1]);
        }
        case "list" -> listExtension(commands[1]);
        case "search" -> searchPattern(commands[1]);
        case "interactive" -> interactiveMode();
        default -> {
          if (commands.length == 2 && commands[1] != null)
            AraneaHelper.requestCommandHelp(commands[1]);
          else
            AraneaHelper.requestGenericHelp();
        }
      }

    } catch (Exception e) {
      araneaLogger.log(AraneaLoggerLevels.ERROR, "Invalid or incomplete command!");
      e.getStackTrace();
    }
  }

  /**
   * Constructor implements interaction methods User can interact live with program or Static by
   * inserting a specific commmand
   *
   * @param commands string array of keyword
   * @throws AraneaException if command does not exist
   */
  private Aranea(String[] commands) throws AraneaException {
    initLogger();
    try {
      if (commands != null && commands.length != 0) executeCommand(commands);
      else {
        AraneaHelper.requestGenericHelp();
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

  /** Main class */
  public static void main(String[] argv)
      throws AraneaException, MalformedURLException, FileNotFoundException {
    Aranea aranea = Aranea.getInstance(argv);
  }
}
