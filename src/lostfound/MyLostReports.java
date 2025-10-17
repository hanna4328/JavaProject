package lostfound;

import javax.swing.*;
import java.awt.*;
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

    // A helper class to hold the retrieved Lost Item details (internal use)
    private static class LostItem {
        String name;
        String category;
        String date;
        String location;
        String description;
    }

    public MyLostReports() {
        frame = new JFrame("My Lost Reports & Matches");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        JPanel mainPanel = createStyledPanel(new BorderLayout(), Theme.BACKGROUND_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        frame.add(mainPanel);

        // --- Header Panel (Title and Back Button) ---
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel titleLabel = createStyledLabel("My Lost Reports & Possible Matches");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to Dashboard");
        headerPanel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // --- Content Panel: JTextArea for Structured Reports ---
        JTextArea reportDisplay = new JTextArea();
        // Use Monospaced font for neat alignment of columns
        reportDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14)); 
        reportDisplay.setForeground(Theme.TEXT_LIGHT);
        reportDisplay.setBackground(Theme.BACKGROUND_DARK);
        reportDisplay.setEditable(false);

        // Add a titled border for visual structure
        TitledBorder resultsTitleBorder = BorderFactory.createTitledBorder(
            new LineBorder(Theme.ACCENT_PRIMARY, 1), 
            "Report Status and System Matches", TitledBorder.LEFT, TitledBorder.TOP, 
            new Font(Font.SANS_SERIF, Font.BOLD, 14), Theme.ACCENT_PRIMARY
        );
        reportDisplay.setBorder(BorderFactory.createCompoundBorder(resultsTitleBorder, new EmptyBorder(10, 10, 10, 10)));
        
        JScrollPane scrollPane = new JScrollPane(reportDisplay);
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_DARK); 
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // --- Load Reports and Matches on Startup ---
        loadReportsAndMatches(reportDisplay);

        backButton.addActionListener(e -> {
            frame.dispose();
            new UserDashboard();
        });

        frame.setVisible(true);
    }

    // -------------------------------------------------------------------------
    // --- CORE LOGIC: Fetch Lost Items and Find Matches ---
    // -------------------------------------------------------------------------

    private void loadReportsAndMatches(JTextArea reportDisplay) {
        // NOTE: We assume the logged-in user's ID is 1 for testing purposes.
        int currentUserId = 1; 
        
        StringBuilder output = new StringBuilder();
        Connection conn = null;
        
        try {
            conn = DatabaseConnector.connect();
            
            // 1. Fetch ALL Lost Items reported by the current user
            String lostSql = "SELECT item_name, category, lost_date, lost_location, description FROM Lost_Items WHERE user_id = ?";
            try (PreparedStatement lostStmt = conn.prepareStatement(lostSql)) {
                lostStmt.setInt(1, currentUserId);
                
                try (ResultSet lostRs = lostStmt.executeQuery()) {
                    
                    if (!lostRs.isBeforeFirst()) { 
                        reportDisplay.setText("\n\n\tYou have not reported any lost items yet.");
                        return;
                    }
                    
                    // Header for output section
                    output.append("=========================================================================================\n");
                    output.append(String.format("%-25s | %-15s | %-20s\n", "LOST ITEM (Name, Category)", "DATE LOST", "LOCATION"));
                    output.append("=========================================================================================\n");
                    
                    // 2. Loop through each lost item
                    while (lostRs.next()) {
                        LostItem lostItem = new LostItem();
                        lostItem.name = lostRs.getString("item_name");
                        lostItem.category = lostRs.getString("category");
                        lostItem.date = lostRs.getString("lost_date");
                        lostItem.location = lostRs.getString("lost_location");
                        lostItem.description = lostRs.getString("description");
                        
                        // Display Lost Item Details (Main Row)
                        output.append(String.format("%-25s | %-15s | %-20s\n", 
                            lostItem.name + " (" + lostItem.category + ")", 
                            lostItem.date, 
                            lostItem.location));
                        output.append("-----------------------------------------------------------------------------------------\n");
                        
                        // 3. Perform Auto-Match Query
                        // The autoMatchFoundItems method opens its own connection and handles its execution
                        try (ResultSet matchRs = DatabaseConnector.autoMatchFoundItems(lostItem.name, lostItem.category)) {
                            
                            if (matchRs.isBeforeFirst()) {
                                output.append("  [POSSIBLE MATCHES FOUND]\n");
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
                        
                        output.append("\n\n"); // Extra space before the next report
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
    // --- STANDARDIZED UI HELPER METHODS (Navy Galaxy Theme) ---
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

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        // Uses Theme.FONT_BUTTON (Font.PLAIN, 18) for the thinner look
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
}
