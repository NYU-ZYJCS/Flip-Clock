import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import javax.swing.*;

// TimePrinter class to handle updating the time information
public class TimePrinter implements Runnable {
    // Time label to display the time information
    private final JLabel timeLabel;
    // ZoneId to get the time information
    private ZoneId zoneId = ZoneId.systemDefault();
    // HttpClient to make requests
    private final HttpClient httpClient = HttpClient.newHttpClient();
    // City to get the time information
    private String city;
    // Flag to check if the time should be updated
    private volatile boolean shouldUpdate = true;

    // Constructor to initialize the time label and city
    public TimePrinter(JLabel label, String city) {
        this.timeLabel = label;
        this.city = city.replace(" ", "_");
        updateZoneId();
    }

    // Update the zoneId based on the city
    public void updateZoneId() {
        try {
            // Encode the city for the URL
            String urlCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            // Format the URL
            String format = URLEncoder.encode("\"%Z\"", StandardCharsets.UTF_8.toString());
            // Create the URL
            String url = "https://wttr.in/" + urlCity + "?format=" + format;
            // Create the request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            // Send the request and get the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            // Get the timezone from the response
            String timezone = response.body().replaceAll("\"", "").trim();
            if (!timezone.isEmpty()) {
                this.zoneId = ZoneId.of(timezone);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error retrieving timezone: " + e.getMessage());
        }
    }

    // Set the city
    public synchronized void setCity(String newCity) {
        this.city = newCity.replace(" ", "_");
        shouldUpdate = true;
        // Notify the running thread that the city has been updated
        notify();
    }

    // Run method to update the time information
    public void run() {
        while (!Thread.interrupted()) {
            if (shouldUpdate) {
                synchronized (this) {
                    updateZoneId();
                    shouldUpdate = false;
                }
            }
            // Get the current time
            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId).withNano(0);
            String timeText = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            // Update the time label
            SwingUtilities.invokeLater(() -> {
                timeLabel.setText(timeText);
                timeLabel.revalidate(); // to ensure layout is updated
                timeLabel.repaint();    // to repaint the label
            });
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // Properly handle the interrupted exception
                Thread.currentThread().interrupt();
                // Break out of the loop if the thread is interrupted
                break;
            }
        }
    }
}