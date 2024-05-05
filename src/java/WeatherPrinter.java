import java.awt.*;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

// WeatherPrinter class to handle updating the weather information
public class WeatherPrinter implements Runnable {
    // Weather label to display the weather information
    private final JLabel weatherLabel;
    // Image label to display the weather icon
    private final JLabel imageLabel;
    // City to get the weather information
    private volatile String city;
    // HttpClient to make requests
    private final HttpClient client;
    // Flag to check if the weather should be updated
    private boolean shouldUpdate = true;
    // Current image size
    private volatile int currentImageSize = 80;
    // Last weather condition
    private String lastWeatherCondition = "";

    // Constructor to initialize the weather label, image label, and location
    public WeatherPrinter(JLabel weatherLabel, JLabel imageLabel, String location) {
        // Set the weather label, image label, and location
        this.weatherLabel = weatherLabel;
        this.imageLabel = imageLabel;
        this.city = location;
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

    // Set the image size
    public void setImageSize(int imageSize) {
        this.currentImageSize = imageSize;
    }

    // Set the city
    public synchronized void setCity(String newCity) {
        this.city = newCity;
        this.shouldUpdate = true;
        notify();
    }

    // Extracts the weather condition from the response
    private String extractWeatherCondition(String response) {
        String weatherPattern = "([^0-9+]+)";
        Matcher matcher = Pattern.compile(weatherPattern).matcher(response);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return "null";
    }

    // Update the weather information
    private void updateWeather() {
        // Get the weather information from the API
        String url = "http://wttr.in/" + city + "?format=%25C+%25t";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        // Send the request and handle the response
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String weatherCondition = extractWeatherCondition(response.body());
            String updatedWeather = city + ": " + response.body();
            SwingUtilities.invokeLater(() -> {
                weatherLabel.repaint();
                weatherLabel.setText(updatedWeather);
                weatherLabel.revalidate();
                weatherLabel.repaint();
                updateWeatherIcon(weatherCondition);
            });
        } catch (IOException | InterruptedException e) {
            SwingUtilities.invokeLater(() -> weatherLabel.setText("Error retrieving weather data."));
        }
    }

    // Map the weather condition to the corresponding icon
    private String mapWeatherConditionToIconName(String weatherCondition) {
        // Convert the weather condition to lowercase
        String condition = weatherCondition.toLowerCase();
        // Check the weather condition and return the corresponding icon
        if (condition.contains("clear") || condition.contains("sunny")) {
            return "WeatherIcon/sunny_clear.png";
        } else if (condition.contains("rain") || condition.contains("showers")) {
            return "WeatherIcon/rain.png";
        } else if (condition.contains("cloudy") || condition.contains("overcast")) {
            return "WeatherIcon/partlycloudy_cloudy.png";
        } else if (condition.contains("thunder") || condition.contains("storm")) {
            return "WeatherIcon/thunder_thunderstorm.png";
        } else if (condition.contains("snow") || condition.contains("blizzard") || condition.contains("flurries")) {
            return "WeatherIcon/flurries_snow_blizzard.png";
        } else if (condition.contains("hail") || condition.contains("hailstone")) {
            return "WeatherIcon/hail_hailstone.png";
        } else if (condition.contains("heatwave") || condition.contains("hot")) {
            return "WeatherIcon/heatwave_hot.png";
        } else if (condition.contains("hurricane") || condition.contains("tornado")) {
            return "WeatherIcon/hurricane_storm_tornado.png";
        } else if (condition.contains("ice") || condition.contains("coldwave") || condition.contains("cold")) {
            return "WeatherIcon/ice_coldwave_cold.png";
        } else if (condition.contains("mist") || condition.contains("frost") || condition.contains("dew")) {
            return "WeatherIcon/mist_frost_dew.png";
        } else if (condition.contains("drizzle")) {
            return "WeatherIcon/drizzle.png";
        } else if (condition.contains("breezy") || condition.contains("windy")) {
            return "WeatherIcon/breezy_windy.png";
        } else if (condition.contains("sand") || condition.contains("sandstorm")) {
            return "WeatherIcon/sand_sandstorm.png";
        } else if (condition.contains("sleet")) {
            return "WeatherIcon/sleet.png";
        } else if (condition.contains("smog") || condition.contains("haze") || condition.contains("fog") || condition.contains("humid")) {
            return "WeatherIcon/smog_haze_fog_humid.png";
        } else if (condition.contains("warm")) {
            return "WeatherIcon/warm.png";
        } else {
            return "WeatherIcon/null.png"; // a default image if no match
        }
    }

    // Update the weather icon
    public void updateIcon() {
        updateWeatherIcon(lastWeatherCondition); // Call the method to update the icon
    }

    // Update the weather icon based on the weather condition
    private void updateWeatherIcon(String weatherCondition) {
        // Set the last weather condition
        lastWeatherCondition = weatherCondition;
        // Map the weather condition to the corresponding icon
        String iconName = mapWeatherConditionToIconName(weatherCondition);
        // Get the image URL
        URL imageUrl = DesktopWidget.class.getResource(iconName);
        // Set the image icon
        if (imageUrl != null) {
            ImageIcon imgIcon = new ImageIcon(imageUrl);
            Image img = imgIcon.getImage();
            Image scaledImg = img.getScaledInstance(currentImageSize, currentImageSize, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(scaledImg));
            imageLabel.revalidate();
            imageLabel.repaint();
        }
    }
    // Run method to update the weather information
    @Override
    public void run() {
        while (!Thread.interrupted()) {
            synchronized (this) {
                while (!shouldUpdate) {
                    try {
                        wait(10 * 60 * 1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
                shouldUpdate = false;
            }
            // Update the weather information
            updateWeather();
        }
    }
}