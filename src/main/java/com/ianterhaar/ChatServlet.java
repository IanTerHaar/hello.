package com.ianterhaar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
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
            String conversationId = request.getParameter("conversationId");

            if (conversationId == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "No conversation specified");
                return;
            }

            try (Connection connection = getConnection()) {
                logger.info("Database connection established.");

                // Adjusted SQL query to use aliases for clarity
                String messagesQuery = "SELECT u.username AS sender_username, m.message AS message_content, m.timestamp AS message_timestamp " +
                        "FROM messages m " +
                        "JOIN users u ON m.sender_id = u.id " +
                        "WHERE m.conversation_id = ? " +
                        "ORDER BY m.timestamp ASC";

                PreparedStatement messagesStmt = connection.prepareStatement(messagesQuery);
                messagesStmt.setInt(1, Integer.parseInt(conversationId));
                ResultSet messagesRs = messagesStmt.executeQuery();

                List<Message> messages = new ArrayList<>();
                while (messagesRs.next()) {
                    Message message = new Message(
                            Integer.parseInt(conversationId),
                            messagesRs.getString("sender_username"),
                            messagesRs.getString("message_content"),
                            messagesRs.getTimestamp("message_timestamp")
                    );
                    messages.add(message);
                }

                response.setContentType("application/json");
                PrintWriter out = response.getWriter();
                out.println(toJson(messages));

                messagesRs.close();
                messagesStmt.close();

            } catch (SQLException e) {
                logger.severe("SQL Exception: " + e.getMessage());
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database connection error");
            }
        } else {
            logger.warning("Session or username not found. Redirecting to login.");
            response.sendRedirect("login.html");
        }
    }

    private String toJson(List<Message> messages) {
        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < messages.size(); i++) {
            Message msg = messages.get(i);
            json.append("{")
                    .append("\"sender\":\"").append(msg.getSender()).append("\",")
                    .append("\"message\":\"").append(msg.getMessage()).append("\",")
                    .append("\"timestamp\":\"").append(msg.getTimestamp()).append("\"}")
                    .append(i < messages.size() - 1 ? "," : "");
        }
        json.append("]");
        return json.toString();
    }
}
