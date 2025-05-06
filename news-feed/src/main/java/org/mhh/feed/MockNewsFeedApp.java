package org.mhh.feed;

/**
 * @auther:MHEsfandiari
 */


import org.mhh.common.NewsItem;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MockNewsFeedApp {

    private static final String DEFAULT_ANALYZER_HOST = "localhost";
    private static final int DEFAULT_ANALYZER_PORT = 9090;
    private static final int NEWS_ITEMS_TO_SEND = 5;
    private static final long SEND_INTERVAL_MS = 2000;

    public static void main(String[] args) {
        String host = System.getProperty("analyzer.host", DEFAULT_ANALYZER_HOST);
        int port = Integer.parseInt(System.getProperty("analyzer.port", String.valueOf(DEFAULT_ANALYZER_PORT)));

        NewsGenerator generator = new NewsGenerator();

        System.out.println("Mock News Feed starting...");
        System.out.println("Attempting to connect to Analyzer at " + host + ":" + port);

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Successfully connected to News Analyzer.");

            for (int i = 0; i < NEWS_ITEMS_TO_SEND; i++) {
                NewsItem itemToSend = generator.generateNewsItem();
                System.out.println("Generated (" + (i + 1) + "/" + NEWS_ITEMS_TO_SEND + "): " + itemToSend.getHeadline());
                System.out.println("Sending news item to analyzer...");
                out.writeObject(itemToSend);
                out.flush();
                System.out.println("News item sent successfully.");

                if (i < NEWS_ITEMS_TO_SEND - 1) {
                    try {
                        Thread.sleep(SEND_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        System.err.println("Feed interrupted while waiting.");
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
            System.out.println("Finished sending " + NEWS_ITEMS_TO_SEND + " news items.");

        } catch (UnknownHostException e) {
            System.err.println("Error: Analyzer host not found: " + host + " (" + e.getMessage() + ")");
        } catch (IOException e) {
            System.err.println("Error connecting or sending data to Analyzer at " + host + ":" + port);
            System.err.println("Message: " + e.getMessage());
        } catch (SecurityException e) {
            System.err.println("Security Exception connecting to " + host + ":" + port + " (" + e.getMessage() + ")");
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid port number: " + port + " (" + e.getMessage() + ")");
        }

        System.out.println("Mock News Feed finished.");
    }
}