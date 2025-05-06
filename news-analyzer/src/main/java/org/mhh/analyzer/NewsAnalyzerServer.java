package org.mhh.analyzer;

/**
 * @auther:MHEsfandiari
 */

import org.mhh.common.AnalyzedNewsItem;
import org.mhh.common.NewsItem;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NewsAnalyzerServer {

    private static final int DEFAULT_PORT = 9090;
    private static final int MAX_THREADS = 10;

    private final int port;
    private final ExecutorService clientExecutor;
    private final HeadlineAnalyzer headlineAnalyzer;
    private final List<AnalyzedNewsItem> analyzedItemsStore;

    public NewsAnalyzerServer(int port, int maxThreads) {
        this.port = port;
        this.clientExecutor = Executors.newFixedThreadPool(maxThreads);
        this.headlineAnalyzer = new HeadlineAnalyzer();
        this.analyzedItemsStore = new CopyOnWriteArrayList<>();
    }

    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("News Analyzer Server started on port " + port + ". Max concurrent clients: " + MAX_THREADS);
            System.out.println("Waiting for connections...");

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("\nClient connected: " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort());

                    ClientHandler handlerTask = new ClientHandler(clientSocket, this.headlineAnalyzer, this::recordAnalysis);
                    clientExecutor.submit(handlerTask);

                } catch (IOException e) {
                    if (serverSocket.isClosed()) {
                        System.out.println("Server socket closed, stopping listening.");
                        break;
                    }
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException ioEx) {
            System.err.println("Could not start server on port " + port + ": " + ioEx.getMessage());
        } catch (IllegalArgumentException illegalArgEx) {
            System.err.println("Invalid port specified: " + port + ". " + illegalArgEx.getMessage());
        } finally {
            shutdownClientExecutor();
            System.out.println("News Analyzer Server process finished.");
        }
    }

    public synchronized void recordAnalysis(NewsItem item, HeadlineAnalyzer.AnalysisResult result) {
        AnalyzedNewsItem analyzedItem = new AnalyzedNewsItem(item, result);
        this.analyzedItemsStore.add(analyzedItem);
        System.out.println("SERVER_STORAGE: Recorded item. Total stored: " + this.analyzedItemsStore.size());
    }

    private void shutdownClientExecutor() {
        System.out.println("Attempting to shut down client handler executor...");
        clientExecutor.shutdown();
        try {
            if (!clientExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                clientExecutor.shutdownNow();
                if (!clientExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    System.err.println("Executor did not terminate.");
                }
            }
        } catch (InterruptedException ie) {
            clientExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }
        System.out.println("Client handler executor shutdown complete.");
    }

    private void printAllAnalyzedItems() {
        System.out.println("\n--- All Analyzed Items (" + this.analyzedItemsStore.size() + ") ---");
        if (this.analyzedItemsStore.isEmpty()) {
            System.out.println("No items were analyzed and stored in this session.");
        } else {
            this.analyzedItemsStore.forEach(System.out::println);
        }
        System.out.println("--- End of Analyzed Items ---");
    }

    public static void main(String[] args) {
        int portArg = DEFAULT_PORT;
        if (args.length > 0) {
            try {
                portArg = Integer.parseInt(args[0]);
                if (portArg < 1 || portArg > 65535) {
                    System.err.println("Invalid port: " + args[0] + ". Using default " + DEFAULT_PORT);
                    portArg = DEFAULT_PORT;
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Invalid port format: " + args[0] + ". Using default " + DEFAULT_PORT);
            }
        }

        NewsAnalyzerServer server = new NewsAnalyzerServer(portArg, MAX_THREADS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutdown hook triggered. Shutting down server and printing analyzed items...");
            server.printAllAnalyzedItems();
            // Note: clientExecutor is shut down in the finally block of startServer
            // or if startServer exits normally. If startServer fails very early,
            // the shutdown hook might run before clientExecutor is properly handled.
            // However, the main finally block in startServer should generally cover it.
        }));

        server.startServer();
    }
}