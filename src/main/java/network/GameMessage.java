package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.List;

import model.Board.MinePosition;

public class GameMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum MessageType {
        MOVE,
        GAME_OVER,
        DISCONNECT,
        RESET_REQUEST,
        PLAYER_JOINED,
        MINE_POSITIONS
    }

    private final MessageType type;
    private final int x;
    private final int y;
    private final boolean isFlag;
    private final String playerName;
    private final List<MinePosition> minePositions;

    public GameMessage(MessageType type) {
        this.type = type;
        this.x = -1;
        this.y = -1;
        this.isFlag = false;
        this.playerName = null;
        this.minePositions = null;
        System.out.println("Created message of type: " + type);
    }

    public GameMessage(MessageType type, int x, int y) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isFlag = false;
        this.playerName = null;
        this.minePositions = null;
    }

    public GameMessage(MessageType type, int x, int y, boolean isFlag) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.isFlag = isFlag;
        this.playerName = null;
        this.minePositions = null;
    }

    public GameMessage(MessageType type, String playerName) {
        this.type = type;
        this.x = -1;
        this.y = -1;
        this.isFlag = false;
        this.playerName = playerName;
        this.minePositions = null;
        System.out.println("Created message: type=" + type + ", playerName=" + playerName);
    }

    public GameMessage(MessageType type, List<MinePosition> minePositions) {
        this.type = type;
        this.x = -1;
        this.y = -1;
        this.isFlag = false;
        this.playerName = null;
        this.minePositions = minePositions;
    }

    public MessageType getType() {
        return type;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isFlag() {
        return isFlag;
    }

    public String getPlayerName() {
        return playerName;
    }

    public List<MinePosition> getMinePositions() {
        return minePositions;
    }

    public static GameMessage readFromStream(InputStream inputStream) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(inputStream)) {
            GameMessage message = (GameMessage) ois.readObject();
            System.out.println("Deserialized message of type: " + message.getType());
            return message;
        } catch (ClassNotFoundException e) {
            System.err.println("Error deserializing GameMessage: " + e.getMessage());
            throw new IOException("Failed to deserialize GameMessage", e);
        }
    }
} 