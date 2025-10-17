package lostfound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class ReportLostItem {
    
    private GraphicsDevice graphicsDevice;
    private JFrame frame;

    public ReportLostItem() {
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        package lostfound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ReportLostItem {
    
    private JFrame frame;

    public ReportLostItem() {
        frame = new JFrame("Report Lost Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        JPanel mainPanel = createStyledPanel(new GridBagLayout(), Theme.BACKGROUND_DARK);
        frame.add(mainPanel);

        GridBagConstraints gbc = createGBC();
        JLabel titleLabel = createStyledLabel("Report a Lost Item");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.ACCENT_PRIMARY);
        
        // --- FIX: Center the text within the JLabel ---
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); 
        
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2; // Span across both columns
        gbc.insets = new Insets(0, 0, 30, 0); // Add spacing below title
        
        // FIX: Ensure the component itself is centered within its two-column span
        gbc.anchor = GridBagConstraints.CENTER; 
        
        mainPanel.add(titleLabel, gbc);

        // Reset gridwidth and insets for subsequent form fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        // Reset anchor to WEST for left-aligned labels
        gbc.anchor = GridBagConstraints.WEST;
        // Form Fields
        JTextField itemNameField = createStyledTextField();
        JTextField lostLocationField = createStyledTextField();
        JTextField lostDateField = createStyledTextField();
        JTextField contactEmailField = createStyledTextField();
        JTextArea descriptionArea = createStyledTextArea();
        String[] categories = {"Electronics", "Clothing", "Keys", "Wallet", "Documents", "Other"};
        JComboBox<String> categoryComboBox = createStyledComboBox(categories);
        
        // --- Layout ---
        addRow(mainPanel, gbc, 1, "Item Name:", itemNameField);
        addRow(mainPanel, gbc, 2, "Category:", categoryComboBox);
        addRow(mainPanel, gbc, 3, "Last Seen Location:", lostLocationField);
        addRow(mainPanel, gbc, 4, "Date Lost (YYYY-MM-DD):", lostDateField);
        addRow(mainPanel, gbc, 5, "Contact Email:", contactEmailField);
        
        // Description Area
        JLabel descLabel = createStyledLabel("Description:");
        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.WEST; mainPanel.add(descLabel, gbc);
        gbc.gridx = 1; 
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(250, 80));
        mainPanel.add(scrollPane, gbc);

        // Buttons
        JButton submitButton = createStyledButton("Submit Report");
        JButton backButton = createStyledButton("Back to Dashboard");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.insets.top = 30;
        mainPanel.add(buttonPanel, gbc);
        
        // --- Action Listener ---
        submitButton.addActionListener(e -> handleSubmit(
            itemNameField.getText(), 
            (String) categoryComboBox.getSelectedItem(), 
            lostLocationField.getText(), 
            lostDateField.getText(), 
            descriptionArea.getText(), 
            contactEmailField.getText())
        );

        backButton.addActionListener(e -> {
            frame.dispose();
            new UserDashboard();
        });

        frame.setVisible(true);
    }
    
    // --- Database Submission Logic ---
    private void handleSubmit(String name, String category, String location, String date, String desc, String email) {
        if (name.isEmpty() || location.isEmpty() || date.isEmpty() || email.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // NOTE: user_id is hardcoded to 1 for simplicity here. You should pass the actual logged-in user's ID.
        String sql = "INSERT INTO Lost_Items (user_id, item_name, category, lost_location, lost_date, description, contact_email) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, 1); // Assuming user ID is 1 for now
            stmt.setString(2, name);
            stmt.setString(3, category);
            stmt.setString(4, location);
            stmt.setString(5, date);
            stmt.setString(6, desc);
            stmt.setString(7, email);
            
            stmt.executeUpdate();
            
            JOptionPane.showMessageDialog(frame, "Lost item reported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            // After success, this is where the AUTO-MATCH logic would be triggered.
            frame.dispose();
            new UserDashboard(); 

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // --- Helper Methods (must be copied to ReportLostItem.java) ---
    // (Note: These methods use the Theme and styling rules set in previous steps)
    private JPanel createStyledPanel(LayoutManager layout, Color background) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(background);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        return panel;
    }
    
    private GridBagConstraints createGBC() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        return gbc;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Theme.TEXT_LIGHT);
        label.setFont(Theme.FONT_LABEL);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField(20);
        field.setBackground(Theme.BACKGROUND_DARK); 
        field.setForeground(Theme.TEXT_LIGHT);
        field.setCaretColor(Theme.ACCENT_SECONDARY); 
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8) 
        ));
        return field;
    }
    
    private JTextArea createStyledTextArea() {
        JTextArea area = new JTextArea(5, 20);
        area.setBackground(Theme.BACKGROUND_DARK);
        area.setForeground(Theme.TEXT_LIGHT);
        area.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8) 
        ));
        return area;
    }

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(Theme.BACKGROUND_DARK);
        comboBox.setForeground(Theme.TEXT_LIGHT);
        return comboBox;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(Theme.FONT_BUTTON);
        button.setBackground(Theme.ACCENT_PRIMARY); 
        button.setForeground(Theme.BACKGROUND_DARK); 
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Button padding
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(Theme.ACCENT_SECONDARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.ACCENT_PRIMARY);
            }
        });
        return button;
    }
    
    private void addRow(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        JLabel label = createStyledLabel(labelText);
        
        gbc.gridx = 0; gbc.gridy = row; gbc.anchor = GridBagConstraints.WEST; gbc.fill = GridBagConstraints.NONE;
        panel.add(label, gbc);
        
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.EAST; gbc.fill = GridBagConstraints.HORIZONTAL;
        panel.add(field, gbc);
    }
}
        frame = new JFrame("Report Lost Item");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(frame);
        } else {
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BACKGROUND_DARK);
        
        JLabel messageLabel = new JLabel("Report Lost Item UI Coming Soon!");
        messageLabel.setFont(Theme.FONT_TITLE);
        messageLabel.setForeground(Theme.TEXT_LIGHT);
        panel.add(messageLabel);
        
        frame.add(panel);
        frame.setVisible(true);

        // Add a listener to handle the full-screen exit when the window closes
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (graphicsDevice.getFullScreenWindow() != null) {
                    graphicsDevice.setFullScreenWindow(null);
                }
                frame.dispose();
            }
        });
    }

}
