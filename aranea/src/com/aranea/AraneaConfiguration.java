package com.aranea;

import com.aranea.AraneaLogger.AraneaLoggerLevels;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Struct;
import java.util.Properties;

public class AraneaConfiguration {

  private Properties props = null;
  private String downloadDir = null;
  private String logFile = null;
  private AraneaLoggerLevels logLevel = null;
  private boolean isSitemapGenerated = false;
  private int maxThreads = -1;
  private int delay = -1;
  private String[] allowedExtensions = null;
  private int allowedMaxSize = -1;
  private String allowedPattern = "";
  private boolean skipRobotsdottxtFiles = false;

  public AraneaConfiguration(String filename)
      throws ConfigurationOpenFileException, ConfigurationMissingKeysException {
    this.props = new Properties();
    try {
      this.props.load(new FileInputStream(filename));
    } catch (IOException e) {
      throw new ConfigurationOpenFileException();
    }

    this.downloadDir = this.props.getProperty("download_dir");
    this.logFile = this.props.getProperty("log_file");
    if (this.downloadDir == null || this.logFile == null)
      throw new ConfigurationMissingKeysException();
    this.logLevel =
        AraneaLoggerLevels.createFromIntLevel(
            Integer.parseInt(this.props.getProperty("log_level", "0")));
    this.isSitemapGenerated =
        Boolean.parseBoolean(this.props.getProperty("is_sitemap_generated", "true"));
    this.maxThreads = Integer.parseInt(this.props.getProperty("max_threads", "1000"));
    this.delay = Integer.parseInt(this.props.getProperty("delay", "1"));
    this.allowedExtensions = this.props.getProperty("allowed_extensions", "*").split(",");
    this.allowedMaxSize =
        Integer.parseInt(this.props.getProperty("allowed_max_size", "1000000000"));
    this.allowedPattern = this.props.getProperty("allowed_pattern", "");
    this.skipRobotsdottxtFiles =
        Boolean.parseBoolean(this.props.getProperty("skip_robotsdottxt_files", "true"));
  }

  public String getDownloadDir() {
    return this.downloadDir;
  }

  public String getLogFile() {
    return this.logFile;
  }

  public AraneaLoggerLevels getLogLevel() {
    return this.logLevel;
  }

  public boolean isSitemapGenerated() {
    return this.isSitemapGenerated;
  }

  public int getMaxThreads() {
    return this.maxThreads;
  }

  public int getDelay() {
    return this.delay;
  }

  public String[] getAllowedExtensions() {
    return allowedExtensions;
  }

  public int getAllowedMaxSize() {
    return this.allowedMaxSize;
  }

  public String getAllowedPattern() {
    return this.allowedPattern;
  }

  public boolean isSkipRobotsdottxtFiles() {
    return this.skipRobotsdottxtFiles;
  }

  public static class ConfigurationOpenFileException extends AraneaException {
    public ConfigurationOpenFileException() {
      super(AraneaLoggerLevels.ERROR, "The configuration file could not be opened.");
    }
  }

  static class ConfigurationMissingKeysException extends AraneaException {
    public ConfigurationMissingKeysException() {
      super(
          AraneaLoggerLevels.ERROR, "The configuration file does not contains all mandatory keys.");
    }
  }

  private void exemplifyUsage(){
    // Get logger instance and set output file
    AraneaLogger logger = AraneaLogger.getInstance();
    try {
      logger.setOutputFile("log.txt");
    } catch (AraneaException e) {
      System.exit(1);
    }

    try {

      // Create configuration object based on a local file
      AraneaConfiguration configuration = new AraneaConfiguration("aranea.properties");

      // Get properties
      System.out.println(configuration.getDownloadDir());
      System.out.println(configuration.getLogFile());

    } catch (AraneaException e) {
      e.logException(logger);
    }
  }

}
