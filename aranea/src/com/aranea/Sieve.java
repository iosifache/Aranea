package com.aranea;

import com.aranea.AraneaLogger.AraneaLoggerLevels;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Sieve {

  private static Sieve sieve = null;
  private List<String> allowedExtensions;
  private List<String> ignoredPages = new ArrayList<>();
  private String pattern;
  private final int maxSize;

  private Sieve(String[] allowedExtensions, int maxSize, String pattern) {
    this.allowedExtensions = Arrays.asList(allowedExtensions);
    this.maxSize = maxSize;
    this.pattern = pattern;
  }

  public static Sieve getInstance(String[] allowedExtensions, int maxSize, String pattern) {
    if (sieve == null) sieve = new Sieve(allowedExtensions, maxSize, pattern);
    return sieve;
  }

  public void addIgnoredPages(String[] pages) {
    this.ignoredPages.addAll(Arrays.asList(pages));
  }

  public boolean checkURL(String url) throws FailedRequestException {

    boolean isInRobots = this.ignoredPages.contains(url);
    if (isInRobots) return false;

    if (!this.allowedExtensions.contains("*")){
      boolean hasRightExtension =
          this.getFileExtension(url)
              .filter(extension -> this.allowedExtensions.contains(extension))
              .isPresent();
      if (!hasRightExtension) return false;
    }

    return this.getFileSize(url) <= this.maxSize;

  }

  public boolean checkContent(FileInputStream file) throws FailedFileReadException {

    Charset charset = StandardCharsets.UTF_8;
    try {
      byte[] bytes = file.readAllBytes();
      String fileContent = new String(bytes, charset);

      Pattern pattern = Pattern.compile(this.pattern, Pattern.CASE_INSENSITIVE);
      Matcher matcher = pattern.matcher(fileContent);
      return matcher.find();
    } catch (IOException e) {
      throw new FailedFileReadException();
    }
  }

  private Optional<String> getFileExtension(String filename) {
    return Optional.ofNullable(filename)
        .filter(f -> f.contains("."))
        .map(f -> f.substring(filename.lastIndexOf(".") + 1));
  }

  private int getFileSize(String url) throws FailedRequestException {

    URL urlObject = null;
    URLConnection conn = null;

    try {
      urlObject = new URL(url);
      conn = urlObject.openConnection();
      if (conn instanceof HttpURLConnection) {
        ((HttpURLConnection) conn).setRequestMethod("HEAD");
      }
      conn.getInputStream();
      return conn.getContentLength();
    } catch (IOException e) {
      throw new FailedRequestException();
    } finally {
      if (conn instanceof HttpURLConnection) {
        ((HttpURLConnection) conn).disconnect();
      }
    }
  }

  private void exemplifyUsage() {

    String extensions[] = {"html", "css", "js"};
    String ignoredPages[] = {"http://www.columbia.edu/~fdc/contact.html"};

    Sieve sieve = Sieve.getInstance(extensions, 100000, "Content");
    sieve.addIgnoredPages(ignoredPages);
    /*try {
      boolean validURL = sieve.checkURL("http://www.columbia.edu/~fdc/index.html");
    } catch (FailedRequestException e) {
      // Log exception
    }*/

    try {
      File file = new File("index.html");
      boolean validContent = sieve.checkContent(new FileInputStream(file));
    } catch (FileNotFoundException e) {
      // Log exception
    } catch (FailedFileReadException e) {
      // Log exception
    }
  }

  class FailedRequestException extends AraneaException {
    public FailedRequestException() {
      super(AraneaLoggerLevels.ERROR, "Failed creation of a request to target website.");
    }
  }

  private class FailedFileReadException extends AraneaException {
    public FailedFileReadException() {
      super(AraneaLoggerLevels.ERROR, "Failed read from target file.");
    }
  }

}
