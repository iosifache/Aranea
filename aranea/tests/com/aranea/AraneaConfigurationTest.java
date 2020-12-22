package com.aranea;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.aranea.AraneaLogger.AraneaLoggerLevels;
import com.aranea.AraneaException.ConfigurationMissingKeysException;
import com.aranea.AraneaException.ConfigurationOpenFileException;

class AraneaConfigurationTest {

  @Test
  void parseValidConfig() {

    AraneaConfiguration config = null;
    String[] extensions = {"html", "css", "js"};

    // Parse configuration file
    try {
      config = new AraneaConfiguration("tests/com/aranea/files/configurations/valid.config");
    } catch (AraneaException e) {
      Assertions.fail("File needs to be parsed because it exists and has a valid format");
    }

    // Check configuration members
    Assertions.assertEquals(config.getDownloadDir(), "downloads/");
    Assertions.assertEquals(config.getLogFile(), "log.txt");
    Assertions.assertEquals(config.getLogLevel(), AraneaLoggerLevels.INFO);
    Assertions.assertFalse(config.isSitemapGenerated());
    Assertions.assertEquals(config.getMaxThreads(), 1);
    Assertions.assertEquals(config.getDelay(), 1);
    Assertions.assertArrayEquals(config.getAllowedExtensions(), extensions);
    Assertions.assertEquals(config.getAllowedMaxSize(), 1000000000);
    Assertions.assertEquals(config.getAllowedPattern(), "");
    Assertions.assertTrue(config.isSkipRobotsdottxtFiles());
  }

  @Test
  void detectNonexistentFile() {

    // Try to open nonexistent configuration file
    try {
      AraneaConfiguration config =
          new AraneaConfiguration("nonexistent/path/to/custom.config");
    } catch (ConfigurationOpenFileException e) {
      return;
    } catch (ConfigurationMissingKeysException e) {
      Assertions.fail("File needs to be rejected by a ConfigurationOpenFileException exception");
    }

    // If an exception is not raised, fail the test
    Assertions.fail("File needs to be rejected because it doesn't have the required keys");
  }

  @Test
  void detectInvalidConfig() {

    // Try to open existent (but invalid) configuration file
    try {
      AraneaConfiguration config =
          new AraneaConfiguration("tests/com/aranea/files/configurations/invalid.config");
    } catch (ConfigurationOpenFileException e) {
      Assertions.fail("File needs to be opened because it exists");
    } catch (ConfigurationMissingKeysException e) {
      return;
    }

    // If an exception is not raised, fail the test
    Assertions.fail("File needs to be rejected because it doesn't have the required keys");
  }
}
