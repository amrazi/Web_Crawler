package crawler;

import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CrawlRunnable implements Runnable {
    private Map<String, String> siteTable;
    private Queue<String> queue;
    private Queue<String> nextDepthQueue;
    private boolean isFirst = true;
    private JLabel parsedPagesValLabel;
    private int parsedPagesVal;
    private int depth;


    public CrawlRunnable(Queue<String> queue, Queue<String> nextDepthQueue, Map<String, String> siteTable, JLabel parsedPagesValLabel, int parsedPagesVal, int depth) {
        this.queue = queue;
        this.nextDepthQueue = nextDepthQueue;
        this.siteTable = siteTable;
        this.parsedPagesValLabel = parsedPagesValLabel;
        this.parsedPagesVal = parsedPagesVal;
        this.depth = depth;
    }

    public void run() {

        String url = queue.poll();

        // Don't crawl pages that have already been crawled
        while (siteTable.containsKey(url) && queue.size() > 0) {
            url = queue.poll();
        }
        parsedPagesVal++;
        parsedPagesValLabel.setText(Integer.toString(parsedPagesVal));

        while (url != null) {
            Boolean isValid = true;
            URL URLObj = null;
            try {
                URLObj = new URL(url);
            } catch (MalformedURLException e) {
                System.out.println("MalformedURLException: " + e.toString());
            }
            URLConnection URLConnection = null;
            try {
                URLConnection = URLObj.openConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
            URLConnection.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:63.0) Gecko/20100101 Firefox/63.0");

            if (URLConnection.getContentType() == null) {
                isValid = false;
            }

            if (!URLConnection.getContentType().equals("text/html")) {
                isValid = false;
            }


            if (isValid || isFirst) {
                isFirst = false;
                try (InputStream inputStream = new BufferedInputStream(new URL(url).openStream())) {
                    String siteText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                    parsedPagesVal++;


                    String protocol;

                    if (url.matches("http:/.*")) {
                        protocol = "http";
                    } else if (url.matches("https:/.*")) {
                        protocol = "https";
                    } else {
                        protocol = "error with protocol";
                    }

                    String baseLink;
                    Pattern patternBaseLink = Pattern.compile("^.*/");
                    Matcher matcherBaseLink = patternBaseLink.matcher(url);

                    if (matcherBaseLink.find()) {
                        baseLink = matcherBaseLink.group();
                    } else {
                        baseLink = "error with finding base link";
                    }

                    Pattern patternTitle = Pattern.compile("<title>.*</title>");
                    Matcher matcherTitle = patternTitle.matcher(siteText);

                    String title;
                    if (matcherTitle.find()) {
                        title = matcherTitle.group().replaceAll("<title>", "").replaceAll("</title>", "");
                    } else {
                        title = "None found.";
                    }

                    List<String> matches = new ArrayList<>();

                    Pattern patternHref = Pattern.compile("href=[\"'].*?[\"']");
                    Matcher matcherHref = patternHref.matcher(siteText);

                    Pattern patternAbsLink = Pattern.compile("^http.*", Pattern.CASE_INSENSITIVE);
                    Pattern patternRelativeLinkHtmlSimple = Pattern.compile("^\\w+.?\\w*$", Pattern.CASE_INSENSITIVE);
                    Pattern patternRelativeLinkHtml = Pattern.compile("^/.*$", Pattern.CASE_INSENSITIVE);
                    Pattern patternRelativeLinkHtmlInPage = Pattern.compile("^/?#.*$", Pattern.CASE_INSENSITIVE);
                    Pattern patternNoProtocolSlashes = Pattern.compile("^//.*", Pattern.CASE_INSENSITIVE);
                    Pattern patternNoProtocolNoSlashes = Pattern.compile("(^//|^(?!http).*$).*", Pattern.CASE_INSENSITIVE); //use as last case in if else

                    while (matcherHref.find()) {
                        matches.add(matcherHref.group());
                    }

                    siteTable.put(url, title);

                    for (int i = 0; i < matches.size(); i++) {
                        String rawText = matches.get(i);
                        String text = rawText.replaceAll("href=[\"']", "").replaceAll(">.*", "").replaceAll("[\"']", "");
                        String crawledURL;

                        if (patternAbsLink.matcher(text).find()) {
                            // text is like http.....
                            crawledURL = text;
                        } else if (patternRelativeLinkHtmlSimple.matcher(text).find()) {
                            if (baseLink.charAt(baseLink.length() - 1) == '/') {
                                crawledURL = baseLink.substring(0, baseLink.length() - 1) + "/" + text;
                            } else {
                                crawledURL = baseLink + "/" + text;
                            }
                        } else if (patternRelativeLinkHtml.matcher(text).find()) {
                            if (text.charAt(0) == '/' && baseLink.charAt(baseLink.length() - 1) == '/') {
                                crawledURL = baseLink.substring(0, baseLink.length() - 1) + "/" + text.substring(1);
                            } else if (text.charAt(0) == '/') {
                                crawledURL = baseLink + "/" + text.substring(1);
                            } else if (baseLink.charAt(baseLink.length() - 1) == '/') {
                                crawledURL = baseLink.substring(0, baseLink.length() - 1) + "/" + text;
                            } else {
                                crawledURL = baseLink + "/" + text;
                            }
                        } else if (patternRelativeLinkHtmlInPage.matcher(text).find()) {
                            if (text.charAt(0) == '/' && baseLink.charAt(baseLink.length() - 1) == '/') {
                                crawledURL = baseLink.substring(0, baseLink.length() - 1) + "/" + text.substring(1);
                            } else if (text.charAt(0) == '/') {
                                crawledURL = baseLink + "/" + text.substring(1);
                            } else if (baseLink.charAt(baseLink.length() - 1) == '/') {
                                crawledURL = baseLink.substring(0, baseLink.length() - 1) + "/" + text;
                            } else {
                                crawledURL = baseLink + "/" + text;
                            }
                        } else if (patternNoProtocolSlashes.matcher(text).find()) {
                            crawledURL = protocol + ":" + text;
                        } else if (patternNoProtocolNoSlashes.matcher(text).find()) {
                            crawledURL = protocol + "://" + text;
                        } else {
                            crawledURL = "Error";
                        }

                        nextDepthQueue.add(crawledURL);

                    }

                } catch (IOException ex) {
                    // do nothing
                }
            }
            url = queue.poll();
        }
    }
}
