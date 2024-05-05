import java.awt.*;

// WindowManager class to handle window positions
public class WindowManager {
    // Default starting position
    private static Point lastPosition = null;
    // Get the last position
    public static Point getLastPosition() {
        return lastPosition;
    }
    // Update the location
    public static void updateLocation(Point newPosition) {
        lastPosition = newPosition;
    }
}