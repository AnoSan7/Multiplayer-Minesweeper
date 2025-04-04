package network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import config.GameConfig;

public class GameServer {
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private final ExecutorService executor;
    private final AtomicBoolean isRunning;
    private final AtomicBoolean isReady;
    private MessageHandler messageHandler;

    public interface MessageHandler {
        void onMessageReceived(GameMessage message);
    }

    public GameServer() {
        this.executor = Executors.newCachedThreadPool();
        this.isRunning = new AtomicBoolean(false);
        this.isReady = new AtomicBoolean(false);
        System.out.println("GameServer created");
    }

    public void setMessageHandler(MessageHandler handler) {
        this.messageHandler = handler;
        System.out.println("Message handler set for server");
    }

    public void start() {
        if (isRunning.get()) {
            System.out.println("Server already running");
            return;
        }

        executor.submit(() -> {
            try {
                int port = GameConfig.getInstance().getPort();
                System.out.println("Starting server on port " + port);
                serverSocket = new ServerSocket(port);
                isRunning.set(true);
                isReady.set(true);
                System.out.println("Server started successfully on port " + port);

                while (isRunning.get()) {
                    try {
                        System.out.println("Waiting for client connection...");
                        // Accept new client connection
                        clientSocket = serverSocket.accept();
                        System.out.println("New client connected: " + clientSocket.getInetAddress());

                        // Create streams in the correct order - ONCE per client
                        out = new ObjectOutputStream(clientSocket.getOutputStream());
                        out.flush(); // Important: flush the header
                        in = new ObjectInputStream(clientSocket.getInputStream());

                        // Handle client communication in a separate thread
                        handleClient();
                    } catch (IOException e) {
                        if (isRunning.get()) {
                            System.err.println("Error accepting client connection: " + e.getMessage());
                            cleanupClientConnection();
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Failed to start server: " + e.getMessage());
                stop();
            }
        });
    }

    private void cleanupClientConnection() {
        try {
            if (in != null) {
                in.close();
                in = null;
            }
            if (out != null) {
                out.close();
                out = null;
            }
            if (clientSocket != null) {
                clientSocket.close();
                clientSocket = null;
            }
        } catch (IOException e) {
            System.err.println("Error cleaning up client connection: " + e.getMessage());
        }
    }

    private void handleClient() {
        System.out.println("Starting client handler thread");
        executor.submit(() -> {
            try {
                while (isRunning.get() && !clientSocket.isClosed()) {
                    try {
                        System.out.println("Waiting for message from client...");
                        GameMessage message = (GameMessage) in.readObject();
                        System.out.println("Received message of type: " + message.getType());

                        if (messageHandler != null) {
                            System.out.println("Forwarding message to handler");
                            messageHandler.onMessageReceived(message);
                        }

                        // IMPORTANT: DO NOT echo MOVE messages back to the sender
                        // This is what's causing the turn desynchronization
                        if (message.getType() == GameMessage.MessageType.MOVE) {
                            System.out.println("MOVE message received, processing locally but NOT echoing back");
                            // Remove or comment out any code that sends this message back
                            // DO NOT: sendMessage(message);
                        } else {
                            // Handle other messages normally
                            // For non-MOVE messages, sending back might be appropriate
                            // depending on your game logic
                        }

                    } catch (IOException e) {
                        if (isRunning.get()) {
                            System.err.println("Error reading message: " + e.getMessage());
                        }
                        break;
                    } catch (ClassNotFoundException e) {
                        System.err.println("Error deserializing message: " + e.getMessage());
                    }
                }
            } finally {
                System.out.println("Client handler thread ending");
                cleanupClientConnection();
            }
        });
    }

    public synchronized void sendMessage(GameMessage message) {
        if (!isRunning.get()) {
            System.err.println("Cannot send message: server not running");
            return;
        }

        try {
            if (out != null && clientSocket != null && !clientSocket.isClosed()) {
                System.out.println("Sending message of type: " + message.getType());
                // Add debug info for moves
                if (message.getType() == GameMessage.MessageType.MOVE) {
                    System.out.println("Sending MOVE at position (" + message.getX() + "," + message.getY() +"), isFlag=" + message.isFlag());
                }

                out.writeObject(message);
                out.flush();
                out.reset(); // Reset the stream cache to prevent memory leaks
                System.out.println("Message sent successfully");
            } else {
                System.err.println("Cannot send message: no client connected or connection closed");
            }
        } catch (IOException e) {
            System.err.println("Error sending message: " + e.getMessage());
            e.printStackTrace();
            cleanupClientConnection();
        }
    }

    public void stop() {
        System.out.println("Stopping server");
        isRunning.set(false);
        isReady.set(false);
        cleanupClientConnection();
        try {
            if (serverSocket != null) {
                serverSocket.close();
                serverSocket = null;
            }
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
        executor.shutdown();
    }

    public boolean isRunning() {
        return isRunning.get();
    }

    public boolean isReady() {
        return isReady.get();
    }
}