package com.lylio.weather;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Locale;

@Service
public class WeatherService {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public WeatherService(ObjectMapper objectMapper) {

        this.restClient = RestClient.builder().build();
        this.objectMapper = objectMapper;
    }

    public WeatherResponse getWeather(String city) {

        if (city == null || city.trim().isEmpty()) {
            throw new WeatherApiException("Please enter a city name.");
        }

        try {

            String cleanCity = city.trim();

            String geocodingUrl =
                    "https://geocoding-api.open-meteo.com/v1/search"
                            + "?name=" + java.net.URLEncoder.encode(
                            cleanCity,
                            java.nio.charset.StandardCharsets.UTF_8)
                            + "&count=1"
                            + "&language=en"
                            + "&format=json";

            String geocodingResponse = restClient.get()
                    .uri(geocodingUrl)
                    .retrieve()
                    .body(String.class);

            JsonNode geocodingJson =
                    objectMapper.readTree(geocodingResponse);

            JsonNode results =
                    geocodingJson.path("results");

            if (!results.isArray() || results.isEmpty()) {
                throw new WeatherApiException(
                        "Unable to find a location called \"" +
                                cleanCity + "\"."
                );
            }

            JsonNode location = results.get(0);

            double latitude =
                    location.path("latitude").asDouble();

            double longitude =
                    location.path("longitude").asDouble();

            String locationName =
                    location.path("name").asText(cleanCity);

            String country =
                    location.path("country").asText("");

            String weatherUrl =
                    "https://api.open-meteo.com/v1/forecast"
                            + "?latitude=" + latitude
                            + "&longitude=" + longitude
                            + "&current="
                            + "temperature_2m,"
                            + "relative_humidity_2m,"
                            + "apparent_temperature,"
                            + "weather_code,"
                            + "wind_speed_10m"
                            + "&timezone=auto";

            String weatherResponse = restClient.get()
                    .uri(weatherUrl)
                    .retrieve()
                    .body(String.class);

            JsonNode weatherJson =
                    objectMapper.readTree(weatherResponse);

            JsonNode current =
                    weatherJson.path("current");

            if (current.isMissingNode()) {
                throw new WeatherApiException(
                        "Unable to retrieve weather data for " +
                                locationName + "."
                );
            }

            double temperature =
                    current.path("temperature_2m").asDouble();

            double feelsLike =
                    current.path("apparent_temperature").asDouble();

            int humidity =
                    current.path("relative_humidity_2m").asInt();

            int weatherCode =
                    current.path("weather_code").asInt();

            double windSpeed =
                    current.path("wind_speed_10m").asDouble();

            String description =
                    getWeatherDescription(weatherCode);

            return new WeatherResponse(
                    locationName,
                    country,
                    temperature,
                    feelsLike,
                    humidity,
                    windSpeed,
                    weatherCode,
                    description
            );

        } catch (WeatherApiException e) {

            throw e;

        } catch (Exception e) {

            throw new WeatherApiException(
                    "Unable to retrieve weather data for " +
                            city + ".",
                    e
            );
        }
    }

    private String getWeatherDescription(int weatherCode) {

        return switch (weatherCode) {

            case 0 -> "Clear sky";

            case 1 -> "Mainly clear";

            case 2 -> "Partly cloudy";

            case 3 -> "Overcast";

            case 45, 48 -> "Foggy";

            case 51, 53, 55 ->
                    "Drizzle";

            case 56, 57 ->
                    "Freezing drizzle";

            case 61, 63, 65 ->
                    "Rain";

            case 66, 67 ->
                    "Freezing rain";

            case 71, 73, 75 ->
                    "Snow";

            case 77 ->
                    "Snow grains";

            case 80, 81, 82 ->
                    "Rain showers";

            case 85, 86 ->
                    "Snow showers";

            case 95 ->
                    "Thunderstorm";

            case 96, 99 ->
                    "Thunderstorm with hail";

            default ->
                    "Unknown weather conditions";
        };
    }
}
