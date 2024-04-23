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

public class WeatherPrinter implements Runnable {
    private final JLabel weatherLabel;
    private final JLabel imageLabel;
    private volatile String city;
    private final HttpClient client;
    private boolean shouldUpdate = true;

    public WeatherPrinter(JLabel weatherLabel, JLabel imageLabel, String location) {
        this.weatherLabel = weatherLabel;
        this.imageLabel = imageLabel;
        this.city = location;
        this.client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
    }

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

    private void updateWeather() {
        String url = "http://wttr.in/" + city + "?format=%25C+%25t";
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
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

    private String mapWeatherConditionToIconName(String weatherCondition) {
        String condition = weatherCondition.toLowerCase();
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

    private void updateWeatherIcon(String weatherCondition) {
        String iconName = mapWeatherConditionToIconName(weatherCondition);
        URL imageUrl = TimeDemo.class.getResource(iconName);
        if (imageUrl != null) {
            ImageIcon imgIcon = new ImageIcon(imageUrl);
            Image img = imgIcon.getImage();

            Image scaledImg = img.getScaledInstance(80, 80,  Image.SCALE_SMOOTH);
            imageLabel.repaint();
            imageLabel.setIcon(new ImageIcon(scaledImg));
            imageLabel.revalidate();
            imageLabel.repaint();
        }
    }

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
            updateWeather();
        }
    }
}
