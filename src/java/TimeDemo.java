import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static java.awt.Font.DIALOG;

public class TimeDemo extends JDialog {
    JLabel label1;
    JLabel label2;
    TrayIcon trayIcon = null; // 托盘图标
    Point initialClick; // 鼠标按下时的坐标点

    public TimeDemo() {
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));
        setSize(500, 200);
        setLayout(null);

        label1 = new JLabel("");
        label2 = new JLabel("");
        label1.setForeground(new Color(255, 255, 255));
        label2.setForeground(new Color(255, 255, 255));
        label1.setBounds(0, 0, 500, 100);
        label2.setBounds(0, 100, 500, 100);
        label1.setFont(new Font(DIALOG, Font.BOLD, 100));
        label2.setFont(new Font(DIALOG, Font.BOLD, 40));

        getContentPane().add(label1);
        getContentPane().add(label2);

        // 加载图标资源
        ImageIcon arrowIcon = null;
        java.net.URL imgURL = TimeDemo.class.getResource("/image.jpg");
        if (imgURL != null) {
            arrowIcon = new ImageIcon(imgURL);
            setIconImage(arrowIcon.getImage());
        } else {
            JOptionPane.showMessageDialog(this, "Icon image not found.");
        }

        // 设置系统托盘图标和菜单
        SystemTray tray = SystemTray.getSystemTray();
        ImageIcon icon = new ImageIcon(imgURL);
        PopupMenu pop = new PopupMenu();
        MenuItem exit = new MenuItem("exit");
        pop.add(exit);
        trayIcon = new TrayIcon(icon.getImage(), "时钟", pop);
        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        try {
            tray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }

        // 添加鼠标侦听器到整个窗口
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                initialClick = e.getPoint();
            }
        });

        // 添加鼠标运动侦听器到整个窗口
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                // 获取窗口当前位置
                int thisX = getLocation().x;
                int thisY = getLocation().y;

                // 计算鼠标移动的距离
                int xMoved = e.getX() - initialClick.x;
                int yMoved = e.getY() - initialClick.y;

                // 移动窗口到新位置
                int X = thisX + xMoved;
                int Y = thisY + yMoved;
                setLocation(X, Y);
            }
        });

        // 启动更新时间和天气的线程
        TimePrinter printer = new TimePrinter(label1);
        Thread thread1 = new Thread(printer);
        thread1.start();

        WeatherPrinter weatherPrinter = new WeatherPrinter(label2);
        Thread thread2 = new Thread(weatherPrinter);
        thread2.start();
    }

    public static void main(String[] args) {
        TimeDemo timeDemo = new TimeDemo();
        timeDemo.setVisible(true);
    }
}