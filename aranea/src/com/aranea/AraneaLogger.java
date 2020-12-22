package com.aranea;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

/* Class for logging messages to screen / file */
public class AraneaLogger {

  private static AraneaLogger araneaLogger = null;
  private static FileHandler outputFile = null;
  private static Logger logger = null;
  private static Level minLevel = Level.INFO;

  /**
   * Private constructor (singleton specific)
   */
  private AraneaLogger() {
    // Get logger via name
    logger = Logger.getLogger("com.aranea.AraneaLogger");

    // Create a new handler to print to screen
    StreamHandler handler = new StreamHandler(System.out, new AraneaLoggerFormatter());
    handler.setFilter(new AraneaLoggerFilter());
    logger.setUseParentHandlers(false);
    logger.addHandler(handler);
  }

  /**
   * Set the output file where logs will be written
   *
   * @param filename Filename of the output file
   * @throws LoggerOpenFileException If file cannot be opened
   */
  public void setOutputFile(String filename) throws LoggerOpenFileException {
    // Get logger via name
    logger = Logger.getLogger("com.aranea.AraneaLogger");

    // Close old handler for logging to screen
    Handler[] handlers = logger.getHandlers();
    for (Handler handler : handlers) {
      logger.removeHandler(handler);
    }

    // Create and add a new handler for the given file
    try {
      outputFile = new FileHandler(filename, true);
    } catch (IOException e) {
      throw new LoggerOpenFileException();
    }
    outputFile.setFormatter(new AraneaLoggerFormatter());
    outputFile.setFilter(new AraneaLoggerFilter());
    logger.addHandler(outputFile);
  }

  /**
   * Set minimum level for an event to be logged
   *
   * @param level Minimum level
   */
  public void setMinLevel(AraneaLoggerLevels level) {
    minLevel = level.standardLevel;
  }

  /**
   *
   * Get logger (singleton) instance
   *
   * @return Logger instance
   */
  public static AraneaLogger getInstance() {
    if (araneaLogger == null) araneaLogger = new AraneaLogger();
    return araneaLogger;
  }

  /**
   * Log an event
   *
   * @param level Level of event
   * @param message Message of event
   */
  public void log(AraneaLoggerLevels level, String message) {
    logger.log(level.standardLevel, message);
  }

  /* Class wrapping the native Java logging levels */
  public enum AraneaLoggerLevels {
    INFO(Level.INFO),
    WARNING(Level.WARNING),
    ERROR(Level.SEVERE),
    CRITICAL(Level.OFF);

    private Level standardLevel;

    /**
     * Constructor
     *
     * @param level Native Java level
     */
    AraneaLoggerLevels(Level level) {
      this.standardLevel = level;
    }

    /**
     * Stringify level
     *
     * @param level Level
     * @return String representing the level
     */
    static String convertStandardToCustomLabel(Level level) {
      if (level == Level.WARNING) return "WARNING";
      else if (level == Level.SEVERE) return "ERROR";
      else if (level == Level.OFF) return "CRITICAL";
      return "INFO";
    }

    /**
     * Convert an integer (for example, taken from the configuration file) to a level
     *
     * @param logLevel Integer representing a level
     * @return Level corresponding to integer
     */
    static AraneaLoggerLevels createFromIntLevel(int logLevel){
      return switch (logLevel) {
        case 1 -> AraneaLoggerLevels.WARNING;
        case 2 -> AraneaLoggerLevels.ERROR;
        case 3 -> AraneaLoggerLevels.CRITICAL;
        default -> AraneaLoggerLevels.INFO;
      };
    }
  }

  /* Class for formatting logged messages, inherited from native formatter */
  private static class AraneaLoggerFormatter extends Formatter {
    @Override
    public String format(LogRecord record) {
      return "["
          + (new SimpleDateFormat("dd.MM.yy hh:mm:ss")).format(new Date(record.getMillis()))
          + "] "
          + AraneaLoggerLevels.convertStandardToCustomLabel(record.getLevel())
          + ": "
          + record.getMessage()
          + "\n";
    }
  }

  /* Class for filtering logged messages, inherited from native filter */
  private static class AraneaLoggerFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
      return record.getLevel().intValue() >= minLevel.intValue();
    }
  }

  /* Exception thrown if file cannot be opened */
  static public class LoggerOpenFileException extends AraneaException {
    public LoggerOpenFileException() {
      super(AraneaLoggerLevels.CRITICAL, "The log file could not be opened.");
    }
  }

  /* Function to exemplify the usage */
  private void exemplifyUsage() {

    try {

      // Get instance, set output file and minimum level of an exception to log it and log a message
      AraneaLogger logger = AraneaLogger.getInstance();
      logger.log(AraneaLoggerLevels.INFO, "Information example that wil be logged on screen");
      logger.setOutputFile("log.txt");
      logger.setMinLevel(AraneaLoggerLevels.WARNING);
      logger.log(AraneaLoggerLevels.INFO, "Information example that will not be logged");

      // Get the instance again and log a message
      AraneaLogger another_logger = AraneaLogger.getInstance();
      another_logger.log(AraneaLoggerLevels.ERROR, "Error example");

    } catch (AraneaException e) {

      // If an exception is thrown, exit the program
      System.exit(1);
    }
  }
}
