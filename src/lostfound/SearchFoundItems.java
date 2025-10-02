package lostfound;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SearchFoundItems {
    
    private GraphicsDevice graphicsDevice;
    private JFrame frame;

    public SearchFoundItems() {
        graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        
        frame = new JFrame("Search Found Items");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        if (graphicsDevice.isFullScreenSupported()) {
            graphicsDevice.setFullScreenWindow(frame);
        } else {
            frame.setSize(800, 600);
            frame.setLocationRelativeTo(null);
        }

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Theme.BACKGROUND_DARK);
        
        JLabel messageLabel = new JLabel("Search Found Items UI Coming Soon!");
        messageLabel.setFont(Theme.FONT_TITLE);
        messageLabel.setForeground(Theme.TEXT_LIGHT);
        panel.add(messageLabel);
        
        frame.add(panel);
        frame.setVisible(true);
        
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