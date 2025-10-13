package lostfound;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ReportFoundItem {
    private JFrame frame;

    public ReportFoundItem() {
        frame = new JFrame("Report Found Item");
        frame.setSize(450, 500);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(null);

        JLabel titleLabel = new JLabel("Report a Found Item");
        titleLabel.setBounds(150, 20, 150, 30);
        frame.add(titleLabel);

        JLabel itemNameLabel = new JLabel("Item Name:");
        itemNameLabel.setBounds(50, 70, 100, 25);
        frame.add(itemNameLabel);
        JTextField itemNameField = new JTextField();
        itemNameField.setBounds(150, 70, 250, 25);
        frame.add(itemNameField);

        JLabel foundAtLabel = new JLabel("Found At:");
        foundAtLabel.setBounds(50, 110, 100, 25);
        frame.add(foundAtLabel);
        JTextField foundAtField = new JTextField();
        foundAtField.setBounds(150, 110, 250, 25);
        frame.add(foundAtField);

        JLabel dateFoundLabel = new JLabel("Date Found:");
        dateFoundLabel.setBounds(50, 150, 100, 25);
        frame.add(dateFoundLabel);
        JTextField dateFoundField = new JTextField();
        dateFoundField.setBounds(150, 150, 250, 25);
        frame.add(dateFoundField);

        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setBounds(50, 190, 100, 25);
        frame.add(descriptionLabel);
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setBounds(150, 190, 250, 100);
        descriptionArea.setBorder(BorderFactory.createEtchedBorder());
        frame.add(descriptionArea);

        JLabel contactLabel = new JLabel("Contact Info:");
        contactLabel.setBounds(50, 310, 100, 25);
        frame.add(contactLabel);
        JTextField contactField = new JTextField();
        contactField.setBounds(150, 310, 250, 25);
        frame.add(contactField);

        JButton submitButton = new JButton("Submit");
        submitButton.setBounds(90, 370, 100, 30);
        frame.add(submitButton);

        JButton clearButton = new JButton("Clear");
        clearButton.setBounds(200, 370, 100, 30);
        frame.add(clearButton);

        JButton backButton = new JButton("Back to Home");
        backButton.setBounds(150, 420, 130, 30);
        frame.add(backButton);

        backButton.addActionListener(e -> {
            new UserDashboard();
            frame.dispose();
        });

        submitButton.addActionListener(e -> {
            String itemName = itemNameField.getText();
            String foundAt = foundAtField.getText();
            String dateFound = dateFoundField.getText();
            String description = descriptionArea.getText();
            String contact = contactField.getText();
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/lostfound", "root", "");
                String sql = "INSERT INTO founditem (name, foundat, datefound, description, contactinfo) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, itemName);
                stmt.setString(2, foundAt);
                stmt.setString(3, dateFound);
                stmt.setString(4, description);
                stmt.setString(5, contact);
                stmt.executeUpdate();
                conn.close();
                JOptionPane.showMessageDialog(frame, "Found item reported successfully!");
                new UserDashboard();
                frame.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage());
            }
        });

        clearButton.addActionListener(e -> {
            itemNameField.setText("");
            foundAtField.setText("");
            dateFoundField.setText("");
            descriptionArea.setText("");
            contactField.setText("");
        });

        frame.setVisible(true);
    }
}