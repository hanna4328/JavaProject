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

        // GBC for placing panels inside the mainContainer
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0); // Ensures seamless split between panels

        // -----------------------------------------------------
        // 1. LEFT PANEL: Introduction and Context
        // -----------------------------------------------------
        JPanel introPanel = createIntroPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        mainContainer.add(introPanel, gbc);

        // -----------------------------------------------------
        // 2. RIGHT PANEL: Login Form
        // -----------------------------------------------------
        JPanel loginBox = createLoginBox();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.5;
        mainContainer.add(loginBox, gbc);

        frame.add(backgroundPanel);
        frame.setVisible(true);
    }
    
    /**
     * Creates the Introduction Panel (Left Side) - FIXES OVERLAP AND EXTRA INPUT BOX
     */
    private JPanel createIntroPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 20)); // Use BorderLayout for better top-level control
        
        // Glassmorphism background for context panel (slightly transparent)
        panel.setBackground(new Color(Theme.BACKGROUND_LIGHT.getRed(), Theme.BACKGROUND_LIGHT.getGreen(), Theme.BACKGROUND_LIGHT.getBlue(), 120));
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.ACCENT_PRIMARY, 1, true),
            new EmptyBorder(60, 60, 60, 60)
        ));

        // Container for Title and the Extra Input Box (Removed the extra box)
        JPanel titleContainer = new JPanel();
        titleContainer.setLayout(new BoxLayout(titleContainer, BoxLayout.Y_AXIS));
        titleContainer.setOpaque(false); // Transparent background

        JLabel title = new JLabel("SECURE PORTAL ACCESS");
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(Theme.ACCENT_PRIMARY);
        title.setAlignmentX(Component.LEFT_ALIGNMENT); // Left align in the container

        // --- FIX: Removed the additional unnecessary input box here ---
        
        titleContainer.add(title);
        titleContainer.add(Box.createVerticalStrut(10));
        
        // JTextArea for the main description
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
        introText.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        introText.setWrapStyleWord(true);
        introText.setLineWrap(true);
        introText.setEditable(false);
        
        // Add components using BorderLayout to fill the space correctly
        panel.add(titleContainer, BorderLayout.NORTH);
        panel.add(introText, BorderLayout.CENTER);
        
        return panel;
    }

    /**
     * Creates the standardized Login Box (Right Side) - Corrected Layout
     */
    private JPanel createLoginBox() {
        JPanel loginBox = new JPanel(new GridBagLayout());
        loginBox.setBackground(Theme.BACKGROUND_LIGHT); 
        // Border is now only on the right edge to complete the split look
        loginBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.ACCENT_PRIMARY, 1), 
            new EmptyBorder(60, 70, 60, 70) 
        ));
        loginBox.setPreferredSize(new Dimension(500, 550)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // --- Form Title ---
        JLabel titleLabel = new JLabel("LOG IN");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT_PRIMARY); 
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 40, 0);
        loginBox.add(titleLabel, gbc);
        
        // Reset GBC
        gbc.gridwidth = 1;
        
        // --- Username ---
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(Theme.FONT_LABEL);
        userLabel.setForeground(Theme.TEXT_LIGHT);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 10); // Reduced vertical padding
        gbc.anchor = GridBagConstraints.WEST; 
        loginBox.add(userLabel, gbc);

        JTextField userText = createStyledTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0); // Reduced padding, keeps fields snug
        gbc.anchor = GridBagConstraints.EAST; 
        loginBox.add(userText, gbc);

        // --- Password ---
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(Theme.FONT_LABEL);
        passLabel.setForeground(Theme.TEXT_LIGHT);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 5, 10); // Reduced vertical padding
        gbc.anchor = GridBagConstraints.WEST; 
        loginBox.add(passLabel, gbc);

        JPasswordField passText = createStyledPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets = new Insets(10, 0, 5, 0); // Reduced padding
        gbc.anchor = GridBagConstraints.EAST; 
        loginBox.add(passText, gbc);

        // --- LOG IN Button ---
        JButton loginButton = createStyledWebButton("LOG IN");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(40, 0, 10, 0); 
        gbc.fill = GridBagConstraints.NONE; // FIX: Prevents button from stretching across both columns
        gbc.anchor = GridBagConstraints.CENTER; 
        loginBox.add(loginButton, gbc);
        
        // --- Sign Up Link ---
        JLabel signupLink = createSignUpLink();
        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        loginBox.add(signupLink, gbc);


        // --- Action Listeners (Database Logic) ---
        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseConnector.validateUser(username, password)) {
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
            @Override public void mouseEntered(MouseEvent e) { signupLink.setForeground(Theme.ACCENT_SECONDARY); }
            @Override public void mouseExited(MouseEvent e) { signupLink.setForeground(Theme.TEXT_LIGHT); }
        });

        return loginBox;
    }
    
    // --- Utility Methods (createIntroPanel, openSignUpDialog, createStyledWebButton, 
    // createSignUpLink, createStyledTextField, createStyledPasswordField, etc. must be included) ---

    // The rest of the helper methods (createStyledWebButton, createSignUpLink, 
    // createStyledTextField, createStyledPasswordField, openSignUpDialog, and main) 
    // must be included from the previous correct version of the code.

    /**
     * Creates the separate JDialog for Sign Up to keep the Login page clean.
     */
    /**
     * Creates the separate JDialog for Sign Up to keep the Login page clean.
     */
    private void openSignUpDialog(JFrame parentFrame) {
        JDialog signupDialog = new JDialog(parentFrame, "Sign Up", true);
        signupDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        signupDialog.setLayout(new GridBagLayout());
        signupDialog.getContentPane().setBackground(Theme.BACKGROUND_DARK);
        signupDialog.getRootPane().setBorder(new LineBorder(Theme.ACCENT_PRIMARY, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 20, 10, 20);

        // Title
        JLabel title = new JLabel("Create New Account");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        // --- FIX 1: Change color to White (TEXT_LIGHT) ---
        title.setForeground(Theme.TEXT_LIGHT); 
        // --- FIX 2: Center align the title ---
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2; 
        signupDialog.add(title, gbc);

        // Form fields
        JTextField newUsername = createStyledTextField();
        JPasswordField newPassword = createStyledPasswordField();
        
        // Username Label/Field
        gbc.gridy = 1; gbc.gridwidth = 1;
        JLabel userLabel = new JLabel("Username:"); userLabel.setForeground(Theme.TEXT_LIGHT);
        gbc.gridx = 0; signupDialog.add(userLabel, gbc);
        gbc.gridx = 1; signupDialog.add(newUsername, gbc);
        
        // Password Label/Field
        gbc.gridy = 2;
        JLabel passLabel = new JLabel("Password:"); passLabel.setForeground(Theme.TEXT_LIGHT);
        gbc.gridx = 0; signupDialog.add(passLabel, gbc);
        gbc.gridx = 1; signupDialog.add(newPassword, gbc);

        // Buttons
        JButton registerButton = createStyledWebButton("REGISTER");
        registerButton.setBackground(Theme.ACCENT_SECONDARY); 
        registerButton.setForeground(Theme.BACKGROUND_DARK);

        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2; gbc.insets = new Insets(30, 20, 10, 20);
        signupDialog.add(registerButton, gbc);

        registerButton.addActionListener(e -> {
            String username = newUsername.getText();
            String password = new String(newPassword.getPassword());

            if (DatabaseConnector.registerUser(username, password)) {
                JOptionPane.showMessageDialog(signupDialog, "Sign Up Successful! You can now log in.", "Success", JOptionPane.INFORMATION_MESSAGE);
                signupDialog.dispose();
                parentFrame.dispose();
                new Loginpg();
            } else {
                JOptionPane.showMessageDialog(signupDialog, "User already exists or an error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        signupDialog.pack();
        signupDialog.setLocationRelativeTo(parentFrame);
        signupDialog.setVisible(true);
    }
    private JButton createStyledWebButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.ACCENT_PRIMARY); 
        button.setForeground(Theme.BACKGROUND_DARK); 
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Theme.ACCENT_SECONDARY);
                button.setForeground(Theme.BACKGROUND_DARK); 
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.ACCENT_PRIMARY);
                button.setForeground(Theme.BACKGROUND_DARK);
            }
        });

        return button;
    }

    private JLabel createSignUpLink() {
        JLabel link = new JLabel("Don't have an account? Sign up here");
        link.setForeground(Theme.TEXT_LIGHT);
        link.setFont(Theme.FONT_LABEL);
        link.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        return link;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setBackground(Theme.BACKGROUND_DARK); 
        textField.setForeground(Theme.TEXT_LIGHT);
        textField.setCaretColor(Theme.ACCENT_SECONDARY); 
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8) 
        ));
        
        textField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_SECONDARY, 2),
                    BorderFactory.createEmptyBorder(7, 7, 7, 7)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                 textField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }
        });
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(Theme.BACKGROUND_DARK);
        passwordField.setForeground(Theme.TEXT_LIGHT);
        passwordField.setCaretColor(Theme.ACCENT_SECONDARY);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8) 
        ));

        passwordField.addFocusListener(new FocusAdapter() {
            @Override public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_SECONDARY, 2),
                    BorderFactory.createEmptyBorder(7, 7, 7, 7)
                ));
            }
            @Override public void focusLost(FocusEvent e) {
                 passwordField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
                    BorderFactory.createEmptyBorder(8, 8, 8, 8)
                ));
            }
        });
        return passwordField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Loginpg());
    }
}
