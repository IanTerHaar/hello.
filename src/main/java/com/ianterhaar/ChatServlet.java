package com.ianterhaar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;

@WebServlet("/chat")
public class ChatServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ChatServlet.class.getName());

    static {
        try {
            FileHandler fileHandler = new FileHandler("application.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Connection getConnection() throws SQLException {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String username = "postgres";
        String password = "03Maelks03";
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("username") != null) {
            String loggedInUser = (String) session.getAttribute("username");
            String otherUser = request.getParameter("otherUser");

            if (otherUser == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No user specified");
                return;
            }

            logger.info("Logged in user: " + loggedInUser);
            logger.info("Other user: " + otherUser);

            try (Connection connection = getConnection()) {
                logger.info("Database connection established.");

                // Fetching conversation ID
                String conversationQuery = "SELECT id FROM conversations WHERE (sender_id = (SELECT id FROM users WHERE username = ?) AND receiver_id = (SELECT id FROM users WHERE username = ?)) OR (sender_id = (SELECT id FROM users WHERE username = ?) AND receiver_id = (SELECT id FROM users WHERE username = ?))";
                PreparedStatement conversationStmt = connection.prepareStatement(conversationQuery);
                conversationStmt.setString(1, loggedInUser);
                conversationStmt.setString(2, otherUser);
                conversationStmt.setString(3, otherUser);
                conversationStmt.setString(4, loggedInUser);
                ResultSet rs = conversationStmt.executeQuery();

                int conversationId = -1;
                if (rs.next()) {
                    conversationId = rs.getInt("id");
                }

                // If no conversation exists, create a new one
                if (conversationId == -1) {
                    String insertConversationQuery = "INSERT INTO conversations (sender_id, receiver_id, created_at) VALUES ((SELECT id FROM users WHERE username = ?), (SELECT id FROM users WHERE username = ?), CURRENT_TIMESTAMP) RETURNING id";
                    PreparedStatement insertConversationStmt = connection.prepareStatement(insertConversationQuery);
                    insertConversationStmt.setString(1, loggedInUser);
                    insertConversationStmt.setString(2, otherUser);
                    ResultSet insertRs = insertConversationStmt.executeQuery();
                    if (insertRs.next()) {
                        conversationId = insertRs.getInt("id");
                    }
                    insertRs.close();
                    insertConversationStmt.close();
                }

                // Fetch chat history
                String messagesQuery = "SELECT sender_id, message, timestamp FROM messages WHERE conversation_id = ?";
                PreparedStatement messagesStmt = connection.prepareStatement(messagesQuery);
                messagesStmt.setInt(1, conversationId);
                ResultSet messagesRs = messagesStmt.executeQuery();

                List<Message> messages = new ArrayList<>();
                while (messagesRs.next()) {
                    String senderId = messagesRs.getString("sender_id");
                    String messageContent = messagesRs.getString("message");
                    Timestamp timestamp = messagesRs.getTimestamp("timestamp");
                    messages.add(new Message(senderId, messageContent, timestamp));
                }

                request.setAttribute("messages", messages);
                request.getRequestDispatcher("/home.jsp").forward(request, response);

                messagesRs.close();
                messagesStmt.close();
                conversationStmt.close();

            } catch (SQLException e) {
                logger.severe("SQL Exception: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error");
            }
        } else {
            logger.warning("Session or username not found. Redirecting to login.");
            response.sendRedirect("login.html");
        }
    }
}
