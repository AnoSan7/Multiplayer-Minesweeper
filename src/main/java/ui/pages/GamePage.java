package ui.pages;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import model.Board;
import model.Cell;
import model.GameState;
import network.GameClient;
import network.GameMessage;
import network.GameServer;
import ui.MainFrame;
import ui.theme.DarkTheme;

public class GamePage extends JPanel {
    private Board board;
    private final JButton[][] buttonGrid;
    private final JPanel boardPanel;
    private final JLabel statusLabel;
    private final JLabel turnLabel;
    private final JLabel playersLabel;
    private final JLabel timerLabel;
    private final GameState gameState;
    private Timer turnTimer;
    private int timeRemaining;
    private static final int TURN_TIME = 60; // 1 minute per turn
    private boolean isTimerPaused;

    // Enhanced color scheme
    private static final Color CELL_UNREVEALED = new Color(80, 80, 95);
    private static final Color CELL_REVEALED = new Color(50, 50, 60);
    private static final Color CELL_HOVER = new Color(90, 90, 105);
    private static final Color MINE_COLOR = new Color(220, 50, 50);
    private static final Color GRID_COLOR = new Color(40, 40, 45);
    private static final Color HEADER_BG = new Color(30, 30, 40);
    private static final Font GAME_TITLE_FONT = new Font("Consolas", Font.BOLD, 18);
    private static final Font GAME_INFO_FONT = new Font("Consolas", Font.BOLD, 14);
    private static final Font CELL_FONT = new Font("Consolas", Font.BOLD, 18);
    private static final Font BUTTON_FONT = new Font("Consolas", Font.BOLD, 14);

