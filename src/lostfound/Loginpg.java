package lostfound;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Loginpg {

    private GraphicsDevice graphicsDevice;
    private JFrame frame;

    public Loginpg() {
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

        frame = new JFrame("Login Page");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(frame);
        } else {
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
        }

        JPanel mainPanel = new ImagePanel("login_background.jpg");
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 20, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Lost & Found Portal");

        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Theme.NEON_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 80, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userLabel.setForeground(Theme.NEON_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(userLabel, gbc);

        JTextField userText = createStyledTextField();
        gbc.gridx = 1;
        gbc.gridy = 1;
        mainPanel.add(userText, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.BOLD, 16));
        passLabel.setForeground(Theme.NEON_BLUE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(passLabel, gbc);

        JPasswordField passText = createStyledPasswordField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.insets.top = 20;
        mainPanel.add(passText, gbc);

        gbc.insets.top = 10;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        JButton loginButton = createStyledButton("Login");
        JButton signupButton = createStyledButton("Sign Up");
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                frame.setVisible(false);
                JOptionPane.showMessageDialog(null, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                frame.setVisible(true);
                return;
            }

            if (DatabaseConnector.validateUser(username, password)) {
                frame.setVisible(false);
                JOptionPane.showMessageDialog(null, "Login Successful!");

                if (graphicsDevice.getFullScreenWindow() != null) {
                    graphicsDevice.setFullScreenWindow(null);
                }
                frame.dispose();

                new UserDashboard();
            } else {
                frame.setVisible(false);
                JOptionPane.showMessageDialog(null, "Invalid Credentials");
                frame.setVisible(true);
            }
        });

        signupButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passText.getPassword());

            if (username.isEmpty() || password.isEmpty()) {
                frame.setVisible(false);
                JOptionPane.showMessageDialog(null, "Username and password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                frame.setVisible(true);
                return;
            }

            if (DatabaseConnector.registerUser(username, password)) {
                frame.setVisible(false);
                JOptionPane.showMessageDialog(null, "Sign Up Successful! Please log in.");
                frame.setVisible(true);
            } else {
                frame.setVisible(false);
                JOptionPane.showMessageDialog(null, "User already exists or an error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
                frame.setVisible(true);
            }
        });

        frame.add(mainPanel);
        frame.setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.BACKGROUND_LIGHT);
        button.setForeground(Theme.NEON_BLUE);
        button.setBorder(BorderFactory.createEmptyBorder(12, 21, 12, 21));
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

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setBackground(Theme.BACKGROUND_LIGHT);
        textField.setForeground(Theme.TEXT_LIGHT);
        textField.setCaretColor(Theme.NEON_BLUE);
        textField.setBorder(BorderFactory.createLineBorder(Theme.TEXT_LIGHT, 1));

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                textField.setBorder(BorderFactory.createLineBorder(Theme.NEON_BLUE, 2));
            }

            @Override
            public void focusLost(FocusEvent e) {
                textField.setBorder(BorderFactory.createLineBorder(Theme.TEXT_LIGHT, 1));
            }
        });
        return textField;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setBackground(Theme.BACKGROUND_LIGHT);
        passwordField.setForeground(Theme.TEXT_LIGHT);
        passwordField.setCaretColor(Theme.NEON_BLUE);
        passwordField.setBorder(BorderFactory.createLineBorder(Theme.TEXT_LIGHT, 1));

        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createLineBorder(Theme.NEON_BLUE, 2));
            }

            @Override
            public void focusLost(FocusEvent e) {
                passwordField.setBorder(BorderFactory.createLineBorder(Theme.TEXT_LIGHT, 1));
            }
        });
        return passwordField;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Loginpg());
    }
}

