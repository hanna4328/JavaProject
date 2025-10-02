package lostfound;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImagePanel extends JPanel {

    private Image backgroundImage;

    public ImagePanel(String imagePath) {
        try {
            // Check if the file exists before attempting to read it
            File imageFile = new File(imagePath);
            if (!imageFile.exists()) {
                System.err.println("Image file not found: " + imagePath);
                // You can set a fallback color or image here
                setBackground(Theme.BACKGROUND_DARK);
                return;
            }
            backgroundImage = ImageIO.read(imageFile);
        } catch (IOException e) {
            System.err.println("Could not load image: " + imagePath);
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}