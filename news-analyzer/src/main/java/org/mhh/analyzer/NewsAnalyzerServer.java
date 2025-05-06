package org.mhh.analyzer;

/**
 * @auther:MHEsfandiari
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Make sure ClientHandler is in the same package 'org.mhh.domain'
// or imported correctly if it's in a different one.
// e.g., import com.example.news.analyzer.ClientHandler; (if it was there)

public class NewsAnalyzerServer {

    private static final int DEFAULT_PORT = 9091;
    private static final int MAX_THREADS = 10; // Limit number of concurrent handlers

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                if (port < 1 || port > 65535) {
                    System.err.println("Invalid port: " + args[0] + ". Using default " + DEFAULT_PORT);
                    port = DEFAULT_PORT;
                }
            } catch (NumberFormatException nfe) { // Used nfe for clarity
                System.err.println("Invalid port format: " + args[0] + ". Using default " + DEFAULT_PORT);
                port = DEFAULT_PORT;            }
        }

        ExecutorService clientExecutor = Executors.newFixedThreadPool(MAX_THREADS);

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("News Analyzer Server started on port " + port + ". Max concurrent clients: " + MAX_THREADS);
            System.out.println("Waiting for connections...");

            while (!Thread.currentThread().isInterrupted()) {
                try {                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\nClient connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                    // Assuming ClientHandler is in org.mhh.domain package
                    ClientHandler handlerTask = new ClientHandler(clientSocket);
                    clientExecutor.submit(handlerTask);

                } catch (IOException e) { // This 'e' is for the inner try-catch (accepting connection)
                    if (serverSocket.isClosed()) {
                        System.out.println("Server socket closed, stopping listening.");
                        break;
                    }
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }

        } catch (IOException ioEx) {
            System.err.println("Could not start server on port " + port + ": " + ioEx.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException illegalArgEx) {
            System.err.println("Invalid port specified: " + port + ". " + illegalArgEx.getMessage());
            System.exit(1);
        } finally {
            System.out.println("Shutting down client handler executor...");
            clientExecutor.shutdown();
            System.out.println("Executor shutdown complete.");
            System.out.println("News Analyzer Server stopped.");
        }
    }
}