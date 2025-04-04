package ui.pages;

import ui.MainFrame;
import ui.theme.DarkTheme;

import javax.swing.*;
import java.awt.*;

public class HelpPage extends JPanel {
    public HelpPage() {
        setLayout(new BorderLayout(10, 10));
        setBackground(DarkTheme.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Title
        JLabel titleLabel = new JLabel("How to Play", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(DarkTheme.FOREGROUND);

        // Help content with HTML formatting for better appearance
        JEditorPane helpContent = new JEditorPane();
        helpContent.setEditable(false);
        helpContent.setContentType("text/html");
        helpContent.setBackground(DarkTheme.BACKGROUND);

        // Convert RGB color to hex for HTML
        String bgColor = String.format("#%02x%02x%02x",
            DarkTheme.BACKGROUND.getRed(),
            DarkTheme.BACKGROUND.getGreen(),
            DarkTheme.BACKGROUND.getBlue());

        // Brighter text color for better visibility
        String textColor = "#FFFFFF"; // Pure white for better contrast

        String accentColor = String.format("#%02x%02x%02x",
            DarkTheme.SECONDARY.getRed(),
            DarkTheme.SECONDARY.getGreen(),
            DarkTheme.SECONDARY.getBlue());

        // Highlight color for important elements
        String highlightColor = "#00FFFF"; // Cyan for emphasis

        // HTML content with better formatting and improved contrast
        helpContent.setText(
            "<html><body style='background-color:" + bgColor + "; color:" + textColor + "; font-family:Arial; margin:10px;'>" +

            "<h2 style='color:" + highlightColor + "; text-align:center;'>Objective</h2>" +
            "<div style='background-color:" + DarkTheme.BACKGROUND.darker().darker() + "; border-left:4px solid " + highlightColor + "; padding:10px; margin:10px 0; color:" + textColor + ";'>" +
            "The goal of Multiplayer Minesweeper is to uncover all non-mine squares. Avoid triggering mines while strategically revealing safe tiles." +
            "</div>" +

            "<h2 style='color:" + highlightColor + "; margin-top:20px;'>How to Play</h2>" +

            "<h3 style='color:" + highlightColor + ";'>1. Starting the Game</h3>" +
            "<ul style='color:" + textColor + ";'>" +
            "<li>Join a multiplayer lobby or create your own game room</li>" +
            "<li>The game starts when all players are ready</li>" +
            "</ul>" +

            "<h3 style='color:" + highlightColor + ";'>2. Taking Turns</h3>" +
            "<ul style='color:" + textColor + ";'>" +
            "<li>On your turn, you must click on one tile to reveal it</li>" +
            "<li>If the tile is a number, it indicates the number of mines in adjacent tiles</li>" +
            "<li>If it is an empty tile, it will automatically reveal adjacent empty tiles</li>" +
            "<li>If you click on a mine, you immediately lose the game</li>" +
            "</ul>" +

            "<h3 style='color:" + highlightColor + ";'>3. Winning & Losing Conditions</h3>" +
            "<ul style='color:" + textColor + ";'>" +
            "<li>If a player clicks on a bomb, they instantly lose, and the opponent wins</li>" +
            "<li>If all safe tiles are revealed without any player clicking on a mine, the game ends in a draw</li>" +
            "</ul>" +

            "<h3 style='color:" + highlightColor + "; margin-top:20px;'>Controls</h3>" +
            "<div style='display:grid; grid-template-columns:auto 1fr; gap:10px; margin:10px 0;'>" +
            "<div style='font-weight:bold; color:" + highlightColor + ";'>Left-click:</div><div style='color:" + textColor + ";'>Reveal a cell</div>" +
            "<div style='font-weight:bold; color:" + highlightColor + ";'>Right-click:</div><div style='color:" + textColor + ";'>Place/remove a flag</div>" +
            "</div>" +

            "<div style='margin-top:20px; margin-bottom:20px; border-bottom:1px solid " + highlightColor + ";'></div>" +

            "<div style='text-align:center; font-style:italic; margin-top:20px; color:" + textColor + ";'>" +
            "Coordinate with your partner to win!" +
            "</div>" +

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
        add(new JScrollPane(helpContent), BorderLayout.CENTER);

        // Panel for button to center it
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(DarkTheme.BACKGROUND);
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
}