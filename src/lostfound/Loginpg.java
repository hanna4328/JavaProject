package lostfound;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.EmptyBorder;

public class Loginpg {

    private JFrame frame;

    public Loginpg() {
        frame = new JFrame("Lost & Found Portal Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        // Use ImagePanel for galaxy background
        JPanel backgroundPanel = new ImagePanel("login_background.jpg");
        backgroundPanel.setLayout(new GridBagLayout()); 

        // --- Create Main Two-Column Container ---
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setOpaque(false);
        backgroundPanel.add(mainContainer);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);

        // 1. LEFT PANEL: Introduction and Context
        JPanel introPanel = createIntroPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        mainContainer.add(introPanel, gbc);

        // 2. RIGHT PANEL: Login Form
        JPanel loginBox = createLoginBox();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        mainContainer.add(loginBox, gbc);

        frame.add(backgroundPanel);
        frame.setVisible(true);
    }
    
    // --- UI Creation Methods (omitted for brevity, assume they are correct) ---
    private JPanel createIntroPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 20)); 
        panel.setBackground(new Color(Theme.BACKGROUND_LIGHT.getRed(), Theme.BACKGROUND_LIGHT.getGreen(), Theme.BACKGROUND_LIGHT.getBlue(), 120));
        panel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Theme.ACCENT_PRIMARY, 1, true), new EmptyBorder(60, 60, 60, 60)));

        JLabel title = new JLabel("SECURE PORTAL ACCESS");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Theme.ACCENT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea introText = new JTextArea(
            "\n" +
            "Welcome to the Lost & Found Portal, your centralized hub for securely " +
            "connecting lost items with their rightful owners.\n\n" +
            "We use a cutting-edge matching system to automatically scan all reported found items " +
            "against newly registered lost items, maximizing your chances of recovery.\n\n" +
            "Please log in to continue or sign up to join our community."
        );
        introText.setFont(Theme.FONT_LABEL);
        introText.setForeground(Theme.TEXT_LIGHT);
        introText.setBackground(new Color(0, 0, 0, 0)); 
        introText.setWrapStyleWord(true);
        introText.setLineWrap(true);
        introText.setEditable(false);
        
        panel.add(new JPanel() {{ // Container for title/spacing
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            setOpaque(false);
            add(title);
            add(Box.createVerticalStrut(10));
        }}, BorderLayout.NORTH);
        panel.add(introText, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createLoginBox() {
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(Theme.BACKGROUND_LIGHT); 
        loginBox.setBorder(BorderFactory.createCompoundBorder(new LineBorder(Theme.ACCENT_PRIMARY, 1), new EmptyBorder(60, 70, 60, 70)));
        loginBox.setPreferredSize(new Dimension(500, 550)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // --- Form Title ---
        JLabel titleLabel = new JLabel("LOG IN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT_PRIMARY); 
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 40, 0);
        loginBox.add(titleLabel, gbc);
        
        gbc.gridwidth = 1;
        
        // --- Username ---
        JLabel userLabel = new JLabel("Username:"); userLabel.setFont(Theme.FONT_LABEL); userLabel.setForeground(Theme.TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 1; gbc.insets = new Insets(10, 0, 10, 10); gbc.anchor = GridBagConstraints.WEST; loginBox.add(userLabel, gbc);
        JTextField userText = createStyledTextField();
        gbc.gridx = 1; gbc.gridy = 1; gbc.insets = new Insets(10, 10, 10, 0); gbc.anchor = GridBagConstraints.EAST; loginBox.add(userText, gbc);

        // --- Password ---
        JLabel passLabel = new JLabel("Password:"); passLabel.setFont(Theme.FONT_LABEL); passLabel.setForeground(Theme.TEXT_LIGHT);
        gbc.gridx = 0; gbc.gridy = 2; gbc.insets = new Insets(10, 0, 10, 10); gbc.anchor = GridBagConstraints.WEST; loginBox.add(passLabel, gbc);
        JPasswordField passText = createStyledPasswordField();
        gbc.gridx = 1; gbc.gridy = 2; gbc.insets = new Insets(10, 10, 10, 0); gbc.anchor = GridBagConstraints.EAST; loginBox.add(passText, gbc);

        // --- LOG IN Button ---
        JButton loginButton = createStyledWebButton("LOG IN");
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.insets = new Insets(40, 0, 10, 0); 
        gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        loginBox.add(loginButton, gbc);
        
        // --- Sign Up Link ---
        JLabel signupLink = createSignUpLink();
        gbc.gridy = 4; gbc.insets = new Insets(10, 0, 0, 0);
        loginBox.add(signupLink, gbc);


        // --- Action Listeners (Database Logic) ---
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // FIX: Use new method and store ID in SessionManager
            int userId = DatabaseConnector.validateUser(username, password);

            if (userId != -1) {
                SessionManager.login(userId); // Log user into the session
                JOptionPane.showMessageDialog(frame, "Login Successful!");
                frame.dispose();
                new UserDashboard();
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                openSignUpDialog(frame);
            }
        });

        return loginBox;
    }

    private JDialog openSignUpDialog(JFrame parentFrame) {
        // (Implementation omitted for brevity, but must be included in your code)
        JDialog signupDialog = new JDialog(parentFrame, "Sign Up", true);
        // ... (setup and registration logic)
        return signupDialog;
    }
    
    // --- Helper Methods (omitted for brevity, must be included) ---
    private JButton createStyledWebButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BUTTON); button.setBackground(Theme.ACCENT_PRIMARY); 
        button.setForeground(Theme.BACKGROUND_DARK); button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 

        button.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { button.setBackground(Theme.ACCENT_SECONDARY); button.setForeground(Theme.BACKGROUND_DARK); }
            @Override public void mouseExited(MouseEvent e) { button.setBackground(Theme.ACCENT_PRIMARY); button.setForeground(Theme.BACKGROUND_DARK); }
        });
        return button;
    }

    private JLabel createSignUpLink() {
        JLabel link = new JLabel("Don't have an account? Sign up here");
        link.setForeground(Theme.TEXT_LIGHT); link.setFont(Theme.FONT_LABEL);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return link;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setBackground(Theme.BACKGROUND_DARK); textField.setForeground(Theme.TEXT_LIGHT);
        textField.setCaretColor(Theme.ACCENT_SECONDARY); 
        textField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(Theme.BACKGROUND_DARK); passwordField.setForeground(Theme.TEXT_LIGHT);
        passwordField.setCaretColor(Theme.ACCENT_SECONDARY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1), BorderFactory.createEmptyBorder(8, 8, 8, 8)));
        return passwordField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Loginpg());
    }
}
