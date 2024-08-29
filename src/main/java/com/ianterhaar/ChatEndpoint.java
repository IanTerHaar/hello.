package com.ianterhaar;

import com.google.gson.Gson;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

@ServerEndpoint("/chat/{username}")
public class ChatEndpoint {

    private static final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());
    private static final Gson gson = new Gson();

    static {
        try {
            FileHandler fileHandler = new FileHandler("websocket.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(java.util.logging.Level.ALL);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        session.getUserProperties().put("username", username);
        logger.info("WebSocket opened for user: " + username);
    }

    @OnMessage
    public void onMessage(String messageJson, Session session) {
        logger.info("Received message: " + messageJson);

        // Parse the incoming message JSON to get the conversation ID, sender, and message content
        Message message = gson.fromJson(messageJson, Message.class);
        int senderId = resolveUserId(message.getSender());

        if (senderId == -1) {
            logger.severe("User not found: " + message.getSender());
            return;
        }

        // Save the message to the database
        saveMessageToDatabase(message.getConversationId(), senderId, message.getMessage());

        try {
            // Broadcast the message to all connected sessions
            for (Session s : session.getOpenSessions()) {
                if (s.isOpen()) {
                    s.getBasicRemote().sendText(messageJson);
                }
            }
        } catch (Exception e) {
            logger.severe("Error sending message: " + e.getMessage());
        }
    }

    private void saveMessageToDatabase(int conversationId, int senderId, String messageContent) {
        try (Connection connection = getConnection()) {
            String insertMessageQuery = "INSERT INTO messages (conversation_id, sender_id, message, timestamp) " +
                    "VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
            PreparedStatement stmt = connection.prepareStatement(insertMessageQuery);
            stmt.setInt(1, conversationId);
            stmt.setInt(2, senderId);
            stmt.setString(3, messageContent);
            stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            logger.severe("Error saving message to database: " + e.getMessage());
        }
    }

    private int resolveUserId(String username) {
        int userId = -1;
        try (Connection connection = getConnection()) {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                userId = rs.getInt("id");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            logger.severe("Error resolving user ID for username " + username + ": " + e.getMessage());
        }
        return userId;
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "03Maelks03";
        return DriverManager.getConnection(url, username, password);
    }

    @OnClose
    public void onClose(Session session) {
        String username = (String) session.getUserProperties().get("username");
        if (username != null) {
            logger.info("WebSocket closed for user: " + username);
        }
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        String username = (String) session.getUserProperties().get("username");
        if (username != null) {
            logger.severe("WebSocket error for user " + username + ": " + throwable.getMessage());
        } else {
            logger.severe("WebSocket error: " + throwable.getMessage());
        }
    }
}
