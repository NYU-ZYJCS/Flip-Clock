import java.awt.*;

public class WindowManager {
    private static Point lastPosition = null; // Default starting position

    public static Point getLastPosition() {
        return lastPosition;
    }

    public static void updateLocation(Point newPosition) {
        lastPosition = newPosition;
    }
}

