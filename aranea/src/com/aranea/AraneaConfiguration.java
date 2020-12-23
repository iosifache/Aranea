package com.aranea;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import com.aranea.AraneaLogger.AraneaLoggerLevels;
import com.aranea.AraneaException.ConfigurationMissingKeysException;
import com.aranea.AraneaException.ConfigurationOpenFileException;

/* Class for working with configuration files */
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
  private int maxDownloadedPages = -1;
  private boolean headRequests = false;

  /**
   * Constructor
   *
   * @param filename Filename of the configuration file
   * @throws ConfigurationOpenFileException If the configuration file cannot be opened
   * @throws ConfigurationMissingKeysException If the configuration file misses mandatory keys
   */
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
    this.maxDownloadedPages = Integer.parseInt(this.props.getProperty("max_downloaded_pages",
        Integer.toString(Integer.MAX_VALUE)));
    this.headRequests =
        Boolean.parseBoolean(this.props.getProperty("head_requests", "false"));

    AraneaLogger.getInstance().log(AraneaLoggerLevels.INFO, "Configuration file imported");
  }

  /**
   * Get download directory
   *
   * @return Download directory
   */
  public String getDownloadDir() {
    return this.downloadDir;
  }

  /**
   * Get log file
   *
   * @return Log file
   */
  public String getLogFile() {
    return this.logFile;
  }

  /**
   * Get minimum level for an event to be logged
   *
   * @return Minimum level for an event to be logged
   */
  public AraneaLoggerLevels getLogLevel() {
    return this.logLevel;
  }

  /**
   * Check if the sitemap needs to be generated
   *
   * @return Boolean indicating if the sitemap needs to be generated
   */
  public boolean isSitemapGenerated() {
    return this.isSitemapGenerated;
  }

  /**
   * Get the maximum number of threads
   *
   * @return Maximum number of threads
   */
  public int getMaxThreads() {
    return this.maxThreads;
  }

  /**
   * Get delay between two consecutive requests
   *
   * @return Delay between two consecutive requests
   */
  public int getDelay() {
    return this.delay;
  }

  /**
   * Get allowed extensions for downloaded files
   *
   * @return Allowed extensions for downloaded files
   */
  public String[] getAllowedExtensions() {
    return allowedExtensions;
  }

  /**
   * Get maximum size of a file to be downloaded
   *
   * @return Maximum size of a file to be downloaded
   */
  public int getAllowedMaxSize() {
    return this.allowedMaxSize;
  }

  /**
   * Get mandatory pattern to be matched in a file to be downloaded
   *
   * @return Mandatory pattern to be matched in a file to be downloaded
   */
  public String getAllowedPattern() {
    return this.allowedPattern;
  }

  /**
   * Check if files mentioned in robots.txt will be ignored
   *
   * @return Boolean indicating if files mentioned in robots.txt will be ignored
   */
  public boolean isSkipRobotsdottxtFiles() {
    return this.skipRobotsdottxtFiles;
  }

  /**
   * Get maximum number of pages to be downloaded
   *
   * @return Maximum number of to be downloaded
   */
  public int getMaxDownloadedPages(){ return this.maxDownloadedPages; }

  /**
   * Check if HEAD requests are allowed to check file sizes before download
   *
   * @return Boolean indicating if HEAD requests are allowed to check file sizes before download
   */
  public boolean getHeadRequests(){ return this.headRequests; }

  /* Function to exemplify the usage */
  private void exemplifyUsage() {
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
