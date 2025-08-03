# Whether the Weather

### Description
A weather application built with Java, Swing and Meteomatics API. This app provides real-time weather information for any location in the world. The JSON weather data is parsed and extracted from the Meteomatics API
using the JSON Simple library. The weather data is then displayed in a user-friendly interface using JavaFX components. To fetch the JSON weather data, the app uses the HTTPURLConnection class, which is a part of Java's built-in library for making HTTP requests to fetch data from external APIs.

<br/>

|              |                                                                                                                                |
|--------------|--------------------------------------------------------------------------------------------------------------------------------|
| Demo Link    | [whether-the-weather.lyle.app](https://whether-the-weather.lyle.app)                                                           |
| Tech Stack   | Java 18                                                                                                                        | JSON Simple | Meteomatics API | JavaFX | HTTPURLConnection |
| Cloud Deploy | ![Azure](https://img.shields.io/badge/microsoft%20azure-0078D4.svg?style=for-the-badge&logo=microsoft%20azure&logoColor=white) |
| Top Language | ![Github Language](https://img.shields.io/github/languages/top/lylio/whether-the-weather?style=for-the-badge)                  |
| Last Commit  | ![Github Commit Activity](https://img.shields.io/github/last-commit/lylio/whether-the-weather/main?style=for-the-badge)        |

### Launch & Structure

#### Launch
1. Open the `whether-the-weather` folder in your IDE
2. Locate the `AppLauncher.java` file in the `src` folder
3. Run the `AppLauncher.java` file
4. The app will launch and display the main application window

#### Structure

1. **AppLauncher.java**
   
    **Description**: The AppLauncher class serves as the entry point for the Weather App. It initializes the GUI and displays the main application window.


2. **WeatherAppGui.java**

    **Description**: The WeatherAppGui class represents the graphical user interface (GUI) of the Weather App. It is responsible for displaying weather information for a specified location.

    **Summary**: This class handles the layout and display of GUI components, including text fields, labels, buttons, and images. It also implements the user interface for entering a location and updating the weather information based on user input.


3. **WeatherApp.java**

   **Description**: The WeatherApp class contains the backend logic for fetching weather data from an external API. It retrieves geographic coordinates for a location, fetches weather data for that location, and provides methods to convert weather codes.

   **Summary**: This class encapsulates the core functionality of the Weather App. It includes methods to fetch weather data and location coordinates, convert weather codes into readable weather conditions, and manage API requests. This class acts as the bridge between the GUI and the external weather data source, ensuring that weather information is retrieved and displayed accurately.


<br >

#### Acknowledgements
This app was built using the work of TapTap's tutorials: https://www.youtube.com/watch?v=8ZcEYv2ezWc & https://github.com/curadProgrammer/WeatherAppGUI-Java
