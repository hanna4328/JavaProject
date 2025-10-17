package lostfound;

import java.awt.Color;
import java.awt.Font;

public class Theme {
    // --- Navy Galaxy / Glassmorphism Palette ---
    
    // Background Colors
    public static final Color BACKGROUND_DARK = new Color(10, 25, 41); // Deep Navy Blue (#0A1929)
    public static final Color BACKGROUND_LIGHT = new Color(25, 42, 63, 180); // Translucent Panel Color (Alpha 180)

    // Primary Accent Color (Soft Cyan/White)
    public static final Color ACCENT_PRIMARY = new Color(224, 247, 250); // Muted White/Cyan (#E0F7FA)
    
    // Secondary Accent Color (Hover/Active)
    public static final Color ACCENT_SECONDARY = new Color(0, 184, 255); // Vibrant Blue for hover (#00B8FF)

    // Text & UI Colors
    public static final Color TEXT_LIGHT = new Color(240, 240, 240);     // Off-White 
    
    // Fonts
    public static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 28); // Increased title font size
    public static final Font FONT_BUTTON = new Font("Arial", Font.PLAIN, 18);
    public static final Font FONT_LABEL = new Font("Arial", Font.PLAIN, 16);
}
