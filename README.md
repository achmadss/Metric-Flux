# Metric Flux

[![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://www.android.com)

MetricFlux is an Android application designed to provide users with insights into their device's usage patterns. It tracks application usage, network data consumption, and presents this information in an easily understandable format.

## Features

-   **App Usage Tracking:** Monitors how frequently and for how long users interact with different apps on their device.
-   **Network Data Consumption:** Tracks the amount of network data used by each app, helping users manage their data usage.
-   **Detailed Usage Statistics:** Presents comprehensive statistics, including daily, weekly and monthly usage summaries.

## Tech Stack

-   **Jetpack Compose** native Android UI library
-   **Voyager** navigation library from https://voyager.adriel.cafe/
-   **Koin** dependency injection


## Development

1. Clone the repository: 
   ```bash 
   git clone https://github.com/achmadss/Metric-Flux.git
   ```
2. Open the project in Android Studio.
3. Build and run the project on an Android emulator or device.

## Permissions

The application requires the following permissions :

*   **Usage Stats Permission** For getting the application usage information.
*   **Mobile network access** For getting network data usage.