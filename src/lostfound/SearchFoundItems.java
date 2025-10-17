package lostfound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

public class SearchFoundItems {

    private JFrame frame;
    private JList<FoundItem> resultsList;
    private DefaultListModel<FoundItem> listModel;
    private static final int VISIBLE_LIST_ROWS = 6; 
    
    // --- (FoundItem Inner Class definition remains the same) ---
    private static class FoundItem {
        int id;
        String name;
        String category;
        String foundAt;
        String dateFound;
        String description;
        String contactInfo;

        public FoundItem(int id, String name, String category, String foundAt, String dateFound, String description, String contactInfo) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.foundAt = foundAt;
            this.dateFound = dateFound;
            this.description = description;
            this.contactInfo = contactInfo;
        }

        @Override
        public String toString() {
            return String.format("Item: %s   |   Found Location: %s", name, foundAt);
        }
    }

    public SearchFoundItems() {
        frame = new JFrame("Search Found Items");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 

        JPanel mainPanel = createStyledPanel(new BorderLayout(), Theme.BACKGROUND_DARK);
        mainPanel.setBorder(new EmptyBorder(30, 50, 30, 50)); 
        frame.add(mainPanel);

        // --- Header Panel (Title and Back Button) ---
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 30, 0)); 

        JLabel titleLabel = createStyledLabel("Search Found Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Theme.ACCENT_PRIMARY);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        JButton backButton = createStyledButton("Back to Dashboard");
        headerPanel.add(backButton, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // --- Search Form Panel (Top Center) ---
        JPanel searchForm = new JPanel(new GridBagLayout());
        searchForm.setOpaque(false);
        searchForm.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Theme.ACCENT_PRIMARY.darker(), 1, true),
            new EmptyBorder(15, 20, 15, 20)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField searchField = createStyledTextField();
        String[] categories = {"All Categories", "Electronics", "Clothing", "Keys", "Wallet", "Documents", "Other"};
        JComboBox<String> categoryComboBox = createStyledComboBox(categories);
        JButton searchButton = createStyledButton("Search");
        
        // Search Input
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.5;
        searchForm.add(searchField, gbc);
        
        // Category Filter
        gbc.gridx = 1; gbc.gridy = 0; gbc.weightx = 0.3;
        searchForm.add(categoryComboBox, gbc);
        
        // Search Button
        gbc.gridx = 2; gbc.gridy = 0; gbc.weightx = 0.2;
        searchForm.add(searchButton, gbc);

        // --- Results Display Area (JList) ---
        listModel = new DefaultListModel<>();
        resultsList = new JList<>(listModel);
        resultsList.setFont(Theme.FONT_LABEL);
        // FIX 1: Ensure JList background is opaque
        resultsList.setBackground(Theme.BACKGROUND_DARK); 
        resultsList.setForeground(Theme.TEXT_LIGHT);
        resultsList.setSelectionBackground(Theme.ACCENT_PRIMARY);
        resultsList.setSelectionForeground(Theme.BACKGROUND_DARK);
        resultsList.setFixedCellHeight(40); 
        resultsList.setVisibleRowCount(VISIBLE_LIST_ROWS); 
        
        TitledBorder resultsTitleBorder = BorderFactory.createTitledBorder(
            new LineBorder(Theme.ACCENT_PRIMARY, 1), 
            "Search Results / Recent Items (Double-click for details)", TitledBorder.LEFT, TitledBorder.TOP, 
            Theme.FONT_LABEL.deriveFont(Font.BOLD, 14), Theme.ACCENT_PRIMARY
        );
        resultsList.setBorder(resultsTitleBorder);

        JScrollPane scrollPane = new JScrollPane(resultsList);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); 
        // FIX 2: Ensure ScrollPane viewport is opaque (not strictly needed, but adds safety)
        scrollPane.getViewport().setBackground(Theme.BACKGROUND_DARK);


        // --- CORRECTED Layout for Center Content (Centralized Form + Results) ---
        JPanel contentPanel = new JPanel(new GridBagLayout()); 
        contentPanel.setOpaque(false);
        
        GridBagConstraints centerGbc = new GridBagConstraints();
        centerGbc.insets = new Insets(10, 0, 20, 0);
        centerGbc.anchor = GridBagConstraints.NORTH;
        centerGbc.fill = GridBagConstraints.NONE; 

        // 1. Add the search form (fixed size at top, centered)
        centerGbc.gridy = 0;
        centerGbc.fill = GridBagConstraints.HORIZONTAL;
        centerGbc.weightx = 0.8; 
        contentPanel.add(searchForm, centerGbc);
        
        // 2. Add the results scroll pane (fixed height, centered)
        centerGbc.gridy = 1;
        centerGbc.weighty = 0; 
        centerGbc.anchor = GridBagConstraints.NORTH; 
        centerGbc.fill = GridBagConstraints.HORIZONTAL; // Fill scroll pane horizontally
        contentPanel.add(scrollPane, centerGbc);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        // --- END FIXED LAYOUT ---


        // 1. Load recent items on startup
        loadResults("", "All Categories"); 

        // Action listener for search button
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            String category = (String) categoryComboBox.getSelectedItem();
            loadResults(query, category);
        });
        
        backButton.addActionListener(e -> {
            frame.dispose();
            new UserDashboard();
        });

        // Add Mouse Listener for item click to show details
        resultsList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) { 
                    FoundItem selectedItem = resultsList.getSelectedValue();
                    if (selectedItem != null && selectedItem.id != 0) {
                        showItemDetails(selectedItem);
                    }
                }
            }
        });

        frame.setVisible(true);
    }
    
    // --- Database Retrieval and Display Logic (omitted for brevity, assume it's correct) ---
    private void loadResults(String keyword, String category) {
        // ... (Data fetching logic remains the same) ...
        listModel.clear();
        Connection conn = null; 
        
        try {
            conn = DatabaseConnector.connect();
            ResultSet rs = DatabaseConnector.searchFoundItems(keyword, category, conn);

            List<FoundItem> items = new ArrayList<>();
            while (rs.next()) {
                items.add(new FoundItem(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("category"),
                    rs.getString("foundat"),
                    rs.getString("datefound"),
                    rs.getString("description"),
                    rs.getString("contactinfo")
                ));
            }
            
            if (items.isEmpty()) {
                listModel.addElement(new FoundItem(0, "No items found", "", "", "", "No items match your criteria.", ""));
            } else {
                for (FoundItem item : items) {
                    listModel.addElement(item);
                }
            }
            
        } catch (SQLException ex) {
            listModel.clear();
            listModel.addElement(new FoundItem(0, "Database Error", "", "", "", "Error retrieving items. Check console for details.", ""));
            System.err.println("SQL Error in loadResults: " + ex.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Failed to close connection: " + e.getMessage());
                }
            }
        }
    }


    // --- Pop-up for Item Details (Sizing and Heading Fixes) ---
    private void showItemDetails(FoundItem item) {
        JDialog detailsDialog = new JDialog(frame, "Item Details: " + item.name, true);
        detailsDialog.setLayout(new GridBagLayout());
        
        // FIX 3: Ensure dialog content pane is opaque
        detailsDialog.getContentPane().setBackground(Theme.BACKGROUND_DARK); 
        detailsDialog.getRootPane().setBorder(new LineBorder(Theme.ACCENT_PRIMARY, 2));
        
        // Set Preferred Size for wider pop-up
        detailsDialog.setPreferredSize(new Dimension(650, 450)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 15, 10, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        // Title (Item Name)
        JLabel title = new JLabel(item.name);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        // FIX 4: Change color to White (TEXT_LIGHT) and center align
        title.setForeground(Theme.TEXT_LIGHT); 
        title.setHorizontalAlignment(SwingConstants.CENTER);
        
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE;
        detailsDialog.add(title, gbc);
        
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL; // Reset fill for fields

        // Details
        addRow(detailsDialog, gbc, 1, "Category:", item.category);
        addRow(detailsDialog, gbc, 2, "Found At:", item.foundAt);
        addRow(detailsDialog, gbc, 3, "Date Found:", item.dateFound);
        addRow(detailsDialog, gbc, 4, "Contact Info:", item.contactInfo);
        
        JLabel descLabel = createStyledLabel("Description:");
        JTextArea descArea = new JTextArea(item.description);
        descArea.setWrapStyleWord(true);
        descArea.setLineWrap(true);
        descArea.setEditable(false);
        descArea.setBackground(Theme.BACKGROUND_LIGHT.darker());
        descArea.setForeground(Theme.TEXT_LIGHT);
        descArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        gbc.gridx = 0; gbc.gridy = 5; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; 
        detailsDialog.add(descLabel, gbc);
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1; 
        detailsDialog.add(new JScrollPane(descArea), gbc);

        // Close Button
        JButton closeButton = createStyledButton("Close");
        closeButton.addActionListener(e -> detailsDialog.dispose());
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER; gbc.weighty = 0;
        gbc.insets = new Insets(20, 15, 10, 15);
        detailsDialog.add(closeButton, gbc);

        detailsDialog.pack();
        detailsDialog.setLocationRelativeTo(frame);
        detailsDialog.setVisible(true);
    }
    
    // Helper for adding rows to dialog
    private void addRow(JDialog dialog, GridBagConstraints gbc, int row, String labelText, String valueText) {
        JLabel label = createStyledLabel(labelText);
        JLabel value = createStyledLabel(valueText);
        value.setFont(Theme.FONT_LABEL.deriveFont(Font.PLAIN));

        gbc.gridx = 0; gbc.gridy = row; gbc.weightx = 0;
        dialog.add(label, gbc);
        gbc.gridx = 1; gbc.gridy = row; gbc.weightx = 1;
        dialog.add(value, gbc);
    }


    // --- UI Helper Methods (Standardized) ---
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

    private JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(Theme.BACKGROUND_DARK);
        comboBox.setForeground(Theme.TEXT_LIGHT);
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setBackground(Theme.BACKGROUND_DARK);
                setForeground(Theme.TEXT_LIGHT);
                if (isSelected) {
                    setBackground(Theme.ACCENT_PRIMARY);
                    setForeground(Theme.BACKGROUND_DARK);
                }
                return this;
            }
        });
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Theme.ACCENT_PRIMARY, 1),
            BorderFactory.createEmptyBorder(5, 5, 5, 5) 
        ));
        return comboBox;
    }

    private JButton createStyledButton(String text) {
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
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(Theme.ACCENT_PRIMARY);
            }
        });
        return button;
    }
}
