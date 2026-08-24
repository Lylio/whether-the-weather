package com.lylechristine;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

// Retrieves weather data from the Open-Meteo API.
public class WeatherApp {

    /**
     * Fetch weather data for a given city/location.
     */
    public static JSONObject getWeatherData(String locationName) {

        if (locationName == null || locationName.trim().isEmpty()) {
            System.out.println("Error: Location name cannot be empty.");
            return null;
        }

        try {
            // Get location information from Open-Meteo's geocoding API.
            JSONArray locationData = getLocationData(locationName);

            if (locationData == null || locationData.isEmpty()) {
                System.out.println("Error: Could not find location: " + locationName);
                return null;
            }

            // Use the first matching location.
            JSONObject location = (JSONObject) locationData.get(0);

            Object latitudeObject = location.get("latitude");
            Object longitudeObject = location.get("longitude");

            if (latitudeObject == null || longitudeObject == null) {
                System.out.println("Error: Location did not contain coordinates.");
                return null;
            }

            double latitude = ((Number) latitudeObject).doubleValue();
            double longitude = ((Number) longitudeObject).doubleValue();

            // Open-Meteo returns the correct timezone for the selected location.
            String timezone = (String) location.get("timezone");

            if (timezone == null || timezone.isEmpty()) {
                timezone = "GMT";
            }

            // Encode the timezone so names such as America/Los_Angeles
            // are safely included in the URL.
            String encodedTimezone = URLEncoder.encode(
                    timezone,
                    StandardCharsets.UTF_8
            );

            // Build the Open-Meteo forecast request.
            //
            // These are the current Open-Meteo variable names:
            // temperature_2m
            // relative_humidity_2m
            // weather_code
            // wind_speed_10m
            String urlString =
                    "https://api.open-meteo.com/v1/forecast?" +
                            "latitude=" + latitude +
                            "&longitude=" + longitude +
                            "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m" +
                            "&timezone=" + encodedTimezone;

            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn == null) {
                System.out.println("Error: Could not connect to Open-Meteo.");
                return null;
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println(
                        "Error: Open-Meteo returned HTTP status " + responseCode
                );
                conn.disconnect();
                return null;
            }

            // Read the API response.
            StringBuilder resultJson = new StringBuilder();

            try (Scanner scanner = new Scanner(
                    conn.getInputStream(),
                    StandardCharsets.UTF_8
            )) {
                while (scanner.hasNextLine()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            conn.disconnect();

            // Parse the JSON response.
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj =
                    (JSONObject) parser.parse(resultJson.toString());

            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            if (hourly == null) {
                System.out.println("Error: Weather API returned no hourly data.");
                return null;
            }

            // Get hourly arrays.
            JSONArray time =
                    (JSONArray) hourly.get("time");

            JSONArray temperatureData =
                    (JSONArray) hourly.get("temperature_2m");

            JSONArray weatherCodeData =
                    (JSONArray) hourly.get("weather_code");

            JSONArray relativeHumidityData =
                    (JSONArray) hourly.get("relative_humidity_2m");

            JSONArray windspeedData =
                    (JSONArray) hourly.get("wind_speed_10m");

            if (time == null ||
                    temperatureData == null ||
                    weatherCodeData == null ||
                    relativeHumidityData == null ||
                    windspeedData == null) {

                System.out.println(
                        "Error: Weather API returned incomplete weather data."
                );
                return null;
            }

            // Find the current hour using the selected city's timezone.
            int index = findIndexOfCurrentTime(time, timezone);

            if (index < 0 || index >= time.size()) {
                System.out.println(
                        "Error: Could not find the current hour in the weather data."
                );
                return null;
            }

            // Get temperature.
            double temperature =
                    ((Number) temperatureData.get(index)).doubleValue();

            // Get weather condition.
            long weatherCode =
                    ((Number) weatherCodeData.get(index)).longValue();

            String weatherCondition =
                    convertWeatherCode(weatherCode);

            // Get humidity.
            long humidity =
                    ((Number) relativeHumidityData.get(index)).longValue();

            // Get wind speed.
            double windspeed =
                    ((Number) windspeedData.get(index)).doubleValue();

            // Build the JSON object used by the application/frontend.
            JSONObject weatherData = new JSONObject();

            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            // Also include useful location information.
            weatherData.put(
                    "location",
                    location.get("name")
            );

            weatherData.put(
                    "country",
                    location.get("country")
            );

            weatherData.put(
                    "timezone",
                    timezone
            );

            return weatherData;

        } catch (Exception e) {
            System.out.println(
                    "Error retrieving weather data for " + locationName
            );
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Retrieves geographic coordinates and timezone for a city/location.
     */
    public static JSONArray getLocationData(String locationName) {

        if (locationName == null || locationName.trim().isEmpty()) {
            return null;
        }

        try {
            // Properly URL-encode the city name.
            String encodedLocation = URLEncoder.encode(
                    locationName.trim(),
                    StandardCharsets.UTF_8
            );

            String urlString =
                    "https://geocoding-api.open-meteo.com/v1/search?" +
                            "name=" + encodedLocation +
                            "&count=10" +
                            "&language=en" +
                            "&format=json";

            HttpURLConnection conn = fetchApiResponse(urlString);

            if (conn == null) {
                System.out.println(
                        "Error: Could not connect to Open-Meteo geocoding API."
                );
                return null;
            }

            int responseCode = conn.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                System.out.println(
                        "Error: Geocoding API returned HTTP status "
                                + responseCode
                );
                conn.disconnect();
                return null;
            }

            StringBuilder resultJson = new StringBuilder();

            try (Scanner scanner = new Scanner(
                    conn.getInputStream(),
                    StandardCharsets.UTF_8
            )) {
                while (scanner.hasNextLine()) {
                    resultJson.append(scanner.nextLine());
                }
            }

            conn.disconnect();

            JSONParser parser = new JSONParser();

            JSONObject resultsJsonObj =
                    (JSONObject) parser.parse(resultJson.toString());

            JSONArray locationData =
                    (JSONArray) resultsJsonObj.get("results");

            return locationData;

        } catch (Exception e) {
            System.out.println(
                    "Error retrieving location data for " + locationName
            );
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Creates an HTTP GET connection to the supplied URL.
     */
    private static HttpURLConnection fetchApiResponse(String urlString) {

        try {
            URL url = new URL(urlString);

            HttpURLConnection conn =
                    (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            // Give the API a reasonable amount of time to respond.
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            conn.setRequestProperty(
                    "Accept",
                    "application/json"
            );

            conn.connect();

            return conn;

        } catch (IOException e) {
            System.out.println(
                    "Error creating connection to weather API."
            );
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Finds the current hourly weather entry using the timezone
     * supplied by Open-Meteo for the selected city.
     */
    private static int findIndexOfCurrentTime(
            JSONArray timeList,
            String timezone
    ) {

        try {
            ZoneId zoneId = ZoneId.of(timezone);

            ZonedDateTime currentDateTime =
                    ZonedDateTime.now(zoneId);

            String currentTime =
                    currentDateTime.format(
                            DateTimeFormatter.ofPattern(
                                    "yyyy-MM-dd'T'HH':00'"
                            )
                    );

            for (int i = 0; i < timeList.size(); i++) {

                String time =
                        (String) timeList.get(i);

                if (time.equals(currentTime)) {
                    return i;
                }
            }

        } catch (Exception e) {
            System.out.println(
                    "Error determining current time for timezone: "
                            + timezone
            );
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Converts Open-Meteo WMO weather codes into readable descriptions.
     */
    private static String convertWeatherCode(long weatherCode) {

        switch ((int) weatherCode) {

            case 0:
                return "Clear";

            case 1:
                return "Mainly Clear";

            case 2:
                return "Partly Cloudy";

            case 3:
                return "Overcast";

            case 45:
            case 48:
                return "Fog";

            case 51:
            case 53:
            case 55:
                return "Drizzle";

            case 56:
            case 57:
                return "Freezing Drizzle";

            case 61:
            case 63:
            case 65:
                return "Rain";

            case 66:
            case 67:
                return "Freezing Rain";

            case 71:
            case 73:
            case 75:
                return "Snow";

            case 77:
                return "Snow Grains";

            case 80:
            case 81:
            case 82:
                return "Rain Showers";

            case 85:
            case 86:
                return "Snow Showers";

            case 95:
                return "Thunderstorm";

            case 96:
            case 99:
                return "Thunderstorm with Hail";

            default:
                return "Unknown";
        }
    }
}
