import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

public class WeatherPrinter implements Runnable {
    private final JLabel weatherLabel;
    private volatile String city;
    private final HttpClient client;
    private boolean shouldUpdate = true;

    public WeatherPrinter(JLabel weatherLabel, String location) {
        this.weatherLabel = weatherLabel;
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
        String weatherPattern = "([^0-9+]+)\\+";
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
            SwingUtilities.invokeLater(() -> weatherLabel.setText(updatedWeather));
        } catch (IOException | InterruptedException e) {
            SwingUtilities.invokeLater(() -> weatherLabel.setText("Error retrieving weather data."));
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
