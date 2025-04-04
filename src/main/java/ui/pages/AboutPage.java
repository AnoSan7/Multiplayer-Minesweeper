package ui.pages;

import ui.MainFrame;
import ui.theme.DarkTheme;

import javax.swing.*;
import java.awt.*;

public class AboutPage extends JPanel {
    public AboutPage() {
        setLayout(new BorderLayout(10, 10));
        setBackground(DarkTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("About Multiplayer Minesweeper", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DarkTheme.FOREGROUND);

        // About content - using JEditorPane with HTML for better formatting
        JEditorPane aboutContent = new JEditorPane();
        aboutContent.setEditable(false);
        aboutContent.setContentType("text/html");
        aboutContent.setBackground(DarkTheme.BACKGROUND);

        // Convert RGB color to hex for HTML
        String bgColor = String.format("#%02x%02x%02x",
            DarkTheme.BACKGROUND.getRed(),
            DarkTheme.BACKGROUND.getGreen(),
            DarkTheme.BACKGROUND.getBlue());

        // Use white text for better contrast
        String textColor = "#FFFFFF";

        // Use cyan for highlights and accents
        String highlightColor = "#00FFFF";

        // HTML content with better formatting
        aboutContent.setText(
            "<html><body style='background-color:" + bgColor + "; color:" + textColor + "; font-family:Arial; margin:10px;'>" +
            "<div style='text-align:center;'>" +
            "<h1 style='color:" + highlightColor + ";'>Multiplayer Minesweeper v2.0</h1>" +
            "</div>" +

            "<div style='margin-top:15px; padding:10px; background-color:" + DarkTheme.BACKGROUND.darker().darker() + "; border-left:4px solid " + highlightColor + ";'>" +
            "Multiplayer Minesweeper is a modern twist on the classic single-player puzzle game. " +
            "Built using Java, this version introduces a competitive multiplayer mode where players " +
            "can challenge each other in real-time, strategizing to uncover mines. The game features " +
            "real-time updates, intuitive UI, and robust networking for smooth gameplay." +
            "</div>" +

            "<h2 style='color:" + highlightColor + "; margin-top:20px;'>Features</h2>" +
            "<ul>" +
            "<li>Dark theme interface</li>" +
            "<li>Two-player multiplayer</li>" +
            "<li>Customizable settings</li>" +
            "<li>Local network play</li>" +
            "<li>Real-time game updates</li>" +
            "</ul>" +

            "<h2 style='color:" + highlightColor + "; margin-top:20px;'>Development Team</h2>" +
            "<div style='display:flex; flex-direction:column; gap:8px; margin-left:15px;'>" +
            "<div style='font-weight:bold;'>Aayush Sachan</div>" +
            "<div style='font-weight:bold;'>Anomitra Santra</div>" +
            "<div style='font-weight:bold;'>Ayush Bhagat</div>" +
            "</div>" +

            "<div style='margin-top:20px; margin-bottom:20px; border-bottom:1px solid " + highlightColor + ";'></div>" +

            "</body></html>"
        );

        // Back button with improved styling
        JButton backButton = new JButton("Back to Home");
        backButton.setBackground(DarkTheme.SECONDARY);
        backButton.setForeground(DarkTheme.FOREGROUND);
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setFocusPainted(false);
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> MainFrame.getInstance().navigateTo(MainFrame.HOME_PAGE));

        // Add components
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(aboutContent), BorderLayout.CENTER);

        // Panel for button to center it
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DarkTheme.BACKGROUND);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}