    public GamePage() {
        gameState = GameState.getInstance();
        board = gameState.getBoard();
        setLayout(new BorderLayout(10, 10));
        setBackground(DarkTheme.BACKGROUND.darker());

        // Create enhanced status panel with gradient background
        JPanel statusPanel = new JPanel(new GridLayout(4, 1, 0, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gp = new GradientPaint(
                    0, 0, HEADER_BG,
                    0, getHeight(), DarkTheme.BACKGROUND.darker());

                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        statusPanel.setOpaque(false);
        statusPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        statusLabel = new JLabel("Game in progress", SwingConstants.CENTER);
        statusLabel.setForeground(new Color(220, 220, 220));
        statusLabel.setFont(GAME_TITLE_FONT);

        turnLabel = new JLabel("", SwingConstants.CENTER);
        turnLabel.setForeground(new Color(180, 180, 180));
        turnLabel.setFont(GAME_INFO_FONT);
        updateTurnLabel();

        playersLabel = new JLabel("", SwingConstants.CENTER);
        playersLabel.setForeground(new Color(180, 180, 180));
        playersLabel.setFont(GAME_INFO_FONT);
        updatePlayersLabel();

        timerLabel = new JLabel("", SwingConstants.CENTER);
        timerLabel.setForeground(new Color(220, 220, 80));
        timerLabel.setFont(GAME_INFO_FONT);
        updateTimerLabel();

        statusPanel.add(statusLabel);
        statusPanel.add(turnLabel);
        statusPanel.add(playersLabel);
        statusPanel.add(timerLabel);

        // Initialize timer after UI components
        timeRemaining = TURN_TIME;
        isTimerPaused = true;
        turnTimer = new Timer(1000, e -> {
            if (!isTimerPaused && timeRemaining > 0) {
                timeRemaining--;
                updateTimerLabel();
                if (timeRemaining == 0) {
                    turnTimer.stop();
                    if (gameState.isMultiplayer() && !gameState.isGameOver()) {
                        // If it's my turn and timer ran out, I lose
                        if (gameState.isPlayerTurn()) {
                            statusLabel.setText("You Lost! Time's up!");
                            gameOver(false);
                            sendGameOver(false);
                        } else {
                            // If it's opponent's turn and timer ran out, I win
                            statusLabel.setText("You Won! Opponent ran out of time!");
                            gameOver(true);
                            sendGameOver(true);
                        }
                    }
                }
            }
        });

        // Set up message handler for multiplayer
        if (gameState.isMultiplayer()) {
            System.out.println("Setting up multiplayer game. Is host: " + gameState.isHost());
            // Ensure game starts with host's turn
            gameState.setPlayerTurn(gameState.isHost());
            if (gameState.isHost()) {
                GameServer server = gameState.getServer();
                if (server != null) {
                    server.setMessageHandler(message -> SwingUtilities.invokeLater(() -> handleMessage(message)));
                    System.out.println("Host message handler set with EDT protection");
                }
            } else {
                GameClient client = gameState.getClient();
                if (client != null) {
                    client.setMessageHandler(message -> SwingUtilities.invokeLater(() -> handleMessage(message)));
                    System.out.println("Client message handler set with EDT protection");
                }
            }
            // Send player joined message after a short delay to ensure connection is ready
            new Thread(() -> {
                try {
                    Thread.sleep(500); // Small delay to ensure connection is ready
                    SwingUtilities.invokeLater(() -> sendPlayerJoined());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        // Initialize board
        buttonGrid = new JButton[board.getSize()][board.getSize()];
        boardPanel = new JPanel(new GridLayout(board.getSize(), board.getSize(), 2, 2));
        boardPanel.setBackground(GRID_COLOR);
        createBoardPanel();

        // Add components
        add(statusPanel, BorderLayout.NORTH);
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBackground(DarkTheme.BACKGROUND.darker());
        boardContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 70), 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        boardContainer.add(boardPanel, BorderLayout.CENTER);
        add(boardContainer, BorderLayout.CENTER);
        add(createControlPanel(), BorderLayout.SOUTH);

        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(60, 60, 75));
        button.setForeground(new Color(220, 220, 220));
        button.setFocusPainted(false);
        button.setFont(BUTTON_FONT);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(40, 40, 50), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 70, 90));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(60, 60, 75));
            }
        });
    }

    private void createBoardPanel() {
        // Use a darker background for grid lines and fixed cell spacing
        boardPanel.setLayout(new GridLayout(board.getSize(), board.getSize(), 2, 2));
        boardPanel.setBackground(GRID_COLOR);
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 95), 3),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));

        // Ensure the board panel respects preferred sizes
        boardPanel.setPreferredSize(new Dimension(
            board.getSize() * 42 + 8,  // 40px cell + 2px spacing + 8px border
            board.getSize() * 42 + 8
        ));

        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                JButton button = createCellButton(i, j);
                buttonGrid[i][j] = button;
                boardPanel.add(button);
            }
        }
    }

    private JButton createCellButton(int x, int y) {
        JButton button = new JButton();

        // Make perfectly square tiles with fixed dimensions
        int cellSize = 40; // Size of each cell
        button.setPreferredSize(new Dimension(cellSize, cellSize));
        button.setMinimumSize(new Dimension(cellSize, cellSize));
        button.setMaximumSize(new Dimension(cellSize, cellSize));

        // Improved button styling
        button.setBackground(CELL_UNREVEALED);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(CELL_FONT);
        button.setMargin(new Insets(0, 0, 0, 0));

        // Create a subtle 3D effect with custom border
        Border line = BorderFactory.createLineBorder(GRID_COLOR, 1);
        Border raised = BorderFactory.createRaisedBevelBorder();
        button.setBorder(new CompoundBorder(line, raised));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!board.getCells()[x][y].isRevealed()) {
                    button.setBackground(CELL_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!board.getCells()[x][y].isRevealed()) {
                    button.setBackground(CELL_UNREVEALED);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (!gameState.isGameOver() && gameState.isPlayerTurn()) {
                    if (e.getButton() == MouseEvent.BUTTON1) {
                        handleLeftClick(x, y);
                    }
                }
            }
        });

        return button;
    }

    private void handleLeftClick(int x, int y) {
        Cell cell = board.getCells()[x][y];
        if (!cell.isRevealed() && gameState.isPlayerTurn()) {
            System.out.println("\n[Turn Debug] ===== Left Click Started =====");
            System.out.println("[Turn Debug] Processing left click at (" + x + "," + y + ")");
            System.out.println("[Turn Debug] Current State - Player: " +
                (gameState.isHost() ? "Host" : "Client") + ", Turn: " +
                (gameState.isPlayerTurn() ? "Yes" : "No"));

            // If this is the first click and we're the host, place mines and send positions
            if (gameState.isHost() && !board.areMinesPlaced()) {
                board.placeMines(x, y);
                if (gameState.isMultiplayer()) {
                    sendMinePositions();
                }
                // Start timer for host's first turn
                isTimerPaused = false;
                turnTimer.start();
            }

            // Process the move
            revealCell(x, y);
            if (gameState.isMultiplayer() && !gameState.isGameOver()) {
                System.out.println("[Turn Debug] Sending move to opponent");
                sendMove(x, y);
                System.out.println("[Turn Debug] Toggling turn after sending move");
                gameState.togglePlayerTurn();
                updateTurnLabel();
                isTimerPaused = true;
                turnTimer.stop();
            }
            System.out.println("[Turn Debug] ===== Left Click Completed =====\n");
        }
    }

    private void revealCell(int x, int y) {
        if (gameState.isGameOver()) {
            return; // Don't reveal cells if game is over
        }

        Cell cell = board.getCells()[x][y];
        if (cell.isMine()) {
            // If player hits a mine, they lose - identify who lost and who won
            if (gameState.isMultiplayer()) {
                String loser = gameState.getPlayerName();
                String winner = gameState.getOpponentName();
                statusLabel.setText(loser + " hit a mine! " + winner + " wins!");
            } else {
                statusLabel.setText("Game Over! You hit a mine!");
            }
            gameOver(false);
            return;
        }

        cell.setRevealed(true);
        updateButtonAppearance(x, y);

        if (cell.getAdjacentMines() == 0) {
            revealAdjacentCells(x, y);
        }

        checkWinCondition();
    }

    private void revealAdjacentCells(int x, int y) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int newX = x + i;
                int newY = y + j;
                if (isValidPosition(newX, newY)) {
                    Cell adjacentCell = board.getCells()[newX][newY];
                    if (!adjacentCell.isRevealed() && !adjacentCell.isFlagged()) {
                        revealCell(newX, newY);
                    }
                }
            }
        }
    }

    private boolean isValidPosition(int x, int y) {
        return x >= 0 && x < board.getSize() && y >= 0 && y < board.getSize();
    }

    private void updateButtonAppearance(int x, int y) {
        Cell cell = board.getCells()[x][y];
        JButton button = buttonGrid[x][y];

        if (cell.isRevealed()) {
            // Revealed cells get sunken appearance
            Border line = BorderFactory.createLineBorder(GRID_COLOR, 1);
            Border lowered = BorderFactory.createLoweredBevelBorder();
            button.setBorder(new CompoundBorder(line, lowered));

            if (cell.isMine()) {
                // Improved mine appearance
                button.setText("✹");
                button.setFont(new Font("Dialog", Font.BOLD, 20));
                button.setBackground(MINE_COLOR);
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(CELL_REVEALED);
                if (cell.getAdjacentMines() > 0) {
                    button.setText(String.valueOf(cell.getAdjacentMines()));
                    button.setForeground(getNumberColor(cell.getAdjacentMines()));
                    button.setFont(new Font("Consolas", Font.BOLD, 20));
                } else {
                    button.setText("");
                }
            }
        } else {
            // Unrevealed cells maintain the raised appearance
            Border line = BorderFactory.createLineBorder(GRID_COLOR, 1);
            Border raised = BorderFactory.createRaisedBevelBorder();
            button.setBorder(new CompoundBorder(line, raised));
            button.setText("");
            button.setBackground(CELL_UNREVEALED);
        }
    }

    private Color getNumberColor(int number) {
        switch (number) {
            case 1: return new Color(30, 144, 255);   // Dodger Blue
            case 2: return new Color(50, 205, 50);    // Lime Green
            case 3: return new Color(255, 69, 0);     // Red-Orange
            case 4: return new Color(138, 43, 226);   // Blue Violet
            case 5: return new Color(178, 34, 34);    // Firebrick
            case 6: return new Color(64, 224, 208);   // Turquoise
            case 7: return new Color(255, 215, 0);    // Gold
            case 8: return new Color(255, 255, 255);  // White
            default: return Color.WHITE;
        }
    }

    private void checkWinCondition() {
        if (gameState.checkWinCondition()) {
            gameOver(true);
        }
    }

    private void gameOver(boolean won) {
        if (gameState.isGameOver()) {
            return;
        }
        gameState.setGameOver(true);
        turnTimer.stop();
        isTimerPaused = true;
        revealAllMines();

        if (gameState.isMultiplayer()) {
            if (won) {
                statusLabel.setText("You Won!");
            } else {
                statusLabel.setText("You Lost!");
            }
            sendGameOver(won);
        } else {
            statusLabel.setText(won ? "You Won!" : "Game Over!");
        }
    }

    private void revealAllMines() {
        Cell[][] cells = board.getCells();
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                if (cells[i][j].isMine()) {
                    cells[i][j].setRevealed(true);
                    updateButtonAppearance(i, j);
                }
            }
        }
    }

    private void resetGame() {
        if (gameState.isMultiplayer()) {
            sendResetRequest();
        } else {
            gameState.resetGame();
            board = gameState.getBoard();
            updateAllButtons();
            statusLabel.setText("Game in progress");
            updateTurnLabel();
        }
        turnTimer.stop();
        isTimerPaused = true;
        timeRemaining = TURN_TIME;
        updateTimerLabel();
    }

    private void sendResetRequest() {
        GameMessage message = new GameMessage(GameMessage.MessageType.RESET_REQUEST);
        if (gameState.isHost()) {
            // Host sends reset request to client
            GameServer server = gameState.getServer();
            if (server != null) {
                server.sendMessage(message);
            }
        } else {
            // Client sends reset request to host
            GameClient client = gameState.getClient();
            if (client != null) {
                client.sendMessage(message);
            }
        }

        // Reset the game locally after sending the message
        gameState.resetGame();
        board = gameState.getBoard();
        updateAllButtons();
        statusLabel.setText("Game in progress");
        updateTurnLabel();
        updatePlayersLabel();

        // If host, place mines after reset
        if (gameState.isHost()) {
            // Mines will be placed on first click
            System.out.println("Host reset: mines will be placed on first click");
        }
    }

    public void handleMessage(GameMessage message) {
        System.out.println("\n[Turn Debug] ===== Message Handling Started =====");
        System.out.println("[Turn Debug] Message Type: " + message.getType());
        System.out.println("[Turn Debug] Current State - Player: " +
            (gameState.isHost() ? "Host" : "Client") + ", Turn: " +
            (gameState.isPlayerTurn() ? "Yes" : "No"));

        switch (message.getType()) {
            case MINE_POSITIONS:
                if (!board.areMinesPlaced()) {
                    board.placeMinesFromPositions(message.getMinePositions());
                }
                break;

            case MOVE:
                System.out.println("[Turn Debug] Received move at (" + message.getX() +
                    "," + message.getY() + ")");

                if (!gameState.isGameOver()) {
                    if (!gameState.isHost()) {
                        System.out.println("[Turn Debug] Client processing host's move");
                        revealCell(message.getX(), message.getY());
                        boardPanel.revalidate();
                        boardPanel.repaint();
                        gameState.togglePlayerTurn();
                        updateTurnLabel();
                        isTimerPaused = false;
                        turnTimer.start();
                    } else if (!gameState.isPlayerTurn()) {
                        System.out.println("[Turn Debug] Host processing client's move");
                        revealCell(message.getX(), message.getY());
                        gameState.togglePlayerTurn();
                        updateTurnLabel();
                        isTimerPaused = false;
                        turnTimer.start();
                    }
                }
                break;

            case RESET_REQUEST:
                System.out.println("[Turn Debug] Received reset request from opponent");
                gameState.resetGame();
                board = gameState.getBoard();
                updateAllButtons();
                statusLabel.setText("Game in progress");
                updateTurnLabel();
                updatePlayersLabel();
                isTimerPaused = true;
                turnTimer.stop();
                timeRemaining = TURN_TIME;
                updateTimerLabel();

                if (!gameState.isHost()) {
                    System.out.println("[Turn Debug] Client waiting for host to place mines");
                }
                break;

            case PLAYER_JOINED:
                System.out.println("[Turn Debug] Received PLAYER_JOINED message from: " + message.getPlayerName());
                gameState.setOpponentName(message.getPlayerName());
                SwingUtilities.invokeLater(() -> {
                    updatePlayersLabel();
                    System.out.println("[Turn Debug] Updated players label with opponent name: " + message.getPlayerName());
                });

                if (gameState.isHost()) {
                    GameMessage hostNameMessage = new GameMessage(GameMessage.MessageType.PLAYER_JOINED, gameState.getPlayerName());
                    gameState.getServer().sendMessage(hostNameMessage);
                    System.out.println("[Turn Debug] Host sent their name to client: " + gameState.getPlayerName());
                }
                break;

            case GAME_OVER:
                gameState.setGameOver(true);
                revealAllMines();
                statusLabel.setText(message.isFlag() ? "You Lost!" : "You Won!");
                isTimerPaused = true;
                turnTimer.stop();
                break;

            case DISCONNECT:
                statusLabel.setText("Opponent disconnected");
                isTimerPaused = true;
                turnTimer.stop();
                break;

            default:
                System.out.println("[Turn Debug] Unhandled message type: " + message.getType());
                break;
        }
        System.out.println("[Turn Debug] ===== Message Handling Completed =====\n");
    }

    private void exitToMenu() {
        if (gameState.isMultiplayer()) {
            // Send disconnect message
            sendDisconnect();
        }
        MainFrame.getInstance().navigateTo(MainFrame.HOME_PAGE);
    }

    private void updateAllButtons() {
        for (int i = 0; i < board.getSize(); i++) {
            for (int j = 0; j < board.getSize(); j++) {
                updateButtonAppearance(i, j);
            }
        }
    }

    private void updateTurnLabel() {
        if (gameState.isMultiplayer()) {
            if (gameState.isPlayerTurn()) {
                turnLabel.setText(">> YOUR TURN <<");
                turnLabel.setForeground(new Color(100, 255, 100));
            } else {
                turnLabel.setText("OPPONENT'S TURN");
                turnLabel.setForeground(new Color(255, 160, 100));
            }
        } else {
            turnLabel.setText("SINGLE PLAYER MODE");
            turnLabel.setForeground(new Color(180, 180, 180));
        }
    }

    private void sendMove(int x, int y) {
        if (gameState.isMultiplayer()) {
            GameMessage moveMessage = new GameMessage(GameMessage.MessageType.MOVE, x, y);
            System.out.println("Sending move: x=" + x + ", y=" + y);
            if (gameState.isHost()) {
                gameState.getServer().sendMessage(moveMessage);
            } else {
                gameState.getClient().sendMessage(moveMessage);
            }
        }
    }

    private void sendGameOver(boolean won) {
        if (gameState.isMultiplayer()) {
            GameMessage message = new GameMessage(GameMessage.MessageType.GAME_OVER, 0, 0, won);
            if (gameState.isHost()) {
                GameServer server = gameState.getServer();
                if (server != null) {
                    server.sendMessage(message);
                }
            } else {
                GameClient client = gameState.getClient();
                if (client != null) {
                    client.sendMessage(message);
                }
            }
        }
    }

    private void sendDisconnect() {
        if (gameState.isMultiplayer()) {
            GameMessage message = new GameMessage(GameMessage.MessageType.DISCONNECT);
            if (gameState.isHost()) {
                GameServer server = gameState.getServer();
                if (server != null) {
                    server.sendMessage(message);
                    server.stop();
                }
            } else {
                GameClient client = gameState.getClient();
                if (client != null) {
                    client.sendMessage(message);
                    client.disconnect();
                }
            }
        }
    }

    private void updatePlayersLabel() {
        if (gameState.isMultiplayer()) {
            String playerName = gameState.getPlayerName();
            String opponentName = gameState.getOpponentName();

            if (opponentName != null && !opponentName.isEmpty()) {
                playersLabel.setText(String.format("YOU: %s | OPPONENT: %s", playerName, opponentName));
            } else {
                playersLabel.setText(String.format("YOU: %s | WAITING FOR OPPONENT...", playerName));
            }
            playersLabel.setForeground(new Color(100, 200, 255));
        } else {
            playersLabel.setText("SINGLE PLAYER MODE");
            playersLabel.setForeground(new Color(180, 180, 180));
        }
    }

    private void sendPlayerJoined() {
        if (gameState.isMultiplayer()) {
            GameMessage message = new GameMessage(GameMessage.MessageType.PLAYER_JOINED, gameState.getPlayerName());
            System.out.println("[Turn Debug] Sending PLAYER_JOINED message with name: " + gameState.getPlayerName());
            if (gameState.isHost()) {
                GameServer server = gameState.getServer();
                if (server != null) {
                    server.sendMessage(message);
                }
            } else {
                GameClient client = gameState.getClient();
                if (client != null) {
                    client.sendMessage(message);
                }
            }
        }
    }

    private void sendMinePositions() {
        GameMessage message = new GameMessage(GameMessage.MessageType.MINE_POSITIONS, board.getMinePositions());
        gameState.getServer().sendMessage(message);
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        controlPanel.setBackground(DarkTheme.BACKGROUND.darker());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JButton newGameButton = new JButton("NEW GAME");
        JButton exitButton = new JButton("EXIT TO MENU");

        styleButton(newGameButton);
        styleButton(exitButton);

        // Add special highlighting for the new game button
        newGameButton.setBackground(new Color(50, 100, 120));
        newGameButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                newGameButton.setBackground(new Color(60, 120, 140));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                newGameButton.setBackground(new Color(50, 100, 120));
            }
        });

        newGameButton.addActionListener(e -> resetGame());
        exitButton.addActionListener(e -> exitToMenu());

        controlPanel.add(newGameButton);
        controlPanel.add(exitButton);

        return controlPanel;
    }

    private void updateTimerLabel() {
        timerLabel.setText(String.format("TIME: %02d", timeRemaining));
        if (timeRemaining <= 10) {
            timerLabel.setForeground(new Color(255, 80, 80));
            // Flash the timer when low on time
            if (timeRemaining % 2 == 0) {
                timerLabel.setFont(new Font("Consolas", Font.BOLD, 16));
            } else {
                timerLabel.setFont(new Font("Consolas", Font.BOLD, 14));
            }
        } else {
            timerLabel.setForeground(new Color(220, 220, 80));
            timerLabel.setFont(GAME_INFO_FONT);
        }
    }
}