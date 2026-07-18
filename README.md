# Real-Time Android App: SSE & WebSockets

This project demonstrates how to implement real-time data streaming in Android using two different protocols: **Server-Sent Events (SSE)** and **WebSockets**.

## 🚀 Features

### 1. Wikimedia Live Feed (SSE)
*   **Protocol:** Server-Sent Events (One-way streaming).
*   **Source:** `stream.wikimedia.org`.
*   **Functionality:** Real-time updates of Wikipedia edits from around the globe.
*   **UI:** Includes advanced filtering by wiki, keyword, and bot exclusion, along with live analytics.

### 2. Crypto Tracker (WebSockets)
*   **Protocol:** WebSockets (Two-way / Bi-directional communication).
*   **Source:** `Binance WebSocket API`.
*   **Two-Way Interaction:** Users can send "Subscribe" and "Unsubscribe" commands to the server over the same connection to switch between assets (BTC, ETH, SOL, DOGE) dynamically.
*   **UI:** Live price card with dynamic asset labels and price history.

## 🏗️ Architecture: Clean Architecture + MVI/MVVM
The project follows modern Android development practices:
*   **UI Layer:** Jetpack Compose for declarative UI.
*   **Presentation Layer:** ViewModel with `StateFlow` and `UiState` objects.
*   **Domain Layer:** Use Cases to encapsulate business logic.
*   **Data Layer:** Repositories handling data from `OkHttp` (SSE) and `WebSocket` services.
*   **DI:** Koin 4.0 for dependency injection.

## 🛠️ Technical Stack
*   **Language:** Kotlin
*   **UI:** Jetpack Compose
*   **Networking:** OkHttp & OkHttp-SSE
*   **Serialization:** Kotlinx Serialization
*   **DI:** Koin (4.0.0)
*   **Asynchrony:** Kotlin Coroutines & Flow

## 📖 Key Learnings
*   **SSE** is perfect for one-way streams (like news feeds) because it is lightweight and handles reconnection automatically.
*   **WebSockets** are ideal for interactive apps (like trading or chat) where the client needs to talk back to the server over a low-latency persistent connection.

## 🚦 How to Run
1. Clone the repository.
2. Build in Android Studio (Ladybug or later).
3. Run on an emulator or physical device.
4. Use the bottom navigation to switch between SSE (Wikimedia) and WebSocket (Bitcoin) tabs.
