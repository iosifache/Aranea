package com.aranea;

import java.lang.Exception;
import com.aranea.AraneaLogger.AraneaLoggerLevels;

/* Class passed between methods / classes to flag a malfunction */
public class AraneaException extends Exception {

  private AraneaLoggerLevels level;

  /**
   * Constructor
   *
   * @param level Level of exception
   * @param message Message of exception
   */
  public AraneaException(AraneaLoggerLevels level, String message) {
    super(message);
    this.level = level;
  }

  /**
   * Log an exception with a specific logger
   *
   * @param logger Logger to log the exception details (level and message)
   */
  public void logException(AraneaLogger logger) {
    logger.log(this.level, this.getMessage());
  }

  /**
   * Getter for exception level
   *
   * @return Level of exception
   */
  public AraneaLoggerLevels getLevel() {
    return this.level;
  }

  /* Function to exemplify the usage */
  private void exemplifyUsage() {

    try {

      // Get logger instance and set output file
      AraneaLogger logger = AraneaLogger.getInstance();
      logger.setOutputFile("log.txt");

      // Throw a standard exception and log it
      try {
        throw new AraneaException(AraneaLoggerLevels.ERROR, "Error example");
      } catch (AraneaException e) {
        e.logException(logger);
      }
    } catch (AraneaException e) {

      // If an exception is thrown, exit the program
      System.exit(1);
    }
  }

  /* Exception thrown if configuration file cannot be opened */
  public static class ConfigurationOpenFileException extends AraneaException {
    public ConfigurationOpenFileException() {
      super(AraneaLoggerLevels.ERROR, "The configuration file could not be opened.");
    }
  }

  /* Exception thrown if configuration file misses mandatory keys */
  static class ConfigurationMissingKeysException extends AraneaException {
    public ConfigurationMissingKeysException() {
      super(
          AraneaLoggerLevels.ERROR, "The configuration file does not contains all mandatory keys.");
    }
  }

  /* Exception thrown if file cannot be opened */
  static public class LoggerOpenFileException extends AraneaException {
    public LoggerOpenFileException() {
      super(AraneaLoggerLevels.CRITICAL, "The log file could not be opened.");
    }
  }

  public static class CreateDirecoryAraneaException extends AraneaException {
      public CreateDirecoryAraneaException(AraneaLoggerLevels level, String message) {
          super(level, "Error creating the directory" + message);
      }
  }

  public static class ConnectionAraneaException extends AraneaException {
      public ConnectionAraneaException(AraneaLoggerLevels level) {
          super(level, "Error opening connection");
      }
  }

  public static class PageRetrieveAraneaException extends AraneaException {
      public PageRetrieveAraneaException(AraneaLoggerLevels level) {
          super(level, "Error retrieving the page");
      }
  }

  public static class WriteToFileAraneaException extends AraneaException {
      public WriteToFileAraneaException(AraneaLoggerLevels level) {
          super(level, "Error writing content to the directory");
      }
  }

  public static class UnsuccessfullResponseAraneaException extends AraneaException {
      public UnsuccessfullResponseAraneaException(AraneaLoggerLevels level) {
          super(level, "The response was not sucesfull");
      }
  }

  public static class InsertFailAraneaException extends AraneaException {
      public InsertFailAraneaException(AraneaLoggerLevels level) {
          super(level, "Failed to insert in URLQueue");
      }
  }

  public static class RemoveFailAraneaException extends AraneaException {
      public RemoveFailAraneaException(AraneaLoggerLevels level) {
          super(level, "Failed to remove from URLQueue");
      }
  }

  public static class InterruptedAraneaException extends AraneaException {
      public InterruptedAraneaException() {
          super(AraneaLoggerLevels.ERROR, "Interrupted Thread Excetption");
      }
  }

  public static class MalformedURLAraneaException extends AraneaException {
      public MalformedURLAraneaException() {
          super(AraneaLoggerLevels.ERROR, "Malformed URL");
      }
  }

  public static class InterruptedSleepAraneaException extends AraneaException {
      public InterruptedSleepAraneaException(AraneaLoggerLevels level) {
          super(level, "Sleep was interrupted");
      }
  }

  static public class CannotOpenException extends AraneaException {
      public CannotOpenException() {
          super(AraneaLoggerLevels.ERROR, "The File cannot be opened.");
      }
  }

  static class FailedRequestException extends AraneaException {
      public FailedRequestException() {
          super(AraneaLoggerLevels.ERROR, "Failed creation of a request to target website.");
      }
  }

  static class FailedFileReadException extends AraneaException {
      public FailedFileReadException() {
          super(AraneaLoggerLevels.ERROR, "Failed read from target file.");
      }
  }

  static class CannotWriteException extends AraneaException {
      public CannotWriteException() {
          super(AraneaLoggerLevels.ERROR, "Cannot write to file.");
      }
  }

  static class UninitializedQueueAraneaException extends AraneaException {
      public UninitializedQueueAraneaException(AraneaLoggerLevels level) {
          super(level, "UrlQueue is not initialized");
      }
  }

  /* Class to exemplify the inheritance */
  private static class ExampleOfChild extends AraneaException {
    public ExampleOfChild() {
      super(AraneaLoggerLevels.ERROR, "An error occurred.");
    }
  }
}
