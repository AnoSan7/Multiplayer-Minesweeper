package ui.theme;

import java.awt.*;
import javax.swing.*;

public class DarkTheme {
    // Theme colors
    public static final Color BACKGROUND = new Color(32, 33, 36);
    public static final Color FOREGROUND = new Color(232, 234, 237);
    public static final Color ACCENT = new Color(138, 180, 248);
    public static final Color SECONDARY = new Color(54, 55, 58);

    public static void apply() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Panel.foreground", FOREGROUND);
        UIManager.put("Button.background", SECONDARY);
        UIManager.put("Button.foreground", FOREGROUND);
        UIManager.put("Label.foreground", FOREGROUND);
        // Add more UI component styling as needed
    }
} 