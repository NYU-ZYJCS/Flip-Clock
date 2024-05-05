import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.awt.geom.RoundRectangle2D;

// CalendarPrinter class to display a calendar
public class CalendarPrinter extends JFrame {
    // Reference to the main demo
    private DesktopWidget desktopWidget;
    // Initial click point
    private Point initialClick;

    // Constructor to initialize the calendar printer
    public CalendarPrinter(DesktopWidget desktopWidget) {
        // Set the desktop widget
        this.desktopWidget = desktopWidget;
        // Initialize the UI
        initializeUI();
        // Setup mouse listeners
        setupMouseListeners();
        // Add key listener to close the window
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    FlipManager.startFadeOut(CalendarPrinter.this, desktopWidget, true);
                }
            }
        });
        // Set focusable to true for key events
        setFocusable(true);
    }

    // Initialize the UI
    private void initializeUI() {
        // Set the default close operation
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set the size of the frame
        setSize(350, 220);
        // Set the location of the frame
        setLocationRelativeTo(null);
        // Set the layout of the frame
        setLayout(new BorderLayout());
        // Remove the window border
        setUndecorated(true);
        // Customize the frame
        customizeFrame();

        // Set the window to always be on top
        setAlwaysOnTop(true);
        JPanel headerPanel = new JPanel();
        // Set the layout of the header panel
        headerPanel.setLayout(new BorderLayout());
        // Set the background color of the header panel
        headerPanel.setOpaque(true); // Make sure the panel is not opaque
        headerPanel.setBackground(new Color(64, 64, 64, 192));  // Adjust color with alpha for transparency

        // Add the month label
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        // Create a label with the month and year
        JLabel monthLabel = new JLabel(String.format(Locale.US, "%1$tB %1$tY", calendar), JLabel.CENTER);
        // Set the font of the label
        monthLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        // Set the border of the label
        monthLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Top, left, bottom, right
        // Set the foreground color of the label
        monthLabel.setForeground(Color.WHITE);
        // Add the month label to the header panel
        headerPanel.add(monthLabel, BorderLayout.NORTH);

        // Adding day of week header
        String[] daysOfWeek = {"S", "M", "T", "W", "T", "F", "S"};
        // Create a panel to hold the days of the week
        JPanel weekDayPanel = new JPanel(new GridLayout(1, 7));
        // Set the background color of the week day panel
        weekDayPanel.setOpaque(false);
        // Set the background color of the week day panel
        weekDayPanel.setBackground(new Color(64, 64, 64, 192));  // Light gray with transparency
        // Add the days of the week
        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            weekDayPanel.add(dayLabel);
        }
        // Add the week day panel to the header panel
        headerPanel.add(weekDayPanel, BorderLayout.CENTER);

        // Create a panel to hold the days of the month, 7 days in a week
        JPanel dayPanel = new JPanel(new GridLayout(0, 7));
        // Set the background color of the day panel
        dayPanel.setOpaque(true);
        // Set the background color of the day panel
        dayPanel.setBackground(new Color(64, 64, 64, 192));

        // Set to start of the month
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        // Get the day of the week
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        // Get the number of days in the month
        int daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // Add empty labels for days before start of month
        for (int i = 1; i < dayOfWeek; i++) {
            JLabel emptyLabel = new JLabel("");
            dayPanel.add(emptyLabel);
        }

        // Add the days of the month
        for (int day = 1; day <= daysInMonth; day++) {
            // Create a label with the day
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            // Set the font of the label
            dayLabel.setForeground(Color.WHITE);
            // Create a container to hold the day label
            JPanel dayContainer = new JPanel(new BorderLayout());
            dayContainer.add(dayLabel);
            // Ensure the container itself does not paint a background
            dayContainer.setOpaque(false);

            // Highlight the current day
            if (day == java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)) {
                // Create a rounded panel with a white background
                RoundedPanel roundedBackground = new RoundedPanel(Color.WHITE, 25);
                roundedBackground.setLayout(new BorderLayout());
                roundedBackground.add(dayLabel);
                // Add the rounded background to the day panel
                dayPanel.add(roundedBackground);
                dayLabel.setForeground(Color.DARK_GRAY); // Set the text color for highlighted day
            } else {
                dayPanel.add(dayContainer);
            }
        }
        // Add the header panel to the frame
        add(headerPanel, BorderLayout.NORTH);
        // Add the day panel to the frame
        add(dayPanel, BorderLayout.CENTER);
    }

    // Setup mouse listeners for dragging the window
    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();  // Save the initial click point
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // Get the last location of the window
                Point lastLocation = WindowManager.getLastPosition() != null ? WindowManager.getLastPosition() : getLocation();
                // Calculate the new location based on the initial click and the current mouse position
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;
                // Calculate the new location
                int X = lastLocation.x + xMoved;
                int Y = lastLocation.y + yMoved;
                // Update the shared location
                WindowManager.updateLocation(new Point(X, Y));
                // Move window to the new position
                setLocation(X, Y);
                repaint();
            }
        });
    }

    // Customize the frame to have rounded corners and transparent background
    public void customizeFrame() {
        // Set the shape of the frame to a rounded rectangle
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
        setBackground(new Color(0, 0, 0, 0)); // Set the background to transparent
    }

    // RoundedPanel class to create a rounded panel
    public static class RoundedPanel extends JPanel {
        // Background color of the panel
        private Color backgroundColor;
        // Corner radius of the panel
        private int cornerRadius;

        // Constructor to initialize the background color and corner radius
        public RoundedPanel(Color backgroundColor, int cornerRadius) {
            this.backgroundColor = backgroundColor;
            this.cornerRadius = cornerRadius;
            // Set the layout to null to allow custom positioning of components
            setOpaque(false);
        }

        // Paint the panel with rounded corners
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Create a rounded rectangle with the specified background color and corner radius
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }
}


