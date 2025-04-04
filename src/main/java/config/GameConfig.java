package config;

public class GameConfig {
    private static GameConfig instance;
    private int port = 12345; // Default port

    private GameConfig() {}

    public static GameConfig getInstance() {
        if (instance == null) {
            instance = new GameConfig();
        }
        return instance;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
} 