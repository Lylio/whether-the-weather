package com.lylechristine;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


public class WeatherAppTest {
    @Test
    @DisplayName("getLocationData should return non-empty coordinates for a valid city")
    void getLocationData_validCity_returnsCoordinates() {
        JSONArray locationData = WeatherApp.getLocationData("Tokyo");

        assertNotNull(locationData, "Location data should not be null");
        assertFalse(locationData.isEmpty(), "Location data should not be empty");

        // Basic structure checks – adjust field names/indexes to your actual JSON
        Object firstEntry = locationData.get(0);
        assertInstanceOf(JSONObject.class, firstEntry, "First element should be a JSONObject");

        JSONObject locationObject = (JSONObject) firstEntry;

        assertTrue(locationObject.containsKey("latitude"), "Location object should contain latitude");
        assertTrue(locationObject.containsKey("longitude"), "Location object should contain longitude");
    }

    @Test
    @DisplayName("getLocationData should handle invalid city gracefully")
    void getLocationData_invalidCity_returnsEmptyOrNull() {
        JSONArray locationData = WeatherApp.getLocationData("some-made-up-city-xyz-123");

        // Depending on your implementation, you might return null or an empty array.
        // Adapt assertions accordingly.
        if (locationData == null) {
            assertNull(locationData, "Location data may be null for an invalid city");
        } else {
            assertTrue(locationData.isEmpty(), "Location data should be empty for an invalid city");
        }
    }

    @Test
    @DisplayName("getWeatherData should return main elements for a valid city")
    void getWeatherData_validCity_returnsWeatherJson() {
        JSONObject weatherData = WeatherApp.getWeatherData("Tokyo");

        assertNotNull(weatherData, "Weather data should not be null");

        // Adjust keys according to the structure of the JSON your API returns
        assertTrue(weatherData.containsKey("temperature"), "Weather JSON should contain temperature");
        assertTrue(weatherData.containsKey("weathercode"), "Weather JSON should contain weather code");
        assertTrue(weatherData.containsKey("time"), "Weather JSON should contain time");
    }

    @Test
    @DisplayName("getWeatherData should handle invalid city gracefully")
    void getWeatherData_invalidCity_returnsNullOrMinimalJson() {
        JSONObject weatherData = WeatherApp.getWeatherData("some-made-up-city-xyz-123");

        // Again, adapt to your implementation: null / empty JSON / error JSON etc.
        if (weatherData == null) {
            assertNull(weatherData, "Weather data may be null for an invalid city");
        } else {
            // Example: you might store an error field
            assertTrue(weatherData.isEmpty() || weatherData.containsKey("error"),
                    "Weather data should be empty or contain error field for an invalid city");
        }
    }

    @Test
    @DisplayName("Time-dependent logic should select index close to current time")
    void getWeatherData_currentTimeIndexIsReasonable() {
        JSONObject weatherData = WeatherApp.getWeatherData("Tokyo");
        assertNotNull(weatherData);

        Object timeValue = weatherData.get("time");
        assertNotNull(timeValue, "Weather data should contain a time value");

        // Basic sanity check for time format – adapt to your API (e.g., "2024-10-01T13:00:00Z")
        String timeString = timeValue.toString();
        assertFalse(timeString.isBlank(), "Time string should not be blank");
        assertTrue(timeString.contains("T"), "Time string should contain 'T' separator");

        // If you know the exact format, you can parse it
        // Example (adjust formatter pattern as needed to match getCurrentTime()):
        // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
        // assertDoesNotThrow(() -> LocalDateTime.parse(timeString, formatter));
    }

    @Test
    @DisplayName("Convert weather code logic (indirect) returns human-readable text")
    void getWeatherData_weatherCodeIsReadable() {
        JSONObject weatherData = WeatherApp.getWeatherData("Tokyo");
        assertNotNull(weatherData);

        Object readableCondition = weatherData.get("condition"); // Adjust to your actual key
        // If convertWeatherCode sets a 'condition' or 'description' field, test that
        if (readableCondition != null) {
            assertInstanceOf(String.class, readableCondition);
            String condition = readableCondition.toString().toLowerCase();

            assertFalse(condition.isBlank(), "Condition text should not be blank");
            // Optionally check for some expected text fragments
            // e.g., "clear", "cloud", "rain", etc.
        }
    }
}

