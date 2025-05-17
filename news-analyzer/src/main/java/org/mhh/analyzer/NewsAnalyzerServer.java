package org.mhh.analyzer;

/**
 * @auther:MHEsfandiari
 */

import org.mhh.common.AnalyzedNewsItem;
import org.mhh.common.NewsItem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NewsAnalyzerServer {

    private static final int DEFAULT_PORT = 9090;
    private static final int MAX_THREADS = 10;
    private static final String CSV_FILE_NAME = "analyzed_news_items.csv";
    private static final DateTimeFormatter CSV_TIMESTAMP_FORMATTER = DateTimeFormatter.ISO_OFFSET_DATE_TIME;


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
            System.out.println("Results will be saved to " + CSV_FILE_NAME + " upon shutdown.");
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

    private void saveAndPrintAnalyzedItems() {
        System.out.println("\n--- Saving and Printing All Analyzed Items (" + this.analyzedItemsStore.size() + ") ---");
        if (this.analyzedItemsStore.isEmpty()) {
            System.out.println("No items were analyzed and stored in this session.");
            return;
        }

        // Print to console        this.analyzedItemsStore.forEach(System.out::println);
        System.out.println("--- End of Console Print ---");

        // Save to CSV
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE_NAME, true)))) { // true for append mode
            boolean fileIsEmpty = new java.io.File(CSV_FILE_NAME).length() == 0;
            if (fileIsEmpty) {
                writer.println("AnalysisTimestamp,Headline,Priority,AnalysisResult"); // CSV Header
            }
            for (AnalyzedNewsItem item : this.analyzedItemsStore) {
                NewsItem original = item.getOriginalItem();
                String headline = (original != null && original.getHeadline() != null) ? original.getHeadline().replace("\"", "\"\"") : ""; // Escape quotes for CSV
                int priority = (original != null) ? original.getPriority() : -1;

                writer.printf("\"%s\",\"%s\",%d,%s%n",
                        item.getAnalysisTimestamp().atZone(java.time.ZoneId.systemDefault()).format(CSV_TIMESTAMP_FORMATTER),
                        headline,
                        priority,
                        item.getResult().name());
            }
            System.out.println("Successfully saved " + this.analyzedItemsStore.size() + " analyzed items to " + CSV_FILE_NAME);
            // Clear the in-memory store after saving if you only want to save new items each run
            // this.analyzedItemsStore.clear(); // Optional: if you want each run to have its own distinct set in memory until shutdown.

        } catch (IOException e) {
            System.err.println("Error writing analyzed items to CSV file " + CSV_FILE_NAME + ": " + e.getMessage());
        }
        System.out.println("--- End of Analyzed Items Processing ---");
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
            System.out.println("Shutdown hook triggered. Saving and printing analyzed items...");
            server.saveAndPrintAnalyzedItems();
        }));

        server.startServer();
    }
}