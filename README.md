# News Feed Analyzer System

This project is a simple yet robust client-server system designed for real-time sentiment analysis of news headlines. The multi-threaded server listens for incoming news headlines from clients, analyzes their sentiment using a weighted keyword scoring system, provides live statistics, and persists the results to a CSV file upon shutdown.

## ✨ Features

-   **Real-time Sentiment Analysis:** Analyzes each incoming headline and classifies it as `POSITIVE`, `NEGATIVE`, or `NEUTRAL`.
-   **Multi-threaded Server:** Utilizes an `ExecutorService` to handle multiple client connections concurrently without blocking the main thread.
-   **Weighted Scoring Analysis:** Implements a scoring mechanism for keywords, providing a more nuanced analysis than simple keyword matching.
-   **Live Console Statistics:** Periodically displays aggregated statistics (total analyzed, percentages of each sentiment) in the server console using a `ScheduledExecutorService`.
-   **Persistent Storage:** Gracefully saves all analyzed items to an `analyzed_news_items.csv` file upon server shutdown using a `ShutdownHook`.
-   **Modular Architecture:** The project is structured into three distinct Maven modules for better organization and separation of concerns:
    -   `news-common`: Shared data models.
    -   `news-feed`: A mock client for sending news items.
    -   `news-analyzer`: The core server and analysis logic.

## 🏗️ System Architecture

The system follows a straightforward client-server architecture based on TCP sockets:

`MockNewsFeedApp (Client)` ➡️ `TCP/IP Socket` ➡️ `NewsAnalyzerServer (Server)`

1.  **`MockNewsFeedApp` (Client)**: A simulator that reads a list of sample headlines and sends them, one by one, over a socket connection to the server.
2.  **`NewsAnalyzerServer` (Server)**:
    -   Listens for client connections on a specified port (default: 9090).
    -   Spawns a new `ClientHandler` thread for each connected client to manage the session.
    -   The `ClientHandler` reads the data, passes it to the `HeadlineAnalyzer` for sentiment scoring, and records the result in a central, thread-safe list (`analyzedItemsStore`).
    -   On shutdown, it writes all in-memory analyzed data to the CSV file.

## 🛠️ Tech Stack

-   **Language:** Java (developed and tested with JDK 17)
-   **Build & Dependency Management:** Apache Maven-   **Networking:** Java TCP/IP Sockets
-   **Concurrency:** Java Concurrency API (`ExecutorService`, `ScheduledExecutorService`, `CopyOnWriteArrayList`)

## 🚀 Getting Started

Follow these instructions to get the project up and running on your local machine.

### Prerequisites

-   **Java Development Kit (JDK)** version 17 or higher.
-   **Apache Maven**.

### Installation & Usage

1.  **Clone the repository:**
    ```bash
    git clone <YOUR-GITHUB-REPO-URL>
    cd NewsFeedSystem
    ```

2.  **Build the project with Maven:**
    This command will compile the code, run tests, and package each module into an executable `JAR` file.
    ```bash
    mvn clean package
    ```

3.  **Run the Analyzer Server:**
    Open a terminal and run the following command. The server will start, display its status, and wait for client connections.
    ```bash
    java -jar news-analyzer/target/news-analyzer-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```
    You will see startup messages and live statistics printed to the console periodically.

4.  **Run the News Feed Client:**
    Open a **new terminal window** and run the command below. The client will connect to the server, send its batch of headlines, and then disconnect. You can run this command multiple times to simulate multiple clients or more data.
    ```bash
    java -jar news-feed/target/news-feed-1.0-SNAPSHOT-jar-with-dependencies.jar
    ```

5.  **Observe the Output:**
    -   **Server Console:** Watch the logs for client connections, real-time analysis of each headline, and the live stats updates.
    -   **Stop the Server & Persist Data:** In the server's terminal, press `Ctrl+C`. This will trigger the shutdown hook, which saves all results to a file named `analyzed_news_items.csv` located in the `news-analyzer/target/` directory.

## 📂 Project Structure

```
NewsFeedSystem/
├── pom.xml             # Main Maven POM for module management
├── news-common/        # Module for shared data models (e.g., NewsItem)
│   └── src/
├── news-feed/          # Module for the mock news feed client
│   └── src/└── news-analyzer/      # Module for the analyzer server and its logic
    └── src/
```

## 🧠 How It Works: The Analysis Logic

-   **Sentiment Scoring:** The `HeadlineAnalyzer` class contains a `Map` of keywords, each assigned a numerical score (e.g., +2 for "breakthrough", +1 for "success", -1 for "problem", -2 for "crisis").
-   **Decision Making:** The total score for a headline is the sum of the scores of all recognized keywords. The final sentiment (`POSITIVE`, `NEGATIVE`, or `NEUTRAL`) is determined by comparing this total score against predefined positive and negative thresholds.

## 🔮 Future Improvements

-   [ ] **Web-based UI:** Develop a simple dashboard using a framework like Spring Boot or SparkJava to visualize the live statistics.
-   [ ] **Database Integration:** Replace the CSV file with a proper database (e.g., PostgreSQL, MySQL) for more robust storage and querying capabilities.
-   [ ] **Advanced NLP:** Enhance the analyzer by:
    -   Implementing stop-word removal (e.g., "a", "the", "is").
    -   Adding word stemming to recognize different forms of a word.
    -   Analyzing n-grams (phrases of 2-3 words) to understand context better.
-   [ ] **Real News Source:** Connect the analyzer to a live data stream from an RSS feed or a service like the Twitter API instead of using the mock client.

## 📄 License

This project is licensed under the MIT License. See the `LICENSE` file for details.
