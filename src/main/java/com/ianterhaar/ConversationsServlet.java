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

@WebServlet("/conversations")
public class ConversationsServlet extends HttpServlet {

    private static final Logger logger = Logger.getLogger(ConversationsServlet.class.getName());

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
            logger.info("Logged in user: " + loggedInUser);

            try (Connection connection = getConnection()) {
                logger.info("Database connection established.");

                String userQuery = "SELECT id FROM users WHERE username = ?";
                PreparedStatement userStmt = connection.prepareStatement(userQuery);
                userStmt.setString(1, loggedInUser);
                ResultSet rs = userStmt.executeQuery();

                if (rs.next()) {
                    int userId = rs.getInt("id");
                    logger.info("User ID for " + loggedInUser + ": " + userId);

                    String query = "SELECT DISTINCT u.username FROM users u " +
                            "JOIN conversations c ON (u.id = c.sender_id OR u.id = c.receiver_id) " +
                            "WHERE (c.sender_id = ? OR c.receiver_id = ?) AND u.id != ?";
                    PreparedStatement stmt = connection.prepareStatement(query);
                    stmt.setInt(1, userId);
                    stmt.setInt(2, userId);
                    stmt.setInt(3, userId);

                    ResultSet conversationResults = stmt.executeQuery();
                    List<String> participants = new ArrayList<>();

                    while (conversationResults.next()) {
                        participants.add(conversationResults.getString("username"));
                    }

                    logger.info("Conversations participants fetched: " + participants);

                    request.setAttribute("conversations", participants);
                    stmt.close();

                    request.getRequestDispatcher("/home.jsp").forward(request, response);
                } else {
                    logger.warning("No user found with username: " + loggedInUser);
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
                }

                rs.close();
                userStmt.close();

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
