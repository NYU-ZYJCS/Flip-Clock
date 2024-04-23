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

public class TimePrinter implements Runnable {
    private final JLabel timeLabel;
    private ZoneId zoneId = ZoneId.systemDefault();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private String city;
    private volatile boolean shouldUpdate = true;

    public TimePrinter(JLabel label, String city) {

        this.timeLabel = label;
        this.city = city.replace(" ", "_");
        updateZoneId();
    }

    public void updateZoneId() {
        try {
            String urlCity = URLEncoder.encode(city, StandardCharsets.UTF_8.toString());
            String format = URLEncoder.encode("\"%Z\"", StandardCharsets.UTF_8.toString());
            String url = "https://wttr.in/" + urlCity + "?format=" + format;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String timezone = response.body().replaceAll("\"", "").trim();
            if (!timezone.isEmpty()) {
                this.zoneId = ZoneId.of(timezone);
            }
        } catch (IOException | InterruptedException e) {
            System.out.println("Error retrieving timezone: " + e.getMessage());
        }
    }

    public synchronized void setCity(String newCity) {
        this.city = newCity.replace(" ", "_");
        shouldUpdate = true;
        // Notify the running thread that the city has been updated
        notify();
    }

    public void run() {
        while (!Thread.interrupted()) {
            if (shouldUpdate) {
                synchronized (this) {
                    updateZoneId();
                    shouldUpdate = false;
                }
            }


            ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId).withNano(0);
            String timeText = zonedDateTime.format(DateTimeFormatter.ofPattern("HH:mm:ss"));

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
