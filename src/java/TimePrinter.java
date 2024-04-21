import java.time.LocalTime;
import javax.swing.*;

public class TimePrinter implements Runnable{
    JLabel label = null;
    public TimePrinter(JLabel label){
        this.label=label;
    }

    @Override
    public void run() {

        while (true) {
            LocalTime time = LocalTime.now().withNano(0);
            label.setText(time.toString());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
