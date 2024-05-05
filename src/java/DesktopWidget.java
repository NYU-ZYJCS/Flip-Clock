import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

// This file can run the DesktopWidget application

// DesktopWidget class to display a desktop widget
public class DesktopWidget extends JDialog {
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
    // Default font name
    private String currentFontName = "Dialog";
    // Default font size map
    private Map<String, FontSizeConfig> fontSizeConfigs;
    // Default size
    private String currentSize = "Medium";
    // Default color map
    private Map<String, Color> colorConfigs;
    // Default color
    private Color currentColor = Color.WHITE;

    // Weather printer instance
    private final WeatherPrinter weatherPrinter;
    // Time printer instance
    private final TimePrinter timePrinter;
    private CalendarPrinter calendarPrinter;

    // Constructor to initialize the DesktopWidget
    public DesktopWidget() {
        calendarPrinter = new CalendarPrinter(this);
        // Set the window properties
        setWindowProperties();

        // Initialize the font size configuration
        initializeFontSizeConfigs();

        // Initialize the color configuration
        initializeColorConfigs();

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

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    FadeManager.startFadeOut(DesktopWidget.this, calendarPrinter, true);
                }
            }
        });

        // Set focusable to true for key events
        setFocusable(true);
    }

    // Set the window properties
    private void setWindowProperties() {
        // Remove window chrome
        setUndecorated(true);
        // Set the background color to transparent
        setBackground(new Color(0, 0, 0, 0));
        // Set the initial size of the window
        setSize(500, 200);
        // Set the layout manager
        setLayout(new BorderLayout());
        // Set the window to always be on top
        setAlwaysOnTop(true);
    }

    // Font size configuration class
    public static class FontSizeConfig {
        int timeFontSize;
        int weatherFontSize;
        int imageLabelSize;

        FontSizeConfig(int timeFontSize, int weatherFontSize, int imageLabelSize) {
            this.timeFontSize = timeFontSize;
            this.weatherFontSize = weatherFontSize;
            this.imageLabelSize = imageLabelSize;
        }
    }

    // Initialize the font size configuration
    private void initializeFontSizeConfigs() {
        fontSizeConfigs = new HashMap<>();
        fontSizeConfigs.put("Large", new FontSizeConfig(100, 22, 120));
        fontSizeConfigs.put("Medium", new FontSizeConfig(80, 18, 80));
        fontSizeConfigs.put("Small", new FontSizeConfig(60, 15, 40));
    }

    // Initialize the color configuration
    private void initializeColorConfigs() {
        colorConfigs = new HashMap<>();
        // Add color configurations
        colorConfigs.put("Purple", new Color(111, 84, 149));
        colorConfigs.put("Pink", new Color(249, 209, 209));
        colorConfigs.put("Blue", new Color(150, 177, 208));
        colorConfigs.put("Green", new Color(177, 221, 161));
        colorConfigs.put("Yellow", new Color(255, 208, 141));
        colorConfigs.put("Orange", new Color(228, 153, 105));
        colorConfigs.put("Black", new Color(0, 0, 0));
        colorConfigs.put("White", new Color(255, 255, 255));
        colorConfigs.put("Gray", new Color(178, 178, 178));
    }

    // Initialize the UI components
    private void initializeUI() {
        // Fetch the current configuration based on the selected size
        FontSizeConfig config = fontSizeConfigs.get(currentSize);

        // Setup time label with FlickerFreeLabel
        timeLabel = new FlickerFreeLabel("");
        timeLabel.setForeground(currentColor);
        timeLabel.setFont(new Font(currentFontName, Font.BOLD, config.timeFontSize));
        timeLabel.setOpaque(false);

        // Setup weather label FlickerFreeLabel
        weatherLabel = new FlickerFreeLabel("");
        weatherLabel.setForeground(currentColor);
        weatherLabel.setFont(new Font(currentFontName, Font.BOLD, config.weatherFontSize));
        weatherLabel.setOpaque(false);

        // Set up weather image label FlickerFreeLabel
        imageLabel = new FlickerFreeLabel("");
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setOpaque(false);

        // Panel for weather and image labels
        JPanel weatherPanel = new JPanel(new BorderLayout());
        weatherPanel.add(imageLabel, BorderLayout.WEST);
        weatherPanel.add(weatherLabel, BorderLayout.CENTER);
        // Ensure the panel does not paint its background
        weatherPanel.setOpaque(false);

        // Add components to the dialog
        add(timeLabel, BorderLayout.NORTH);
        add(weatherPanel, BorderLayout.CENTER);

        // Set up the system tray
        setupSystemTray();

        // Set up the mouse listeners
        setupMouseListeners();
    }

    // Custom JLabel class to prevent flickering
    public static class FlickerFreeLabel extends JLabel {
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

    // Set up the system tray
    private void setupSystemTray() {
        // Create a popup menu
        PopupMenu pop = new PopupMenu();
        // Menu Item: Set Location
        setLocationToPopMenu(pop);
        // Menu Item: Preferences
        setPreferenceToPopMenu(pop);
        // Menu Item: Exit
        setExitToPopMenu(pop);
        // Initialize and add the system tray icon
        initializeTrayIcon(pop);
    }

    // Add the Set Location menu item to the popup menu
    private void setLocationToPopMenu(PopupMenu pop) {
        // Menu Item: Set Location
        MenuItem setLocationItem = new MenuItem("Set Location");
        setLocationItem.addActionListener(e -> promptForLocation());
        // Add the Set Location menu item to the popup menu
        pop.add(setLocationItem);
    }

    // Add Preferences menu item to the popup menu
    private void setPreferenceToPopMenu(PopupMenu pop) {
        // Menu Item: Preferences Menu
        Menu preferences = new Menu("Preferences");
        // Submenu Item: Set Icon Submenu
        setIconSubmenu(preferences);
        // Submenu Item: Set Font Submenu
        setFontSubmenu(preferences);
        // Submenu Item: Set Size Submenu
        setSizeSubmenu(preferences);
        // Submenu Item: Set Color Submenu
        setColorSubmenu(preferences);
        // Add the Preferences menu to the popup menu
        pop.add(preferences);
    }

    // Set Icon Submenu
    private void setIconSubmenu(Menu preferences) {
        // Submenu Item: Set Icon Submenu
        Menu iconSubmenu = new Menu("Set Icon");
        // Menu items for icon styles
        Map<String, String> iconStyles = new HashMap<>();
        // Add icon styles to the map
        iconStyles.put("Simple", "MenubarIcon/simpleIcon.png");
        iconStyles.put("Cartoon", "MenubarIcon/cartoonIcon.png");
        iconStyles.put("Artistic", "MenubarIcon/artisticIcon.png");
        // Create and add menu items to the submenu
        for (Map.Entry<String, String> entry : iconStyles.entrySet()) {
            MenuItem iconItem = new MenuItem(entry.getKey());
            String iconPath = entry.getValue();
            iconItem.addActionListener(e -> updateTrayIcon(iconPath));
            iconSubmenu.add(iconItem);
        }
        // Add the icon submenu to the preferences menu
        preferences.add(iconSubmenu);
    }

    // Set Font Submenu
    private void setFontSubmenu(Menu preferences){
        // Submenu Item: Set Font Submenu
        Menu fontSubmenu = new Menu("Set Font");
        // Menu Items for Monospaced font
        MenuItem monospaced = new MenuItem("Monospaced");
        monospaced.addActionListener(e -> {
            currentFontName = "Monospaced";
            updateFont(currentFontName);
        });
        // Menu Items for Serif font
        MenuItem serif = new MenuItem("Serif");
        serif.addActionListener(e -> {
            currentFontName = "Serif";
            updateFont(currentFontName);
        });
        // Menu Items for Dialog font
        MenuItem dialog = new MenuItem("Dialog");
        dialog.addActionListener(e -> {
            currentFontName = "Dialog";
            updateFont(currentFontName);
        });
        // Add the font menu items to the submenu
        fontSubmenu.add(monospaced);
        fontSubmenu.add(serif);
        fontSubmenu.add(dialog);
        // Add the font submenu to the preferences menu
        preferences.add(fontSubmenu);
    }

    // Set Size Submenu
    private void setSizeSubmenu(Menu preferences){
        // Submenu Item: Set Size Submenu
        Menu sizeSubmenu = new Menu("Set Size");
        // Menu Items for Large size
        MenuItem large = new MenuItem("Large");
        large.addActionListener(e -> updateSize("Large"));
        // Menu Items for Medium size
        MenuItem medium = new MenuItem("Medium");
        medium.addActionListener(e -> updateSize("Medium"));
        // Menu Items for Small size
        MenuItem small = new MenuItem("Small");
        small.addActionListener(e -> updateSize("Small"));
        // Add the size menu items to the submenu
        sizeSubmenu.add(large);
        sizeSubmenu.add(medium);
        sizeSubmenu.add(small);
        // Add the size submenu to the preferences menu
        preferences.add(sizeSubmenu);
    }

    // Set Color Submenu
    private void setColorSubmenu(Menu preferences){
        // Submenu Item: Set Color Submenu
        Menu colorSubmenu = new Menu("Set Color");
        // Menu Items for color configurations
        colorConfigs.forEach((colorName, colorValue) -> {
            MenuItem colorItem = new MenuItem(colorName);
            colorItem.addActionListener(e -> updateColor(colorName));
            colorSubmenu.add(colorItem);
        });
        // Add the color submenu to the preferences menu
        preferences.add(colorSubmenu);
    }

    // Add the Set Location menu item to the popup menu
    private void setExitToPopMenu(PopupMenu pop) {
        // Menu Item: Exit
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        // Add the Exit menu item to the popup menu
        pop.add(exitItem);
    }

    // Initialize and set the system tray icon
    private void initializeTrayIcon(PopupMenu pop) {
        // Get the system tray
        SystemTray tray = SystemTray.getSystemTray();
        // Set the default icon image
        ImageIcon imageIcon = new ImageIcon(DesktopWidget.class.getResource("MenubarIcon/simpleIcon.png"));
        // Add the image and popup menu to the tray icon
        trayIcon = new TrayIcon(imageIcon.getImage(), "Widget", pop);
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // Method to update the system tray icon
    private void updateTrayIcon(String iconPath) {
        // Get the image URL
        URL imageUrl = DesktopWidget.class.getResource(iconPath);
        // Update the tray icon image
        if (imageUrl != null) {
            ImageIcon newIcon = new ImageIcon(imageUrl);
            trayIcon.setImage(newIcon.getImage());
            trayIcon.setImageAutoSize(true);
        }
    }

    // Update the font of the labels
    private void updateFont(String fontName) {
        // Update the font of time
        Font timeFont = new Font(fontName, Font.BOLD, 80);
        // Update the font of weather
        Font weatherFont = new Font(fontName, Font.BOLD, 20);
        timeLabel.setFont(timeFont);
        weatherLabel.setFont(weatherFont);

        repaint();
    }

    // Update the size of the labels
    private void updateSize(String sizeKey) {
        // Fetch the current configuration based on the selected size
        FontSizeConfig config = fontSizeConfigs.get(sizeKey);
        currentSize = sizeKey;
        // Update the font size of the labels
        Font timeFont = new Font(currentFontName, Font.BOLD, config.timeFontSize);
        Font weatherFont = new Font(currentFontName, Font.BOLD, config.weatherFontSize);
        timeLabel.setFont(timeFont);
        weatherLabel.setFont(weatherFont);
        // Update the image size in WeatherPrinter
        weatherPrinter.setImageSize(config.imageLabelSize);
        weatherPrinter.updateIcon();

        repaint();
    }

    // Update the color of the labels
    private void updateColor(String colorKey) {
        // Update the current color
        currentColor = colorConfigs.get(colorKey);
        timeLabel.setForeground(currentColor);
        weatherLabel.setForeground(currentColor);
        repaint();
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
                // Update the shared location
                setLocation(X, Y);

                DesktopWidget.this.repaint();
            }
        });
    }

    // Prompt for the user to enter a new location
    private void promptForLocation() {
        while (true) {
            // Display an input dialog for the user to enter a new location
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
            // Update the location in the weather printer
            weatherPrinter.setCity(location);
            weatherLabel.setText("Updating weather for " + location + "...");

            // Update the location in the time printer
            timePrinter.setCity(location);
            DesktopWidget.this.repaint();
        }
    }

    public static void main(String[] args) {
        // Run the DesktopWidget
        DesktopWidget desktopWidget = new DesktopWidget();
        // Set the visibility of the DesktopWidget
        desktopWidget.setVisible(true);
    }
}