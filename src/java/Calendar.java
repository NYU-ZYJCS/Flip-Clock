import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;
import java.awt.geom.RoundRectangle2D;

public class Calendar extends JFrame {
    // Reference to the main demo
    private Demo demo;
    // Initial click point
    private Point initialClick;

    public Calendar(Demo demo) {
        this.demo = demo;
        // Initialize the UI
        initializeUI();
        // Setup mouse listeners
        setupMouseListeners();
        // Add key listener to close the window
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    FadeManager.startFadeOut(Calendar.this, demo, true);
                }
            }
        });
        // Set focusable to true for key events
        setFocusable(true);
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 220);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(true);  // Remove window chrome
        customizeFrame();  // Customize the frame

        // Set the window to always be on top
        setAlwaysOnTop(true);
        // Set the opacity to 50%
//        setOpacity(0.5f);

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(true); // Make sure the panel is not opaque
        headerPanel.setBackground(new Color(64, 64, 64, 192));  // Adjust color with alpha for transparency


        java.util.Calendar calendar = java.util.Calendar.getInstance();
        JLabel monthLabel = new JLabel(String.format(Locale.US, "%1$tB %1$tY", calendar), JLabel.CENTER);
        monthLabel.setFont(new Font("Dialog", Font.BOLD, 24));
        monthLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Top, left, bottom, right
        monthLabel.setForeground(Color.WHITE);
        headerPanel.add(monthLabel, BorderLayout.NORTH);

        // Adding day of week header
        String[] daysOfWeek = {"S", "M", "T", "W", "T", "F", "S"};
        JPanel weekDayPanel = new JPanel(new GridLayout(1, 7));
        weekDayPanel.setOpaque(false);
        weekDayPanel.setBackground(new Color(64, 64, 64, 192));  // Light gray with transparency

        for (String day : daysOfWeek) {
            JLabel dayLabel = new JLabel(day, SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            weekDayPanel.add(dayLabel);
        }
        headerPanel.add(weekDayPanel, BorderLayout.CENTER);

        JPanel dayPanel = new JPanel(new GridLayout(0, 7)); // 7 days in a week
        dayPanel.setOpaque(true);
        dayPanel.setBackground(new Color(64, 64, 64, 192));

        // Set to start of the month
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // Add empty labels for days before start of month
        for (int i = 1; i < dayOfWeek; i++) {
            JLabel emptyLabel = new JLabel("");
            dayPanel.add(emptyLabel);
        }

        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);

            JPanel dayContainer = new JPanel(new BorderLayout());
            dayContainer.add(dayLabel);
            dayContainer.setOpaque(false); // Ensuring the container itself does not paint a background

            if (day == java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)) {
                RoundedPanel roundedBackground = new RoundedPanel(Color.WHITE, 25); // Adjust corner radius as needed
                roundedBackground.setLayout(new BorderLayout());
                roundedBackground.add(dayLabel);
                dayPanel.add(roundedBackground);
                dayLabel.setForeground(Color.DARK_GRAY); // Set the text color for highlighted day
            } else {
                dayPanel.add(dayContainer);
            }
        }

        add(headerPanel, BorderLayout.NORTH);
        add(dayPanel, BorderLayout.CENTER);
    }

    private void setupMouseListeners() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();  // Save the initial click point
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point lastLocation = WindowManager.getLastPosition() != null ? WindowManager.getLastPosition() : getLocation();

                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

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

    public void customizeFrame() {
        setUndecorated(true);
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 30, 30));
        setBackground(new Color(0, 0, 0, 0)); // Set the background to transparent
    }
}
