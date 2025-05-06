package org.mhh.feed;

/**
 * @auther:MHEsfandiari
 */

import org.mhh.common.NewsItem;

import java.io.IOException;
import java.io.ObjectOutputStream; // Import
import java.net.Socket;
import java.net.UnknownHostException;

public class MockNewsFeedApp {

    private static final String DEFAULT_ANALYZER_HOST = "localhost";
    private static final int DEFAULT_ANALYZER_PORT = 9091;

    public static void main(String[] args) {
        String host = System.getProperty("analyzer.host", DEFAULT_ANALYZER_HOST);
        int port = Integer.parseInt(System.getProperty("analyzer.port", String.valueOf(DEFAULT_ANALYZER_PORT)));

        NewsGenerator generator = new NewsGenerator(); // Create generator

        System.out.println("Mock News Feed starting...");
        System.out.println("Attempting to connect to Analyzer at " + host + ":" + port);

        try (Socket socket = new Socket(host, port);

             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            System.out.println("Successfully connected to News Analyzer.");

            NewsItem itemToSend = generator.generateNewsItem();
            System.out.println("Generated news item: " + itemToSend);

            System.out.println("Sending news item to analyzer...");
            out.writeObject(itemToSend);
            out.flush();

            System.out.println("News item sent successfully.");


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