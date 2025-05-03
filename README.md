# OrderList - Washing Machine Booking Application

**Project for Android Development Course (KBTU, Spring 2025)**

**Team:**
*   Mustafaev Aslan
*   Sherali Daryn
*   Orazkhan Yerassyl

## Project Description

OrderList is an Android mobile application designed to simplify the process of booking washing machines in shared laundry facilities (e.g., dormitories or residential complexes). Users can view available machines, check their status (free/busy), select a wash cycle and time, and also view their booking history.

## Key Features

*   **Authentication:** User registration and login using Firebase Authentication (Email/Password).
*   **Machine List:** Displays a list of available washing machines with their numbers and current status (free/busy). Data is fetched from MockAPI.
*   **Booking:** Allows booking an available machine by selecting a wash mode (Quick, Manual, Cotton). The machine's status is updated upon booking.
*   **Booking History:** Users can view their past laundry sessions on the Profile screen. Data is stored locally using Room.
*   **Notifications:** Users receive a push notification when their wash cycle is complete (implemented using WorkManager).
*   **Deeplinks:** Supports deeplinks for navigating to the Booking and Profile/History screens (`myapp://orderlist.com/booking`, `myapp://orderlist.com/profile`, `myapp://orderlist.com/booking/history`).
*   **Offline Mode:** Booking history is available for viewing without an internet connection.

## Technology Stack and Architecture

*   **Language:** Kotlin
*   **UI:** Jetpack Compose (Material 3)
*   **Architecture:** MVVM + elements of Clean Architecture (separation into Data and UI layers).
*   **Modularization:** The project is divided into modules:
    *   `:app`: Application entry point, navigation, DI graph.
    *   `:data`: Data layer (repositories, Room, network (Retrofit/OkHttp), WorkManager, Firebase Auth SDK).
    *   `:ui`: Presentation layer (Composable screens, ViewModels, shared UI components, Koin modules for UI).
*   **Asynchronicity:** Kotlin Coroutines + Flow (StateFlow for state management in ViewModels).
*   **Navigation:** Jetpack Navigation Compose.
*   **Network:** Retrofit, OkHttp (with HttpLoggingInterceptor), Gson. API is simulated using MockAPI.io.
*   **Local Storage:** Room Persistence Library.
*   **Background Tasks/Notifications:** WorkManager.
*   **Authentication:** Firebase Authentication.
*   **Dependency Injection (DI):** Koin.
*   **Testing:** JUnit 4, MockK, Turbine, Coroutines Test (for ViewModel Unit tests).
