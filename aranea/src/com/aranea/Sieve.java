package com.aranea;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.aranea.AraneaException.FailedFileReadException;
import com.aranea.AraneaException.FailedRequestException;

public class Sieve {
    private static Sieve sieve = null;
    private List<String> allowedExtensions;
    private List<String> ignoredPages = new ArrayList<>();
    private String pattern;
    private final int maxSize;
    private boolean headRequests;

    private Sieve(String[] allowedExtensions, int maxSize, String pattern, boolean headRequests) {
        this.allowedExtensions = Arrays.asList(allowedExtensions);
        this.maxSize = maxSize;
        this.pattern = pattern;
        this.headRequests = headRequests;
    }

    /**
     *
     * @return
     */
    public static Sieve getInstance() {
        return sieve;
    }

    /**
     * @param allowedExtensions
     * @param maxSize
     * @param pattern
     * @return
     */
    public static Sieve getInstance(String[] allowedExtensions, int maxSize, String pattern, boolean headRequests) {
        if (sieve == null) sieve = new Sieve(allowedExtensions, maxSize, pattern, headRequests);
        return sieve;
    }

    /**
     * @param pages
     */
    public void addIgnoredPages(String[] pages) {
        this.ignoredPages.addAll(Arrays.asList(pages));
    }

    /**
     * @param url
     * @return
     * @throws FailedRequestException
     */
    public boolean checkURL(String url) throws FailedRequestException {

        for (String ignoredPath : this.ignoredPages){
            if (url.startsWith(ignoredPath)) return false;
        }

        if (!this.allowedExtensions.contains("*")) {
            boolean hasRightExtension =
                    this.getFileExtension(url)
                            .filter(extension -> this.allowedExtensions.contains(extension))
                            .isPresent();
            if (url.lastIndexOf("/") == url.length() - 1) return true;

            if (!hasRightExtension) return false;
        }

        if (!this.headRequests)
            return true;
        else
            return this.getFileSize(url) <= this.maxSize;

    }

    /**
     * @param content
     * @return
     * @throws FailedFileReadException
     */
    public boolean checkContent(String content) throws FailedFileReadException {

        // Check if the content matched the pattern
        Pattern pattern = Pattern.compile(this.pattern, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(content);
        boolean match = matcher.find();
        if (!match)
            return false;

        // Check the file size if the HEAD requests are not used
        if (this.headRequests)
            return true;
        return (content.length() < this.maxSize);

    }

    /**
     * @param filename
     * @return
     */
    private Optional<String> getFileExtension(String filename) {

        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1));
    }

    /**
     * @param url
     * @return
     * @throws FailedRequestException
     */
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

    /**
     *
     */
    private void exemplifyUsage() {

        String extensions[] = {"html", "css", "js"};
        String ignoredPages[] = {"http://www.columbia.edu/~fdc/contact.html"};

        Sieve sieve = Sieve.getInstance(extensions, 100000, "con[a-z]*nt", false);
        sieve.addIgnoredPages(ignoredPages);

        String content = "This is an example of content!";
        try {
            boolean validContent = sieve.checkContent(content);
            System.out.println(validContent);
        } catch (FailedFileReadException e) {
            // Log exception
        }

    }

}
