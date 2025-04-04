package model;

import network.GameClient;
import network.GameServer;

public class GameState {
    private static GameState instance;
    private Board board;
    private boolean isGameOver;
    private boolean isPlayerTurn;
    private int currentPlayer;
    private boolean isMultiplayer;
    private boolean isHost;
    private GameServer server;
    private GameClient client;
    private String playerName;
    private String opponentName;

    private GameState() {
        resetGame();
    }

    public static GameState getInstance() {
        if (instance == null) {
            instance = new GameState();
        }
        return instance;
    }

    public void resetGame() {
        // Store multiplayer state before reset
        boolean wasMultiplayer = isMultiplayer;
        boolean wasHost = isHost;
        GameServer savedServer = server;
        GameClient savedClient = client;
        String savedPlayerName = playerName;
        String savedOpponentName = opponentName;

        // Reset game state
        board = new Board();
        isGameOver = false;
        isPlayerTurn = wasHost; // Host plays first
        currentPlayer = 1;

        // Restore multiplayer state
        isMultiplayer = wasMultiplayer;
        isHost = wasHost;
        server = savedServer;
        client = savedClient;
        playerName = savedPlayerName;
        opponentName = savedOpponentName;
    }

    public void startMultiplayerGame(boolean asHost) {
        resetGame();
        isMultiplayer = true;
        isHost = asHost;
        isPlayerTurn = asHost; // Host plays first
    }

    public boolean checkWinCondition() {
        Cell[][] cells = board.getCells();
        for (Cell[] row : cells) {
            for (Cell cell : row) {
                if (cell.isMine() && !cell.isFlagged()) {
                    return false;
                }
                if (!cell.isMine() && !cell.isRevealed()) {
                    return false;
                }
            }
        }
        return true;
    }

    // Getters and setters
    public Board getBoard() { return board; }
    public boolean isGameOver() { return isGameOver; }
    public void setGameOver(boolean gameOver) { isGameOver = gameOver; }
    public boolean isPlayerTurn() { return isPlayerTurn; }
    public void togglePlayerTurn() {
        isPlayerTurn = !isPlayerTurn;
        System.out.println("[Turn Debug] Turn toggled - Player: " +
            (isHost ? "Host" : "Client") +
            ", New Turn State: " + (isPlayerTurn ? "Yes" : "No"));
    }
    public void setPlayerTurn(boolean playerTurn) {
        if (this.isPlayerTurn != playerTurn) {
            System.out.println("[Turn Debug] Setting turn explicitly - Player: " +
                (isHost ? "Host" : "Client") +
                ", From: " + (this.isPlayerTurn ? "Yes" : "No") +
                " To: " + (playerTurn ? "Yes" : "No"));
            this.isPlayerTurn = playerTurn;
        }
    }
    public int getCurrentPlayer() { return currentPlayer; }
    public void setCurrentPlayer(int player) { currentPlayer = player; }
    public boolean isMultiplayer() { return isMultiplayer; }
    public boolean isHost() { return isHost; }
    public GameServer getServer() { return server; }
    public void setServer(GameServer server) { this.server = server; }
    public GameClient getClient() { return client; }
    public void setClient(GameClient client) { this.client = client; }
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String name) { this.playerName = name; }
    public String getOpponentName() { return opponentName; }
    public void setOpponentName(String name) { this.opponentName = name; }

    public void applyMove(int x, int y, boolean isFlag) {
        if (isFlag) {
            board.toggleFlag(x, y);
        } else {
            board.revealCell(x, y);
        }
        togglePlayerTurn();
    }
}