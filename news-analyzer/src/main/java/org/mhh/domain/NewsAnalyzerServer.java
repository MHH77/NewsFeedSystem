package org.mhh.domain;

/**
 * @auther:MHEsfandiari
 */

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class NewsAnalyzerServer {

    private static final int DEFAULT_PORT = 9090;

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    System.err.println("Invalid port number: " + args[0] + ". Using default port " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid port format: " + args[0] + ". Using default port " + DEFAULT_PORT);
                port = DEFAULT_PORT;
            }
        }

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("News Analyzer Server started. Listening on port " + port + "...");

            while (true) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                    // TODO: Handle the client connection (receive data) in a separate thread later.
                    clientSocket.close(); // Temporary: Close immediately for this basic step
                    System.out.println("Client connection closed (temporary).");


                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            } // End of while loop

        } catch (IOException e) {
            System.err.println("Could not start News Analyzer Server on port " + port + ": " + e.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid port specified: " + port + ". " + e.getMessage());
            System.exit(1);
        }
    }
}