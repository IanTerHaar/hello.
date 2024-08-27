package com.ianterhaar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/loginServlet")
public class LoginServlet extends HttpServlet {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "03Maelks03";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Hash the password entered by the user before comparing with the database
        String hashedPassword = hashPassword(password);

        try {
            // Load the PostgreSQL JDBC driver
            Class.forName("org.postgresql.Driver");

            // Establish the connection
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "SELECT name FROM users WHERE username = ? AND password = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, username);
                    statement.setString(2, hashedPassword);

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            // Login successful, retrieve the user's name
                            String name = resultSet.getString("name");

                            // Store the name in session
                            HttpSession session = request.getSession();
                            session.setAttribute("username", username);

                            // Redirect to home page
                            response.sendRedirect("home");

                        } else {
                            // Login failed
                            System.out.println("Login failed: Invalid credentials");
                            response.sendRedirect("login.html?error=invalid");
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Driver class not found
            response.sendRedirect("login.html?error=driver_not_found");
        } catch (SQLException e) {
            e.printStackTrace(); // SQL error
            response.sendRedirect("login.html?error=sql_error");
        }
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
