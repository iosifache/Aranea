package com.aranea;

import com.aranea.AraneaLogger.AraneaLoggerLevels;
import com.aranea.AraneaLogger.LoggerOpenFileException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class AraneaLoggerTest {

  @Test
  void logAllToFile() {

    List<String> loggedLines = List.of("First logged line", "Second logged line");
    String logFilePath = "out/test/aranea/com/aranea/all_logs.txt";

    // Set up the logger
    AraneaLogger logger = AraneaLogger.getInstance();
    try {
      logger.setOutputFile(logFilePath);
    } catch (LoggerOpenFileException e) {
      Assertions.fail("File needs to be created / opened by logger");
    }

    // Log lines
    for (String line : loggedLines) {
      logger.log(AraneaLoggerLevels.INFO, line);
    }

    // Read file content
    try {
      String content = new String(Files.readAllBytes(Paths.get(logFilePath)));
      for (String line : loggedLines) {
        Assertions.assertTrue(content.contains(line), "Line not found in output file");
      }
    } catch (IOException e) {
      Assertions.fail("The unit test could not open the file created by logger");
    }
  }

  @Test
  void filteredLogToFile() {

    String[] loggedLines = {"First logged line", "Second logged line", "Third logged line"};
    AraneaLoggerLevels[] levels = {
      AraneaLoggerLevels.ERROR, AraneaLoggerLevels.INFO, AraneaLoggerLevels.CRITICAL
    };
    String logFilePath = "out/test/aranea/com/aranea/filtered_log.txt";

    // Set up the logger
    AraneaLogger logger = AraneaLogger.getInstance();
    try {
      logger.setOutputFile(logFilePath);
    } catch (LoggerOpenFileException e) {
      Assertions.fail("File needs to be created / opened by logger");
    }
    logger.setMinLevel(AraneaLoggerLevels.ERROR);

    // Log lines
    Assertions.assertEquals(levels.length, loggedLines.length);
    for (int i = 0; i < loggedLines.length; i++) {
      logger.log(levels[i], loggedLines[i]);
    }

    // Read file content
    try {
      String content = new String(Files.readAllBytes(Paths.get(logFilePath)));
      for (int i = 0; i < loggedLines.length; i++) {
        boolean isLinePresent = content.contains(loggedLines[i]);
        if (levels[i] == AraneaLoggerLevels.ERROR || levels[i] == AraneaLoggerLevels.CRITICAL)
          Assertions.assertTrue(isLinePresent, "Line not found in output file");
        else Assertions.assertFalse(isLinePresent, "Ignored line found in output file");
      }
    } catch (IOException e) {
      Assertions.fail("The unit test could not open the file created by logger");
    }
  }

  @Test()
  void detectNonexistentLogPath() {
    AraneaLogger logger = AraneaLogger.getInstance();
    try {
      logger.setOutputFile("nonexistent/path/to/logs.txt");
    } catch (LoggerOpenFileException e) {
      return;
    }
    Assertions.fail("Exception not raised");
  }
}
