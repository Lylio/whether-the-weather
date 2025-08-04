// Retrieve weather data from Meteomatics API - this backend logic will fetch the latest weather data for a location
// from the external API and return it. The GUI will then display the weather information in a user-friendly way

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLOutput;
import java.util.Scanner;

public class WeatherApp {
    // fetch weather data for a given location
    public static JSONObject getWeatherData(String locationName) {
        // get location coordinates using the geolocation API
        JSONArray locationData = getLocationData(locationName);

        // extract longitude and latitude data
        JSONObject location = (JSONObject) locationData.get(0);
        double longitude = (double) location.get("longitude");
        double latitude = (double) location.get("latitude");

        // build API request URL with location coordinates
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
                "latitude=" + latitude + "&longitude=" + longitude +
                "&hourly=temperature_2m,weather_code,wind_speed_10m,relative_humidity_2m&timezone=Europe%2FLondon&wind_speed_unit=mph";

        try {
            // call api can get response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check for response status

            // 200 means the connection was a success
            if(conn.getResponseCode() != 200){
                System.out.println("Error: could not connect to API");
                return null;
            }

            // store resulting JSON data
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(conn.getInputStream());

            // read and store the resulting JSON data into our StringBuilder
            while(scanner.hasNext()){
                // read and store into the StringBuilder
                resultJson.append(scanner.nextLine());
            }

            // close scanner
            scanner.close();

            // close URL connection
            conn.disconnect();

            // parse through our JSON data
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            // retrieve hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            // we want to get the curent hour's data
            // so we need to get the index of the current hour
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexofCurrentHour(time);



            // parse JSON data
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(resultJson.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //retrieves geographic coordinates for a location name
    public static JSONArray getLocationData(String locationName) {
        // replace any whitespace in the location name with a plus sign to adhere to the Meteomatics API format
        // e.g. "New York ->  https://open-meteo.com/en/docs/geocoding-api#name=New+York#
        locationName = locationName.replaceAll("\\s", "+");

        // build API url with location name
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                locationName + "&count=10&language=en&format=json";

        try {
            // call API and get a response
            HttpURLConnection conn = fetchApiResponse(urlString);

            // check response status code
            // 200 means success
            if (conn.getResponseCode() != 200) {
                System.out.println("Error: could not connect to API");
                return null;
            } else {
                // store the API results
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(conn.getInputStream());

                // read and store the resulting JSON data into our StringBuilder
                while (scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                // close scanner
                scanner.close();

                // close URL connection
                conn.disconnect();

                // parse the JSON string into a JSON object
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                // get the list of location data the API generated from the location name
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // couldn't find location
        return null;

    }

    private static HttpURLConnection fetchApiResponse(String urlString){
        try{
            // attempt to connect to the API
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // set request method to GET
            conn.setRequestMethod("GET");

            // connect to our API
            conn.connect();
            return conn;
        }catch(Exception e){
            e.printStackTrace();
        }
        // could not make a connection to the API
        return null;
    }
}
