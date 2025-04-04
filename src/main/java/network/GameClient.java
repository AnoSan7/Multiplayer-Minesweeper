package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final ExecutorService executor;
    private final AtomicBoolean isConnected;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessageReceived(GameMessage message);
    }

    public GameClient(String host, int port) {
        this.executor = Executors.newCachedThreadPool();
        this.isConnected = new AtomicBoolean(false);
        System.out.println("GameClient created for " + host + ":" + port);
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
        System.out.println("Message handler set for client");
    }

    public void connect(String host, int port) throws IOException {
        if (isConnected.get()) {
            System.out.println("Client already connected");
            return;
        }

        try {
            System.out.println("Attempting to connect to " + host + ":" + port);
            socket = new Socket(host, port);

            // Create streams in the correct order
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush(); // Important: flush the header
            in = new ObjectInputStream(socket.getInputStream());

            isConnected.set(true);
            System.out.println("Successfully connected to server at " + host + ":" + port);

            // Start message receiving thread
            executor.submit(this::receiveMessages);
        } catch (IOException e) {
            System.err.println("Failed to connect to server: " + e.getMessage());
            e.printStackTrace();
            disconnect();
            throw e;
        }
    }

    private void receiveMessages() {
        System.out.println("Starting message receiving thread");
        try {
            while (isConnected.get() && !socket.isClosed()) {
                try {
                    System.out.println("Waiting for message from server...");
                    GameMessage message = (GameMessage) in.readObject();
                    System.out.println("Received message of type: " + message.getType() +
                        (message.getType() == GameMessage.MessageType.MOVE ?
                        " at position (" + message.getX() + "," + message.getY() + ")" : ""));

                    if (messageHandler != null) {
                        System.out.println("Forwarding message to handler");
                        messageHandler.onMessageReceived(message);
                    } else {
                        System.out.println("No message handler set!");
                    }
                } catch (IOException e) {
                    if (isConnected.get()) {
                        System.err.println("Error reading message: " + e.getMessage());
                        e.printStackTrace();
                    }
                    break;
                } catch (ClassNotFoundException e) {
                    System.err.println("Error deserializing message: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } finally {
            System.out.println("Message receiving thread ending");
            disconnect();
        }
    }

    public synchronized void sendMessage(GameMessage message) {
        if (!isConnected.get()) {
            System.err.println("Cannot send message: not connected to server");
            return;
        }

        try {
            if (out != null && socket != null && !socket.isClosed()) {
                System.out.println("Sending message of type: " + message.getType());
                out.writeObject(message);
                out.flush();
                out.reset(); // Reset the stream cache
                System.out.println("Message sent successfully");
            } else {
                System.err.println("Cannot send message: connection closed");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            disconnect();
        }
    }

    public void disconnect() {
        System.out.println("Disconnecting client");
        isConnected.set(false);
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (IOException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
        executor.shutdown();
    }

    public boolean isConnected() {
        return isConnected.get() && socket != null && !socket.isClosed();
    }
}