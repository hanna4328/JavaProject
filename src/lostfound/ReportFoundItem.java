package lostfound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class ReportFoundItem {
    
    private JFrame frame;

    public ReportFoundItem() {
        frame = new JFrame("Report Found Item");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        // Use the standardized panel creation and GridBagLayout
        JPanel mainPanel = createStyledPanel(new GridBagLayout(), Theme.BACKGROUND_DARK);
        frame.add(mainPanel);

        GridBagConstraints gbc = createGBC();
        
        // --- Title: Centered and Styled ---
        JLabel titleLabel = createStyledLabel("Report a Found Item");
        titleLabel.setFont(Theme.FONT_TITLE);
        titleLabel.setForeground(Theme.ACCENT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; 
        gbc.gridy = 0; 
        gbc.gridwidth = 2; // Span across both columns
        gbc.insets = new Insets(0, 0, 30, 0); // Add spacing below title
        gbc.anchor = GridBagConstraints.CENTER; 
        
        mainPanel.add(titleLabel, gbc);

        // Reset GBC for form fields
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;

        // Form Fields (Using the standardized Navy Galaxy styled components)
        JTextField itemNameField = createStyledTextField();
        JTextField foundAtField = createStyledTextField();
        JTextField dateFoundField = createStyledTextField();
        JTextField contactField = createStyledTextField();
        JTextArea descriptionArea = createStyledTextArea();
        
        String[] categories = {"Electronics", "Clothing", "Keys", "Wallet", "Documents", "Other"};
        JComboBox<String> categoryComboBox = createStyledComboBox(categories);
        
        // --- Layout Rows ---
        // Item Name
        addRow(mainPanel, gbc, 1, "Item Name:", itemNameField);
        
        // Category 
        addRow(mainPanel, gbc, 2, "Category:", categoryComboBox);

        // Found At
        addRow(mainPanel, gbc, 3, "Found At Location:", foundAtField);

        // Date Found
        addRow(mainPanel, gbc, 4, "Date Found (YYYY-MM-DD):", dateFoundField);

        // Contact Info
        addRow(mainPanel, gbc, 5, "Your Contact Info:", contactField);

        // Description Area
        JLabel descLabel = createStyledLabel("Description:");
        gbc.gridx = 0; gbc.gridy = 6; gbc.anchor = GridBagConstraints.WEST; mainPanel.add(descLabel, gbc);
        gbc.gridx = 1; 
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(250, 80)); // Use 80 height for consistency
        mainPanel.add(scrollPane, gbc);

        // Buttons
        JButton submitButton = createStyledButton("Submit Report");
        JButton backButton = createStyledButton("Back to Dashboard");

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 0)); // 30 gap for consistency
        buttonPanel.setOpaque(false);
        buttonPanel.add(submitButton);
        buttonPanel.add(backButton);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.insets.top = 30; // Top margin for button panel
        mainPanel.add(buttonPanel, gbc);
        
        // --- Action Listeners ---
        submitButton.addActionListener(e -> handleSubmit(
                itemNameField.getText(),
                (String) categoryComboBox.getSelectedItem(),
                foundAtField.getText(),
                dateFoundField.getText(),
                descriptionArea.getText(),
                contactField.getText()
        ));

        backButton.addActionListener(e -> {
            frame.dispose();
            new UserDashboard();
        });
        
        frame.setVisible(true);
    }
    
    // --- Database Submission Logic ---
    private void handleSubmit(String name, String category, String foundAt, String dateFound, String description, String contact) {
        if (name.isEmpty() || foundAt.isEmpty() || dateFound.isEmpty() || contact.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please fill in all required fields.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO founditem (name, category, foundat, datefound, description, contactinfo) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, name);
            stmt.setString(2, category);
            stmt.setString(3, foundAt);
            stmt.setString(4, dateFound);
            stmt.setString(5, description);
            stmt.setString(6, contact);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(frame, "Found item reported successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                frame.dispose();
                new UserDashboard(); 
            } else {
                 JOptionPane.showMessageDialog(frame, "Failed to report item.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // -------------------------------------------------------------------------
    // --- STANDARDIZED HELPER METHODS (Navy Galaxy Theme) ---
    // -------------------------------------------------------------------------
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
        area.setCaretColor(Theme.ACCENT_SECONDARY);
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
