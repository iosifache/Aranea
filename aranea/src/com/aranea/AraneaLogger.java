package com.aranea;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.io.IOException;

public class AraneaLogger {

  private static AraneaLogger araneaLogger = null;
  private static FileHandler outputFile = null;
  private static Logger logger = null;
  private static Level minLevel = Level.INFO;

  private AraneaLogger() {}

  public void setOutputFile(String filename) throws LoggerOpenFileException {
    logger = Logger.getLogger("com.aranea.AraneaLogger");
    logger.setUseParentHandlers(false);

    try {
      outputFile = new FileHandler(filename, true);
    } catch (IOException e) {
      throw new LoggerOpenFileException();
    }
    outputFile.setFormatter(new AraneaLoggerFormatter());
    outputFile.setFilter(new AraneaLoggerFilter());

    logger.addHandler(outputFile);
  }

  public void setMinLevel(AraneaLoggerLevels level) {
    minLevel = level.standardLevel;
  }

  public static AraneaLogger getInstance() {
    if (araneaLogger == null) araneaLogger = new AraneaLogger();
    return araneaLogger;
  }

  public void log(AraneaLoggerLevels level, String message) {
    logger.log(level.standardLevel, message);
  }

  public enum AraneaLoggerLevels {
    INFO(Level.INFO),
    WARNING(Level.WARNING),
    ERROR(Level.SEVERE),
    CRITICAL(Level.OFF);

    private Level standardLevel;

    AraneaLoggerLevels(Level level) {
      this.standardLevel = level;
    }

    static String convertStandardToCustomLabel(Level level) {
      if (level == Level.WARNING) return "WARNING";
      else if (level == Level.SEVERE) return "ERROR";
      else if (level == Level.OFF) return "CRITICAL";
      return "INFO";
    }

    static AraneaLoggerLevels createFromIntLevel(int logLevel){
      return switch (logLevel) {
        case 1 -> AraneaLoggerLevels.WARNING;
        case 2 -> AraneaLoggerLevels.ERROR;
        case 3 -> AraneaLoggerLevels.CRITICAL;
        default -> AraneaLoggerLevels.INFO;
      };
    }
  }

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

  private static class AraneaLoggerFilter implements Filter {
    @Override
    public boolean isLoggable(LogRecord record) {
      return record.getLevel().intValue() >= minLevel.intValue();
    }
  }

  static public class LoggerOpenFileException extends AraneaException {
    public LoggerOpenFileException() {
      super(AraneaLoggerLevels.CRITICAL, "The log file could not be opened.");
    }
  }

  private void exemplifyUsage() {

    try {

      // Get instance, set output file and minimum level of an exception to log it and log a message
      AraneaLogger logger = AraneaLogger.getInstance();
      logger.setOutputFile("log.txt");
      logger.setMinLevel(AraneaLoggerLevels.WARNING);
      logger.log(AraneaLoggerLevels.INFO, "Information example");

      // Get the instance again and log a message
      AraneaLogger another_logger = AraneaLogger.getInstance();
      another_logger.log(AraneaLoggerLevels.ERROR, "Error example");

    } catch (AraneaException e) {

      // If an exception is thrown, exit the program
      System.exit(1);
    }
  }
}
