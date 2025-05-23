package org.mhh.analyzer;

/**
 * @auther:MHEsfandiari
 */

import org.mhh.common.NewsItem;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.function.BiConsumer;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final HeadlineAnalyzer analyzer;
    private final BiConsumer<NewsItem, HeadlineAnalyzer.AnalysisResult> analysisRecorder;

    public ClientHandler(Socket socket, HeadlineAnalyzer analyzer, BiConsumer<NewsItem, HeadlineAnalyzer.AnalysisResult> recorder) {
        this.clientSocket = socket;
        this.analyzer = analyzer;
        this.analysisRecorder = recorder;
        System.out.println("Handler created for client: " + getClientIdentifier());
    }

    @Override
    public void run() {
        System.out.println("Handler thread started for: " + getClientIdentifier());
        try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream())) {
            Object receivedObject;
            while (true) {
                receivedObject = in.readObject();
                if (receivedObject instanceof NewsItem) {
                    NewsItem receivedItem = (NewsItem) receivedObject;
                    System.out.println("Received from [" + getClientIdentifier() + "]: " + receivedItem.getHeadline());

                    HeadlineAnalyzer.AnalysisResult result = analyzer.analyze(receivedItem.getHeadline());
                    System.out.println("Analysis for \"" + receivedItem.getHeadline() + "\": " + result);

                    if (analysisRecorder != null) {
                        analysisRecorder.accept(receivedItem, result);
                    }

                } else {
                    System.err.println("Received unexpected object type from "
                            + getClientIdentifier() + ": " + (receivedObject != null ? receivedObject.getClass().getName() : "null"));
                }
            }
        } catch (EOFException e) {
            System.out.println("Client " + getClientIdentifier() + " finished sending data and closed the connection (EOF).");
        } catch (SocketException e) {
            System.err.println("Socket Exception for " + getClientIdentifier() + ": " + e.getMessage() + " (Client likely disconnected)");
        } catch (IOException e) {
            System.err.println("I/O Error handling client " + getClientIdentifier() + ": " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.err.println("Error: Received object of unknown class from " + getClientIdentifier() + ": " + e.getMessage());
        } finally {
            try {
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close();
                    System.out.println("Closed connection for: " + getClientIdentifier());
                }
            } catch (IOException e) {
                System.err.println("Error closing client socket for " + getClientIdentifier() + ": " + e.getMessage());
            }
            System.out.println("Handler thread finished for: " + getClientIdentifier());
        }
    }

    private String getClientIdentifier() {
        if (clientSocket == null) return "Unknown Client";
        return clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
    }
}