import java.awt.*;
import java.awt.event.*;
import static java.awt.Font.DIALOG;
import javax.swing.*;

public class TimeDemo extends JDialog {
    // Label for displaying the time
    JLabel timeLabel;
    // Label for displaying the weather
    JLabel weatherLabel;
    // Label for displaying the weather image
    private JLabel imageLabel;
    // System tray icon
    TrayIcon trayIcon = null;
    // Point for storing the initial click coordinates
    Point initialClick;
    // Default weather location
    private String location = "New_York";

    // Weather printer instance
    private final WeatherPrinter weatherPrinter;
    private final TimePrinter timePrinter;



    public TimeDemo() {
        // Set the window properties
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(500, 200);
        setLayout(new BorderLayout());

        // Initialize the UI components
        initializeUI();

        // Initialize the time printer and start its thread
        timePrinter = new TimePrinter(timeLabel, location);
        Thread timeThread = new Thread(timePrinter);
        timeThread.start();

        // Initialize the weather printer and start its thread
        weatherPrinter = new WeatherPrinter(weatherLabel, imageLabel, location);
        Thread weatherThread = new Thread(weatherPrinter);
        weatherThread.start();

    }

    static class FlickerFreeLabel extends JLabel {
        public FlickerFreeLabel(String text) {
            super(text);
            setOpaque(false);  // Set the label to be non-opaque to maintain transparency
        }

        @Override
        protected void paintComponent(Graphics g) {
            // Ensure the text antialiasing is on for better text rendering
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            super.paintComponent(g);  // Only call super to paint the label text
        }
    }


    private void initializeUI() {
        // Setup time label with FlickerFreeLabel
        timeLabel = new FlickerFreeLabel("");
        timeLabel.setForeground(new Color(255, 255, 255));
        timeLabel.setFont(new Font(DIALOG, Font.BOLD, 80));
        timeLabel.setOpaque(false);

        // Setup weather label similarly
        weatherLabel = new FlickerFreeLabel("");
        weatherLabel.setForeground(new Color(255, 255, 255));
        weatherLabel.setFont(new Font(DIALOG, Font.BOLD, 20));
        weatherLabel.setOpaque(false);

        // Weather image label setup
        imageLabel = new FlickerFreeLabel("");
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(false);

        // Panel for weather and image labels
        JPanel weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.add(imageLabel, BorderLayout.WEST);
        weatherPanel.add(weatherLabel, BorderLayout.CENTER);
        weatherPanel.setOpaque(false);  // Ensure the panel does not paint its background

        // Add components to the dialog
        add(timeLabel, BorderLayout.NORTH);
        add(weatherPanel, BorderLayout.CENTER);

        // Set up the system tray
        setupSystemTray();

        // Set up the mouse listeners
        setupMouseListeners();
    }

    // Set up the system tray
    private void setupSystemTray() {
        // Set the icon image
        ImageIcon imageIcon = new ImageIcon(TimeDemo.class.getResource("/icon.jpg"));

        // Create a system tray icon
        SystemTray tray = SystemTray.getSystemTray();

        // Create a popup menu
        PopupMenu pop = new PopupMenu();

        // Menu Item: set location
        MenuItem setLocationItem = new MenuItem("Set Location");
        setLocationItem.addActionListener(e -> promptForLocation());
        pop.add(setLocationItem);

        // Menu Item: exit
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        pop.add(exitItem);

        // Add the image and popup menu to the tray icon
        trayIcon = new TrayIcon(imageIcon.getImage(), "Clock", pop);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // Set up the mouse listeners
    private void setupMouseListeners() {
        // Add a mouse listener to the window
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        // Add a mouse motion listener to the window
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Get the current cursor location
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // Compute the distance moved by the cursor
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // Move window to the new position
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
                TimeDemo.this.repaint();
            }
        });
    }

    // Prompt for the user to enter a new location
    private void promptForLocation() {
        while (true) {
            String newLocation = JOptionPane.showInputDialog(this, "Enter a City Name (Use Letters Only):", "Set Location", JOptionPane.QUESTION_MESSAGE);

            if (newLocation == null) {
                break;
            }
            newLocation = newLocation.trim();

            if (newLocation.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Input cannot be empty. Please enter a valid city name.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (!newLocation.matches("[A-Za-z\\s]+")) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please enter letters only.", "Error", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            newLocation = newLocation.toLowerCase().replaceAll("\\s+", "_");

            // Update the location
            location = newLocation;
            break;
        }

        // Update the location in the weather thread
        if (location != null && !location.isEmpty()) {
            weatherPrinter.setCity(location);
            weatherLabel.setText("Updating weather for " + location + "...");
            timePrinter.setCity(location);
            TimeDemo.this.repaint();
        }
    }

    public static void main(String[] args) {
        TimeDemo timeDemo = new TimeDemo();
        timeDemo.setVisible(true);
    }
}