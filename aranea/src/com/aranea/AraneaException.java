package com.aranea;

import com.aranea.AraneaLogger.AraneaLoggerLevels;
import java.lang.Exception;

public class AraneaException extends Exception {

  private AraneaLoggerLevels level;

  public AraneaException(AraneaLoggerLevels level, String message) {
    super(message);
    this.level = level;
  }

  public void logException(AraneaLogger logger) {
    logger.log(this.level, this.getMessage());
  }

  public AraneaLoggerLevels getLevel(){
    return this.level;
  }

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

  private class ExampleOfChild extends AraneaException {
    public ExampleOfChild() {
      super(AraneaLoggerLevels.ERROR, "An error occurred.");
    }
  }
}
