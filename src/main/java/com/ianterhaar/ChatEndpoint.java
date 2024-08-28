package com.ianterhaar;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.sql.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/chat/{username}")
public class ChatEndpoint {

    private static Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessions.add(session);
        session.getUserProperties().put("username", username);
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        String username = (String) session.getUserProperties().get("username");

        // Broadcast the message to all connected clients
        sessions.forEach(s -> {
            try {
                s.getBasicRemote().sendText(username + ": " + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        // Save message to database
        try (Connection connection = getConnection()) {
            String insertMessageQuery = "INSERT INTO messages (conversation_id, sender_id, message, timestamp) VALUES ((SELECT id FROM conversations WHERE (sender_id = (SELECT id FROM users WHERE username = ?) AND receiver_id = (SELECT id FROM users WHERE username = ?)) OR (sender_id = (SELECT id FROM users WHERE username = ?) AND receiver_id = (SELECT id FROM users WHERE username = ?))), (SELECT id FROM users WHERE username = ?), ?, CURRENT_TIMESTAMP)";
            PreparedStatement insertMessageStmt = connection.prepareStatement(insertMessageQuery);
            insertMessageStmt.setString(1, username);
            insertMessageStmt.setString(2, username);  // Adjust as needed for receiver
            insertMessageStmt.setString(3, username);
            insertMessageStmt.setString(4, username);  // Adjust as needed for receiver
            insertMessageStmt.setString(5, username);
            insertMessageStmt.setString(6, message);
            insertMessageStmt.executeUpdate();
            insertMessageStmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "03Maelks03";
        return DriverManager.getConnection(url, username, password);
    }
}
