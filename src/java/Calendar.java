import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Locale;

public class Calendar extends JFrame {
    private Demo demo;  // 引用Demo窗口
    private Point mouseClickPoint;  // 用于存储鼠标点击位置
    private Point initialClick;

    public Calendar(Demo demo) {
        this.demo = demo;
        initializeUI();
        setupMouseListeners();
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    FadeManager.startFadeOut(Calendar.this, demo, true);
                }
            }
        });

        setFocusable(true);
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setUndecorated(true);  // Remove window chrome
        setBackground(new Color(0, 0, 0, 0));  // Set background to transparent

        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        java.util.Calendar calendar = java.util.Calendar.getInstance();
        JLabel monthLabel = new JLabel(String.format(Locale.US, "%1$tB %1$tY", calendar), JLabel.CENTER);
        monthLabel.setFont(new Font("Arial", Font.BOLD, 24));
        monthLabel.setForeground(Color.WHITE);
        headerPanel.add(monthLabel);

        JPanel dayPanel = new JPanel(new GridLayout(0, 7)); // 7 days in a week
        dayPanel.setOpaque(false);
        dayPanel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1)); // Grid lines

        // Set to start of the month
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1);
        int dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK);
        int daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH);

        // Add empty labels for days before start of month
        for (int i = 1; i < dayOfWeek; i++) {
            JLabel emptyLabel = new JLabel("");
            emptyLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            dayPanel.add(emptyLabel);
        }

        // Add day labels for each day
        for (int day = 1; day <= daysInMonth; day++) {
            JLabel dayLabel = new JLabel(String.valueOf(day), SwingConstants.CENTER);
            dayLabel.setForeground(Color.WHITE);
            dayLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            if (day == java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_MONTH)) {
                dayLabel.setOpaque(true);
                dayLabel.setBackground(Color.LIGHT_GRAY);
            }
            dayPanel.add(dayLabel);
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
}
