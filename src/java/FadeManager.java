import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class FadeManager {
    public static void startFadeOut(Window frameToHide, Window frameToShow, boolean fadeInAfterFadeOut) {
        Point currentLocation = frameToHide.getLocation();
        WindowManager.updateLocation(currentLocation);
        frameToShow.setLocation(currentLocation);

        Timer fadeOutTimer = new Timer(35, new ActionListener() {
            float opacity = 1.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity -= 0.05f;
                if (opacity <= 0f) {
                    ((Timer) e.getSource()).stop();
                    frameToHide.setOpacity(0f);
                    frameToHide.setVisible(false);
                    if (fadeInAfterFadeOut) {
                        startFadeIn(frameToShow);
                    }
                } else {
                    frameToHide.setOpacity(opacity);
                }
            }
        });
        fadeOutTimer.start();
    }

    public static void startFadeIn(Window frameToFadeIn) {
        // Set the location when starting to fade in
        Point currentLocation = WindowManager.getLastPosition();
        frameToFadeIn.setLocation(currentLocation);

        Timer fadeInTimer = new Timer(35, new ActionListener() {
            float opacity = 0.0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                opacity += 0.05f;
                if (opacity >= 1.0f) {
                    ((Timer) e.getSource()).stop();
                    frameToFadeIn.setOpacity(1.0f);
                } else {
                    frameToFadeIn.setOpacity(opacity);
                }
            }
        });
        fadeInTimer.start();
        frameToFadeIn.setVisible(true);
    }
}
