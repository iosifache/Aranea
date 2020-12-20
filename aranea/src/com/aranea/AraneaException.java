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

  /* Class to exemplify the inheritance */
  private static class ExampleOfChild extends AraneaException {
    public ExampleOfChild() {
      super(AraneaLoggerLevels.ERROR, "An error occurred.");
    }
  }
}
