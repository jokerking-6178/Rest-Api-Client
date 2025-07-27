package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * Weather API Client - A Java application that fetches weather data from OpenWeatherMap API
 * and displays it in a structured format.
 */
public class WeatherApiClient {

    // OpenWeatherMap API configuration
    private static final String API_BASE_URL = "http://api.openweathermap.org/data/2.5/weather";
    private static final String API_KEY = "d709937e8e849d16f293d05688f6cd61"; // Replace with your actual API key

    // Weather data model
    static class WeatherData {
        private String cityName;
        private String country;
        private double temperature;
        private double feelsLike;
        private int humidity;
        private int pressure;
        private String description;
        private String mainWeather;
        private double windSpeed;
        private int visibility;

        // Constructor
        public WeatherData(String cityName, String country, double temperature,
                           double feelsLike, int humidity, int pressure,
                           String description, String mainWeather,
                           double windSpeed, int visibility) {
            this.cityName = cityName;
            this.country = country;
            this.temperature = temperature;
            this.feelsLike = feelsLike;
            this.humidity = humidity;
            this.pressure = pressure;
            this.description = description;
            this.mainWeather = mainWeather;
            this.windSpeed = windSpeed;
            this.visibility = visibility;
        }

        // Getters
        public String getCityName() { return cityName; }
        public String getCountry() { return country; }
        public double getTemperature() { return temperature; }
        public double getFeelsLike() { return feelsLike; }
        public int getHumidity() { return humidity; }
        public int getPressure() { return pressure; }
        public String getDescription() { return description; }
        public String getMainWeather() { return mainWeather; }
        public double getWindSpeed() { return windSpeed; }
        public int getVisibility() { return visibility; }
    }

    /**
     * Makes HTTP GET request to the weather API
     * @param city The city name to get weather for
     * @return JSON response as string
     * @throws IOException if network error occurs
     */
    private static String makeHttpRequest(String city) throws IOException {
        String urlString = API_BASE_URL + "?q=" + city + "&appid=" + API_KEY + "&units=metric";
        URL url = new URL(urlString);

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new IOException("HTTP Error: " + responseCode + " - " + connection.getResponseMessage());
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }

        reader.close();
        connection.disconnect();

        return response.toString();
    }

    /**
     * Parses JSON response and extracts weather data
     * @param jsonResponse The JSON response from the API
     * @return WeatherData object containing parsed information
     */
    private static WeatherData parseWeatherData(String jsonResponse) {
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(jsonResponse).getAsJsonObject();

        // Extract city information
        String cityName = jsonObject.get("name").getAsString();
        String country = jsonObject.getAsJsonObject("sys").get("country").getAsString();

        // Extract main weather data
        JsonObject main = jsonObject.getAsJsonObject("main");
        double temperature = main.get("temp").getAsDouble();
        double feelsLike = main.get("feels_like").getAsDouble();
        int humidity = main.get("humidity").getAsInt();
        int pressure = main.get("pressure").getAsInt();

        // Extract weather description
        JsonObject weather = jsonObject.getAsJsonArray("weather").get(0).getAsJsonObject();
        String description = weather.get("description").getAsString();
        String mainWeather = weather.get("main").getAsString();

        // Extract wind data
        double windSpeed = jsonObject.getAsJsonObject("wind").get("speed").getAsDouble();

        // Extract visibility (optional field)
        int visibility = jsonObject.has("visibility") ?
                jsonObject.get("visibility").getAsInt() : 0;

        return new WeatherData(cityName, country, temperature, feelsLike,
                humidity, pressure, description, mainWeather,
                windSpeed, visibility);
    }

    /**
     * Displays weather data in a formatted structure
     * @param weather WeatherData object to display
     */
    private static void displayWeatherData(WeatherData weather) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               WEATHER REPORT");
        System.out.println("=".repeat(60));

        System.out.printf("┌─ Location: %s, %s\n", weather.getCityName(), weather.getCountry());
        System.out.println("│");

        System.out.println("├─ Temperature Information:");
        System.out.printf("│  • Current: %.1f°C\n", weather.getTemperature());
        System.out.printf("│  • Feels like: %.1f°C\n", weather.getFeelsLike());
        System.out.println("│");

        System.out.println("├─ Weather Conditions:");
        System.out.printf("│  • Status: %s\n", weather.getMainWeather());
        System.out.printf("│  • Description: %s\n", weather.getDescription());
        System.out.println("│");

        System.out.println("├─ Atmospheric Data:");
        System.out.printf("│  • Humidity: %d%%\n", weather.getHumidity());
        System.out.printf("│  • Pressure: %d hPa\n", weather.getPressure());
        System.out.printf("│  • Wind Speed: %.1f m/s\n", weather.getWindSpeed());

        if (weather.getVisibility() > 0) {
            System.out.printf("│  • Visibility: %.1f km\n", weather.getVisibility() / 1000.0);
        }

        System.out.println("└─" + "─".repeat(58));
    }

    /**
     * Main method - Entry point of the application
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    WEATHER API CLIENT                    ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // Check if API key is configured
        if (API_KEY.equals("ll")) {
            System.out.println("\n⚠️  WARNING: Please configure your OpenWeatherMap API key!");
            System.out.println("   Visit: https://openweathermap.org/api");
            System.out.println("   Replace 'YOUR_API_KEY_HERE' with your actual API key.\n");
        }

        while (true) {
            System.out.print("\nEnter city name (or 'quit' to exit): ");
            String cityInput = scanner.nextLine().trim();

            if (cityInput.equalsIgnoreCase("quit")) {
                System.out.println("\nThank you for using Weather API Client! Goodbye! 👋");
                break;
            }

            if (cityInput.isEmpty()) {
                System.out.println("❌ Please enter a valid city name.");
                continue;
            }

            try {
                System.out.println("\n🔍 Fetching weather data for: " + cityInput);

                // Make API request
                String jsonResponse = makeHttpRequest(cityInput);

                // Parse response
                WeatherData weatherData = parseWeatherData(jsonResponse);

                // Display formatted result
                displayWeatherData(weatherData);

            } catch (IOException e) {
                System.out.println("❌ Network Error: " + e.getMessage());
                System.out.println("   Please check your internet connection and try again.");
            } catch (Exception e) {
                System.out.println("❌ Error: " + e.getMessage());
                System.out.println("   Please check if the city name is correct and try again.");
            }
        }

        scanner.close();
    }
}

/*
 *
 * FEATURES:
 * - Fetches real-time weather data from OpenWeatherMap API
 * - Handles HTTP requests and JSON parsing
 * - Displays data in a structured, user-friendly format
 * - Error handling for network issues and invalid cities
 * - Interactive console interface
 * - Professional formatting with Unicode characters
 */
