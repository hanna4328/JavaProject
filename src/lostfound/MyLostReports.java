package lostfound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class MyLostReports {
    
    private JFrame frame;
    private JTextArea reportDisplay;
    private JTextField deleteIdField;
    private JTextField editIdField;

    private static class LostItem {
        int id;
        String name;
        String category;
        String date;
        String location;
        String description;
        String contactEmail;
    }

    public MyLostReports() {
        frame = new JFrame("My Lost Reports & Matches");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        JPanel mainPanel = createStyledPanel(new BorderLayout(), Theme.BACKGROUND_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        frame.add(mainPanel);

        // --- TOP FIXED CONTAINER (Header + Action Panel) ---
        JPanel topFixedContainer = new JPanel();
        topFixedContainer.setOpaque(false);
        topFixedContainer.setLayout(new BoxLayout(topFixedContainer, BoxLayout.Y_AXIS));

        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        JLabel titleLabel = createStyledLabel("My Lost Reports & Possible Matches");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to Dashboard");
        headerPanel.add(backButton, BorderLayout.EAST);
        
        topFixedContainer.add(headerPanel);
        
        // --- 2. Delete/Action Panel (Below Header) ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        actionPanel.setOpaque(false);
        
        JLabel deleteLabel = createStyledLabel("Delete Report (Enter ID):");
        deleteIdField = createStyledTextField();
        deleteIdField.setColumns(5);
        JButton deleteButton = createStyledButton("Delete");
        
        JLabel editLabel = createStyledLabel("Edit Report (Enter ID):");
        editIdField = createStyledTextField();
        editIdField.setColumns(5);
        JButton editButton = createStyledButton("Load for Edit");
        
        actionPanel.add(deleteLabel);
        actionPanel.add(deleteIdField);
        actionPanel.add(deleteButton);
        actionPanel.add(Box.createHorizontalStrut(30)); // Separator
        actionPanel.add(editLabel);
        actionPanel.add(editIdField);
        actionPanel.add(editButton);
        
        topFixedContainer.add(actionPanel);
        
        mainPanel.add(topFixedContainer, BorderLayout.NORTH);


        // --- Scrollable Content Panel (CENTER) ---
        reportDisplay = new JTextArea();
        reportDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14)); 
        reportDisplay.setForeground(Theme.TEXT_LIGHT);
        reportDisplay.setBackground(Theme.BACKGROUND_DARK);
        reportDisplay.setEditable(false);

        TitledBorder resultsTitleBorder = BorderFactory.createTitledBorder(
            new LineBorder(Theme.ACCENT_PRIMARY, 1), 
            "Report Status and System Matches (NOTE: Use ID to Edit/Delete)", TitledBorder.LEFT, TitledBorder.TOP, 
            new Font(Font.SANS_SERIF, Font.BOLD, 14), Theme.ACCENT_PRIMARY
        );
        reportDisplay.setBorder(BorderFactory.createCompoundBorder(resultsTitleBorder, new EmptyBorder(10, 10, 10, 10)));
        
        JScrollPane scrollPane = new JScrollPane(reportDisplay);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_DARK); 
        
        mainPanel.add(scrollPane, BorderLayout.CENTER); 


        // --- Load Reports and Matches on Startup ---
        loadReportsAndMatches(reportDisplay);

        // --- Action Listeners ---
        backButton.addActionListener(e -> {
            frame.dispose();
            new UserDashboard();
        });
        
        deleteButton.addActionListener(e -> handleDeleteAction());
        editButton.addActionListener(e -> handleEditAction()); 

        frame.setVisible(true);
    }
    
    // -------------------------------------------------------------------------
    // --- DELETE ACTION LOGIC ---
    // -------------------------------------------------------------------------
    private void handleDeleteAction() {
        int currentUserId = SessionManager.getCurrentUserId();
        if (currentUserId < 0) {
            JOptionPane.showMessageDialog(frame, "Error: Must be logged in to delete reports.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int itemId = Integer.parseInt(deleteIdField.getText().trim());
            
            int confirm = JOptionPane.showConfirmDialog(frame, 
                "Are you sure you want to permanently delete Report ID: " + itemId + "?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (DatabaseConnector.deleteLostItem(itemId, currentUserId)) {
                    JOptionPane.showMessageDialog(frame, "Report ID " + itemId + " deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    deleteIdField.setText("");
                    loadReportsAndMatches(reportDisplay); // Refresh the list
                } else {
                    JOptionPane.showMessageDialog(frame, "Could not find Report ID " + itemId + " under your account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid Report ID (number).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error during deletion.", "SQL Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Deletion SQL Error: " + ex.getMessage());
        }
    }


    // -------------------------------------------------------------------------
    // --- UPDATE ACTION LOGIC (Handler) ---
    // -------------------------------------------------------------------------
    private void handleEditAction() {
        int currentUserId = SessionManager.getCurrentUserId();
        if (currentUserId < 0) {
            JOptionPane.showMessageDialog(frame, "Error: Must be logged in to edit reports.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int itemId = Integer.parseInt(editIdField.getText().trim());
            
            Connection conn = null;
            try {
                conn = DatabaseConnector.connect();
                ResultSet rs = DatabaseConnector.getLostItemById(itemId);
                
                if (rs.next()) {
                    // 1. Map data to the LostItem object
                    LostItem item = new LostItem();
                    item.id = itemId;
                    item.name = rs.getString("item_name");
                    item.category = rs.getString("category");
                    item.location = rs.getString("lost_location");
                    item.date = rs.getString("lost_date");
                    item.description = rs.getString("description");
                    item.contactEmail = rs.getString("contact_email");
                    
                    // 2. Launch the integrated edit dialog
                    showEditDialog(item); 
                } else {
                    JOptionPane.showMessageDialog(frame, "Could not find Report ID " + itemId + " under your account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } finally {
                if (conn != null) conn.close();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid Report ID (number).", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database Error: Could not load report for editing.", "SQL Error", JOptionPane.ERROR_MESSAGE);
            System.err.println("Load Report SQL Error: " + ex.getMessage());
        }
    }
    
    /**
     * Creates and displays the modal dialog for editing the item (INTEGRATED).
     */
    private void showEditDialog(LostItem item) {
        // --- Setup Fields for the Dialog ---
        JTextField nameField = createStyledTextField();
        nameField.setText(item.name);
        
        JComboBox<String> categoryBox = createStyledComboBox(new String[]{"Electronics", "Clothing", "Keys", "Wallet", "Documents", "Other"});
        categoryBox.setSelectedItem(item.category);
        
        JTextField locationField = createStyledTextField();
        locationField.setText(item.location);
        
        JTextField dateField = createStyledTextField();
        dateField.setText(item.date);
        
        JTextArea descriptionArea = createStyledTextArea();
        descriptionArea.setText(item.description);
        
        JTextField emailField = createStyledTextField();
        emailField.setText(item.contactEmail);

        // --- Build the Dialog Panel ---
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BACKGROUND_DARK);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Title Label
        JLabel title = createStyledLabel("EDITING REPORT ID: " + item.id);
        title.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        title.setForeground(Theme.ACCENT_SECONDARY);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; panel.add(title, gbc);
        gbc.gridwidth = 1; // Reset width
        
        // Form Layout
        int row = 1;
        addRow(panel, gbc, row++, "Item Name:", nameField);
        addRow(panel, gbc, row++, "Category:", categoryBox);
        addRow(panel, gbc, row++, "Location:", locationField);
        addRow(panel, gbc, row++, "Date Lost:", dateField);
        addRow(panel, gbc, row++, "Email:", emailField);
        
        // Description
        JLabel descLabel = createStyledLabel("Description:");
        gbc.gridx = 0; gbc.gridy = row; panel.add(descLabel, gbc);
        gbc.gridx = 1; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(new JScrollPane(descriptionArea), gbc);
        
        // --- Show Dialog and Handle Submission ---
        int result = JOptionPane.showConfirmDialog(frame, panel, 
            "Edit Lost Report ID: " + item.id, 
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                if (DatabaseConnector.updateLostItem(
                        item.id, nameField.getText(), (String) categoryBox.getSelectedItem(),
                        locationField.getText(), dateField.getText(), descriptionArea.getText(), emailField.getText())) {
                    
                    JOptionPane.showMessageDialog(frame, "Report ID " + item.id + " updated successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadReportsAndMatches(reportDisplay); // Refresh the list
                } else {
                    JOptionPane.showMessageDialog(frame, "Failed to update report (No changes made).", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(frame, "Database Error during update: " + ex.getMessage(), "SQL Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    // -------------------------------------------------------------------------
    // --- CORE LOGIC: Fetch Lost Items and Find Matches (FIXED) ---
    // -------------------------------------------------------------------------

    private void loadReportsAndMatches(JTextArea reportDisplay) {
        int currentUserId = SessionManager.getCurrentUserId(); 
        
        if (currentUserId < 0) {
            reportDisplay.setText("ERROR: No active session. Please log in again.");
            return;
        }
        
        StringBuilder output = new StringBuilder();
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.connect();
            
            // 1. Fetch ALL Lost Items for the current user 
            String lostSql = "SELECT id, item_name, category, lost_date, lost_location, description, contact_email FROM Lost_Items WHERE user_id = ?";
            try (PreparedStatement lostStmt = conn.prepareStatement(lostSql)) {
                lostStmt.setInt(1, currentUserId);
                
                try (ResultSet lostRs = lostStmt.executeQuery()) {
                    
                    if (!lostRs.isBeforeFirst()) { 
                        reportDisplay.setText("\n\n\tYou have not reported any lost items yet.");
                        return;
                    }
                    
                    // Header for output section
                    output.append("====================================================================================================\n");
                    output.append(String.format("%-5s | %-25s | %-15s | %-20s\n", "ID", "LOST ITEM (Name, Category)", "DATE LOST", "LOCATION"));
                    output.append("====================================================================================================\n");
                    
                    // 2. Loop through each lost item
                    while (lostRs.next()) {
                        LostItem lostItem = new LostItem();
                        lostItem.id = lostRs.getInt("id"); 
                        lostItem.name = lostRs.getString("item_name");
                        lostItem.category = lostRs.getString("category");
                        lostItem.date = lostRs.getString("lost_date");
                        lostItem.location = lostRs.getString("lost_location");
                        lostItem.contactEmail = lostRs.getString("contact_email");
                        
                        // Display Lost Item Details (Main Row)
                        output.append(String.format("%-5d | %-25s | %-15s | %-20s\n", 
                            lostItem.id,
                            lostItem.name + " (" + lostItem.category + ")", 
                            lostItem.date, 
                            lostItem.location));
                        output.append("----------------------------------------------------------------------------------------------------\n");
                        
                        // 3. Perform Auto-Match Query
                        try (ResultSet matchRs = DatabaseConnector.autoMatchFoundItems(lostItem.name, lostItem.category, conn)) {
                            
                            if (matchRs.isBeforeFirst()) {
                                output.append("  [POSSIBLE MATCHES FOUND]:\n");
                                while (matchRs.next()) {
                                    output.append(String.format("    -> Found: %-25s | Loc: %-15s | Date: %s\n", 
                                        matchRs.getString("name"), 
                                        matchRs.getString("foundat"), 
                                        matchRs.getString("datefound")));
                                }
                            } else {
                                output.append("  (No close matches found yet. Check back later.)\n");
                            }
                        } 
                        
                        output.append("\n\n"); 
                    }
                } 
            } 
            
            reportDisplay.setText(output.toString());
            
        } catch (SQLException e) {
            reportDisplay.setText("DATABASE ERROR: Could not retrieve lost reports. Check your database connection and tables.");
            System.err.println("SQL Error in MyLostReports: " + e.getMessage());
        } finally {
            if (conn != null) {
                try { conn.close(); } catch (SQLException e) { /* ignore */ }
            }
        }
    }


    // -------------------------------------------------------------------------
    // --- STANDARDIZED UI HELPER METHODS ---
    // -------------------------------------------------------------------------
    private JPanel createStyledPanel(LayoutManager layout, Color background) {
        JPanel panel = new JPanel(layout);
        panel.setBackground(background);
        return panel;
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
        
        button.setBorder(new LineBorder(Theme.BACKGROUND_DARK, 1, true)); 

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
