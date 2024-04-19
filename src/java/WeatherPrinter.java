import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherPrinter implements Runnable {
    private final JLabel label;

    public WeatherPrinter(JLabel label) {
        this.label = label;
    }

    @Override
    public void run() {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS)
                .build();
        String url = "http://wttr.in/Los_Angeles?format=%25C+%25t"; // 获取洛杉矶的天气，格式化为天气条件和温度

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();


        while (true) {
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println(response.body());
                label.setText("LA: " + response.body());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                label.setText("Error retrieving weather data.");
            }

            try {
                Thread.sleep(10 * 60 * 1000); // 每十分钟更新一次
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
}
