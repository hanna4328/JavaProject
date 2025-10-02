package lostfound;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class UserDashboard {

    private GraphicsDevice graphicsDevice;
    private JFrame frame;
    
    // A final variable for the button's uniform width and height
    private static final int BUTTON_WIDTH = 250;
    private static final int BUTTON_HEIGHT = 60;

    public UserDashboard() {
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        frame = new JFrame("User Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(frame);
        } else {
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
        }
        
        JPanel mainPanel = new ImagePanel("dashboard_background.jpg");
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        // Top panel for logout button
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        JButton logoutButton = createStyledButton("Logout");
        topPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Center content panel
        JPanel centerContentPanel = new JPanel();
        centerContentPanel.setOpaque(false);
        centerContentPanel.setLayout(new BoxLayout(centerContentPanel, BoxLayout.Y_AXIS)); // Vertical alignment for elements

        JLabel welcomeLabel = new JLabel("Welcome to the Lost & Found Portal!");
        welcomeLabel.setFont(Theme.FONT_TITLE);
        welcomeLabel.setForeground(Theme.NEON_BLUE); // Heading color set to NEON_BLUE (cyan)
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the label
        
        // Add some space above the heading
        centerContentPanel.add(Box.createVerticalGlue()); // Push content towards center/bottom
        centerContentPanel.add(welcomeLabel);
        centerContentPanel.add(Box.createRigidArea(new Dimension(0, 30))); // Space between heading and button box

        JPanel buttonBoxPanel = new JPanel();
        buttonBoxPanel.setLayout(new BoxLayout(buttonBoxPanel, BoxLayout.Y_AXIS));
        buttonBoxPanel.setOpaque(false);
        buttonBoxPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.NEON_BLUE, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));
        buttonBoxPanel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the button box

        JButton reportLostButton = createStyledButton("Report Lost Item");
        JButton searchFoundButton = createStyledButton("Search Found Items");
        JButton reportFoundButton = createStyledButton("Report Found Item");
        JButton myLostReportsButton = createStyledButton("My Lost Reports");
        
        // Set uniform size for buttons
        setButtonSize(reportLostButton, BUTTON_WIDTH, BUTTON_HEIGHT);
        setButtonSize(searchFoundButton, BUTTON_WIDTH, BUTTON_HEIGHT);
        setButtonSize(reportFoundButton, BUTTON_WIDTH, BUTTON_HEIGHT);
        setButtonSize(myLostReportsButton, BUTTON_WIDTH, BUTTON_HEIGHT);

        buttonBoxPanel.add(reportLostButton);
        buttonBoxPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonBoxPanel.add(searchFoundButton);
        buttonBoxPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonBoxPanel.add(reportFoundButton);
        buttonBoxPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonBoxPanel.add(myLostReportsButton);
        
        centerContentPanel.add(buttonBoxPanel);
        centerContentPanel.add(Box.createVerticalGlue()); // Push content towards center/top

        mainPanel.add(centerContentPanel, BorderLayout.CENTER);

        logoutButton.addActionListener(e -> {
            if (graphicsDevice.getFullScreenWindow() != null) {
                graphicsDevice.setFullScreenWindow(null);
            }
            frame.dispose();
            SwingUtilities.invokeLater(() -> new Loginpg());
        });

        reportLostButton.addActionListener(e -> new ReportLostItem());
        searchFoundButton.addActionListener(e -> new SearchFoundItems());
        reportFoundButton.addActionListener(e -> new ReportFoundItem());
        myLostReportsButton.addActionListener(e -> new MyLostReports());

        frame.add(mainPanel);
        frame.setVisible(true);
    }
    
    // Helper method to set a uniform size for buttons
    private void setButtonSize(JButton button, int width, int height) {
        Dimension size = new Dimension(width, height);
        button.setPreferredSize(size);
        button.setMinimumSize(size);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Ensure buttons are centered within their BoxLayout container
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BACKGROUND_LIGHT);
        button.setForeground(Theme.NEON_BLUE);
        button.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Theme.NEON_PURPLE);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(Theme.NEON_BLUE);
            }
        });

        return button;
    }
}