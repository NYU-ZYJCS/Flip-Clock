import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

// FlipManager class to handle fading in and out of windows
public class FlipManager {
    // Fade out the window
    public static void startFadeOut(Window frameToHide, Window frameToShow, boolean fadeInAfterFadeOut) {
        // Set the location when starting to fade out
        Point currentLocation = frameToHide.getLocation();
        WindowManager.updateLocation(currentLocation);
        frameToShow.setLocation(currentLocation);
        // Set the opacity
        Timer fadeOutTimer = new Timer(35, new ActionListener() {
            float opacity = 1.0f;
            // Decrease the opacity
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
        // Start the timer
        fadeOutTimer.start();
    }

    // Fade in the window
    public static void startFadeIn(Window frameToFadeIn) {
        // Set the location when starting to fade in
        Point currentLocation = WindowManager.getLastPosition();
        frameToFadeIn.setLocation(currentLocation);
        // Set the opacity
        Timer fadeInTimer = new Timer(35, new ActionListener() {
            float opacity = 0.0f;
            // Increase the opacity
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
        // Start the timer
        fadeInTimer.start();
        // Set the window to be visible
        frameToFadeIn.setVisible(true);
    }
}