package lostfound;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

public class UserDashboard {

    private JFrame frame;
    private static final int WINDOW_WIDTH = 1200; // Increased overall window size
    private static final int WINDOW_HEIGHT = 750;
    
    // NEW DIMENSION: Increased width for the central navigation box
    private static final int NAV_BOX_WIDTH = 800; 
    private static final int BUTTON_HEIGHT = 60; // Slightly taller buttons

    public UserDashboard() {
        frame = new JFrame("User Dashboard");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Use fixed size and center it
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null); 

        // Use ImagePanel for background
        JPanel backgroundPanel = new ImagePanel("dashboard_background.png");
        backgroundPanel.setLayout(new BorderLayout()); 
        frame.add(backgroundPanel);

        // --- Top Header Panel (Contains Logout Link ONLY) ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(20, 50, 0, 50)); 

        // Logout Link (Placed at the East of the top panel)
        JLabel logoutLink = createLogoutLink();
        headerPanel.add(logoutLink, BorderLayout.EAST);
        
        backgroundPanel.add(headerPanel, BorderLayout.NORTH);

        // --- Main Content Area (Heading + Central Box) ---
        JPanel mainContentArea = new JPanel();
        mainContentArea.setOpaque(false);
        mainContentArea.setLayout(new BoxLayout(mainContentArea, BoxLayout.Y_AXIS));
        
        // 1. HEADING (Placed BENEATH the Logout line, centered)
        JLabel welcomeLabel = new JLabel("Welcome to the Lost & Found Portal!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 36));
        welcomeLabel.setForeground(Theme.ACCENT_PRIMARY);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT); // Center alignment
        
        mainContentArea.add(Box.createVerticalStrut(20)); // Space below Logout line
        mainContentArea.add(welcomeLabel);
        mainContentArea.add(Box.createVerticalStrut(40)); // Space above the Navigation Box

        // 2. CENTRAL NAVIGATION BOX (Glassmorphism Container)
        JPanel centerWrapper = new JPanel(new GridBagLayout()); // Use GridBag to center the Box horizontally
        centerWrapper.setOpaque(false);
        
        JPanel navBox = createNavigationBox();
        centerWrapper.add(navBox); // NavBox is centered

        // Add the centered box wrapper to the main content area
        mainContentArea.add(centerWrapper);
        
        // Place the entire content structure into the CENTER of the background panel
        backgroundPanel.add(mainContentArea, BorderLayout.CENTER);


        // --- Action Listeners ---
        logoutLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
                new Loginpg();
            }
        });

        frame.setVisible(true);
    }
    
    /**
     * Creates the main navigation box with full-width buttons.
     */
    private JPanel createNavigationBox() {
        // --- Central Box (Glassmorphism Effect) ---
        JPanel navBox = new JPanel();
        navBox.setLayout(new BoxLayout(navBox, BoxLayout.Y_AXIS));
        
        // Background matching theme
        navBox.setBackground(Theme.BACKGROUND_LIGHT);
        navBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.ACCENT_PRIMARY, 2, true),
            new EmptyBorder(40, 40, 40, 40) // Internal padding
        ));
        
        // Use the new, wider preferred size
        navBox.setPreferredSize(new Dimension(NAV_BOX_WIDTH, 400)); 
        navBox.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create Navigation Buttons
        JButton reportLostButton = createNavigationButton("Report Lost Item");
        JButton searchFoundButton = createNavigationButton("Search Found Items");
        JButton reportFoundButton = createNavigationButton("Report Found Item");
        JButton myLostReportsButton = createNavigationButton("My Lost Reports");

        // Add buttons with vertical spacing
        navBox.add(reportLostButton);
        navBox.add(Box.createRigidArea(new Dimension(0, 20))); // Vertical space
        navBox.add(searchFoundButton);
        navBox.add(Box.createRigidArea(new Dimension(0, 20)));
        navBox.add(reportFoundButton);
        navBox.add(Box.createRigidArea(new Dimension(0, 20)));
        navBox.add(myLostReportsButton);
        
        // --- Add Action Listeners for Navigation ---
        reportLostButton.addActionListener(e -> {frame.dispose(); new ReportLostItem();});
        searchFoundButton.addActionListener(e -> {frame.dispose(); new SearchFoundItems();});
        reportFoundButton.addActionListener(e -> {frame.dispose(); new ReportFoundItem();});
        myLostReportsButton.addActionListener(e -> {frame.dispose(); new MyLostReports();});

        return navBox;
    }
    
    /**
     * Creates high-contrast, full-width navigation links.
     */
    private JButton createNavigationButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18)); 
        
        // REQUESTED STYLING: White Background / Navy Text
        button.setBackground(Theme.TEXT_LIGHT); 
        button.setForeground(Theme.BACKGROUND_DARK); 
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Set maximum size to take FULL width of the parent BoxLayout container
        Dimension size = new Dimension(Integer.MAX_VALUE, BUTTON_HEIGHT);
        button.setPreferredSize(size);
        button.setMaximumSize(size);
        button.setAlignmentX(Component.CENTER_ALIGNMENT); // Important for BoxLayout

        // Clean, minimal padding border and ensures uniform height
        button.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.BACKGROUND_DARK, 1, true), // Thin border using navy color
            BorderFactory.createEmptyBorder(10, 20, 10, 20) 
        ));

        // --- Hover Effects ---
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                // Highlight with primary accent color on hover
                button.setBackground(Theme.ACCENT_PRIMARY); // Light Cyan/White on hover
                button.setForeground(Theme.BACKGROUND_DARK); 
            }
            @Override
            public void mouseExited(MouseEvent e) {
                // Restore default style
                button.setBackground(Theme.TEXT_LIGHT); // Back to White
                button.setForeground(Theme.BACKGROUND_DARK);
            }
        });

        return button;
    }
    
    /**
     * Creates a discrete text-based logout link.
     */
    private JLabel createLogoutLink() {
        JLabel link = new JLabel("Logout");
        link.setFont(new Font("Arial", Font.BOLD, 16));
        link.setForeground(Theme.TEXT_LIGHT);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        link.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                link.setForeground(Theme.ACCENT_PRIMARY);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                link.setForeground(Theme.TEXT_LIGHT);
            }
        });
        return link;
    }
    
    // Unused but required helper method placeholder
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setBackground(Theme.ACCENT_PRIMARY); 
        button.setForeground(Theme.BACKGROUND_DARK); 
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        return button;
    }
}
