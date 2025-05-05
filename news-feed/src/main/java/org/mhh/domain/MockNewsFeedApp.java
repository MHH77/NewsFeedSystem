package org.mhh.domain;

/**
 * @auther:MHEsfandiari
 */

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class MockNewsFeedApp {

    private static final String DEFAULT_ANALYZER_HOST = "localhost";
    private static final int DEFAULT_ANALYZER_PORT = 9090;

    public static void main(String[] args) {
        String host = System.getProperty("analyzer.host", DEFAULT_ANALYZER_HOST);
        int port = Integer.parseInt(System.getProperty("analyzer.port", String.valueOf(DEFAULT_ANALYZER_PORT)));

        System.out.println("Mock News Feed starting...");
        System.out.println("Attempting to connect to Analyzer at " + host + ":" + port);

        try (Socket socket = new Socket(host, port)) {


            System.out.println("Successfully connected to News Analyzer.");
            System.out.println("Local Port: " + socket.getLocalPort());
            System.out.println("Remote Address: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());

            // TODO: Start sending news items periodically.

            System.out.println("Connection established (will close shortly in this basic version).");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Feed interrupted while waiting.");
            }

        } catch (UnknownHostException e) {
            System.err.println("Error: Analyzer host not found or DNS resolution failed: " + host);
            System.err.println("Message: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error connecting to News Analyzer at " + host + ":" + port);
            System.err.println("Message: " + e.getMessage());
            System.err.println("Is the News Analyzer server running and listening on the correct port?");
        } catch (SecurityException e) {
            System.err.println("Security Exception: Permission denied to connect to " + host + ":" + port);
            System.err.println("Message: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Invalid port number specified: " + port);
            System.err.println("Message: " + e.getMessage());
        }

        System.out.println("Mock News Feed finished (or failed to connect).");
    }
}