# Real-Time Data Stream Processing Engine (RTDSPE)

A high-performance, lightweight, distributed message streaming engine built in Java. RTDSPE is designed to handle high-throughput, low-latency real-time data ingestion and processing, adopting concepts similar to Apache Kafka.

---

## 🚀 Key Features

*   **Netty-Powered Networking:** Asynchronous, event-driven network communication framework using Netty.
*   **Custom Storage Layout:** Custom commit logs, log segmenting, and offset indexing for efficient message storage and retrieval.
*   **Decoupled Architecture:** Independent Broker, Producer, and Consumer roles.
*   **Custom Binary Protocol:** Lightweight serialization protocol for minimal overhead.

---

## 🏗️ Architecture & Components

The engine consists of the following key sub-modules:

1.  **`broker`:** The central server responsible for receiving messages, storing them durably, and serving them to consumers.
2.  **`producer`:** Client library to publish streams of records to the broker.
3.  **`consumer`:** Client library to subscribe to topics and pull data from the broker.
4.  **`storage`:** High-performance storage manager handles:
    *   `Log`: Sequential commit log records.
    *   `LogSegment`: Rotation of log files.
    *   `OffsetIndex`: Fast message lookup indices mapping offsets to physical positions.
5.  **`protocol`:** Message structure (`Request`, `Response`, and `MessageType`) serialization.

---

## 🛠️ Technology Stack

*   **Language:** Java 17
*   **Build Tool:** Maven
*   **Networking:** Netty 4.1.111.Final
*   **Testing:** JUnit 5.10.2
*   **Logging:** SLF4J (Simple logger)

---

## ⚙️ Getting Started

### Prerequisites
*   Java Development Kit (JDK) 17 or higher
*   Apache Maven 3.6+

### Build the Project
Compile and build the package:
```bash
mvn clean package
```

### Run Tests
Execute the storage system unit tests:
```bash
mvn test
```
