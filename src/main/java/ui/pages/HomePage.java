package ui.pages;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import config.GameConfig;
import model.GameState;
import network.GameClient;
import network.GameServer;
import ui.MainFrame;
import ui.theme.DarkTheme;

public class HomePage extends JPanel {
    private final JTextField ipField;
    private final JTextField portField;
    private final JTextField nameField;
    private GameServer server;
    private GameClient client;
    private GameConfig config = GameConfig.getInstance();

    public HomePage() {
        setLayout(new BorderLayout(0, 0));
        setBackground(DarkTheme.BACKGROUND.darker());

        // Create title panel with mining theme
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(DarkTheme.BACKGROUND.darker());
        titlePanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 30, 10));

        // Logo panel (contains title and small mine icon)
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setBackground(DarkTheme.BACKGROUND.darker());

        // Title with custom font
        JLabel titleLabel = new JLabel("MINESWEEPER");
        titleLabel.setFont(new Font("Consolas", Font.BOLD, 42));
        titleLabel.setForeground(new Color(220, 220, 220));

        // Subtitle
        JLabel subtitleLabel = new JLabel("MULTIPLAYER EDITION");
        subtitleLabel.setFont(new Font("Consolas", Font.PLAIN, 16));
        subtitleLabel.setForeground(new Color(0, 255, 255));

        // Add components vertically
        JPanel titleStack = new JPanel();
        titleStack.setLayout(new BoxLayout(titleStack, BoxLayout.Y_AXIS));
        titleStack.setBackground(DarkTheme.BACKGROUND.darker());

        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titleStack.add(titleLabel);
        titleStack.add(Box.createRigidArea(new Dimension(0, 5)));
        titleStack.add(subtitleLabel);

        logoPanel.add(titleStack);
        titlePanel.add(logoPanel, BorderLayout.CENTER);

        // Create game card panel with raised effect
        JPanel gameCardPanel = new JPanel();
        gameCardPanel.setLayout(new BoxLayout(gameCardPanel, BoxLayout.Y_AXIS));
        gameCardPanel.setBackground(DarkTheme.BACKGROUND);
        gameCardPanel.setBorder(new CompoundBorder(
            new LineBorder(DarkTheme.SECONDARY.darker(), 2),
            new EmptyBorder(25, 25, 25, 25)
        ));

        // Create input panel with gaming style
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        inputPanel.setBackground(DarkTheme.BACKGROUND);
        inputPanel.setMaximumSize(new Dimension(450, 150));
        inputPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create styled labels and fields
        JLabel nameLabel = createStyledLabel("PLAYER NAME:");
        nameField = createStyledField("Player");

        JLabel ipLabel = createStyledLabel("SERVER IP:");
        ipField = createStyledField("localhost");

        JLabel portLabel = createStyledLabel("PORT:");
        portField = createStyledField("5000");

        inputPanel.add(nameLabel);
        inputPanel.add(nameField);
        inputPanel.add(ipLabel);
        inputPanel.add(ipField);
        inputPanel.add(portLabel);
        inputPanel.add(portField);

        // Create buttons panel with gaming style
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
        buttonsPanel.setBackground(DarkTheme.BACKGROUND);
        buttonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        buttonsPanel.setMaximumSize(new Dimension(450, 150));

        JButton hostButton = createGameButton("HOST NEW GAME");
        JButton joinButton = createGameButton("JOIN GAME");

        hostButton.addActionListener(e -> hostGame());
        joinButton.addActionListener(e -> joinGame());

        // Add spacing between elements
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonsPanel.add(hostButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonsPanel.add(joinButton);
        buttonsPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add elements to card panel
        gameCardPanel.add(inputPanel);
        gameCardPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        gameCardPanel.add(buttonsPanel);

        // Panel to center the card
        JPanel centeringPanel = new JPanel(new GridBagLayout());
        centeringPanel.setBackground(DarkTheme.BACKGROUND.darker());
        centeringPanel.add(gameCardPanel);

        // Footer panel for help & about
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        footerPanel.setBackground(DarkTheme.BACKGROUND.darker());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        JButton helpButton = createFooterButton("HELP");
        JButton aboutButton = createFooterButton("ABOUT");

        helpButton.addActionListener(e -> MainFrame.getInstance().navigateTo(MainFrame.HELP_PAGE));
        aboutButton.addActionListener(e -> MainFrame.getInstance().navigateTo(MainFrame.ABOUT_PAGE));

        footerPanel.add(helpButton);
        footerPanel.add(aboutButton);

        // Add all components
        add(titlePanel, BorderLayout.NORTH);
        add(centeringPanel, BorderLayout.CENTER);
        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Consolas", Font.BOLD, 14));
        label.setForeground(new Color(180, 180, 180));
        return label;
    }

    private JTextField createStyledField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Consolas", Font.PLAIN, 14));
        field.setBackground(DarkTheme.BACKGROUND.brighter());
        field.setForeground(Color.WHITE);
        field.setCaretColor(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.SECONDARY.darker(), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createGameButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Consolas", Font.BOLD, 16));
        button.setBackground(new Color(0, 102, 153));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0, 153, 204), 1),
            BorderFactory.createEmptyBorder(12, 30, 12, 30)
        ));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(0, 120, 180));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 102, 153));
            }
        });

        return button;
    }

    private JButton createFooterButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Consolas", Font.BOLD, 12));
        button.setBackground(DarkTheme.BACKGROUND);
        button.setForeground(new Color(180, 180, 180));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(DarkTheme.SECONDARY.darker(), 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setForeground(Color.WHITE);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setForeground(new Color(180, 180, 180));
            }
        });

        return button;
    }

    // The rest of your existing methods remain unchanged
    private void hostGame() {
        // Existing code remains the same...
        try {
            int port = Integer.parseInt(portField.getText());
            String playerName = nameField.getText().trim();
            if (playerName.isEmpty()) {
                playerName = "Player";
            }

            // Set the port in GameConfig
            config.setPort(port);

            // Initialize GameState first
            GameState gameState = GameState.getInstance();
            gameState.resetGame(); // Ensure clean state
            gameState.setPlayerName(playerName);
            gameState.startMultiplayerGame(true); // Set multiplayer mode before server creation

            // Create and start server
            server = new GameServer();
            gameState.setServer(server); // Set server before starting it
            server.start();

            // Wait for server to be ready
            int maxWaitSeconds = 5;
            int waited = 0;
            while (!server.isReady() && waited < maxWaitSeconds) {
                Thread.sleep(1000);
                waited++;
            }

            if (!server.isReady()) {
                throw new Exception("Server failed to start after " + maxWaitSeconds + " seconds");
            }

            // Refresh the game page first
            MainFrame.getInstance().refreshGamePage();
            // Then navigate to game page
            MainFrame.getInstance().navigateTo(MainFrame.GAME_PAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid port number",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            if (server != null) server.stop();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to host game: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            if (server != null) server.stop();
        }
    }

    private void joinGame() {
        // Existing code remains the same...
        try {
            String ip = ipField.getText();
            int port = Integer.parseInt(portField.getText());
            final String playerName = nameField.getText().trim().isEmpty() ? "Player" : nameField.getText().trim();

            // Set the port in GameConfig
            config.setPort(port);

            // Initialize GameState first
            GameState gameState = GameState.getInstance();
            gameState.resetGame(); // Ensure clean state
            gameState.setPlayerName(playerName);
            gameState.startMultiplayerGame(false); // Set multiplayer mode before client creation

            // Create client
            client = new GameClient(ip, port);
            gameState.setClient(client); // Set client before connecting

            // Create connecting dialog
            final JDialog dialog = new JDialog((JFrame)SwingUtilities.getWindowAncestor(this), "Connecting", true);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            panel.setBackground(DarkTheme.BACKGROUND);

            JLabel label = new JLabel("Connecting to server...");
            label.setForeground(DarkTheme.FOREGROUND);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            panel.add(label, BorderLayout.CENTER);

            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                client.disconnect();
                dialog.dispose();
            });
            panel.add(cancelButton, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.pack();
            dialog.setLocationRelativeTo(this);

            // Try to connect in a separate thread
            Thread connectThread = new Thread(() -> {
                try {
                    client.connect(ip, port);
                    if (client.isConnected()) {
                        SwingUtilities.invokeLater(() -> {
                            dialog.dispose();
                            // Refresh game page and navigate in the EDT
                            MainFrame.getInstance().refreshGamePage();
                        });
                    }
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() -> {
                        dialog.dispose();
                        JOptionPane.showMessageDialog(this,
                            "Failed to join game: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                        if (client != null) client.disconnect();
                    });
                }
            });

            // Start connection thread
            connectThread.start();

            // Show dialog (this will block until disposed)
            dialog.setVisible(true);

            // If dialog is closed and thread is still running, interrupt it
            if (connectThread.isAlive()) {
                connectThread.interrupt();
                if (client != null) {
                    client.disconnect();
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Invalid port number",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            if (client != null) client.disconnect();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Failed to join game: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
            if (client != null) client.disconnect();
        }
    }

    // Cleanup method to be called when navigating away
    public void cleanup() {
        if (server != null) {
            server.stop();
        }
        if (client != null) {
            client.disconnect();
        }
    }
}