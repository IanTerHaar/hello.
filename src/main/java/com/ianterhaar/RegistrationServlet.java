package com.ianterhaar;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/RegistrationServlet")
public class RegistrationServlet extends HttpServlet {

    private static final String URL = "jdbc:postgresql://localhost:5432/postgres";
    private static final String USER = "postgres";
    private static final String PASSWORD = "03Maelks03";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String surname = request.getParameter("surname");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        // Hash the password
        String hashedPassword = hashPassword(password);

        try {
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Check if the username already exists
            if (isUsernameTaken(connection, username)) {
                // Username is already taken, redirect to registration page with an error
                response.sendRedirect("register.html?error=username_taken");
            } else {
                // Insert user data into the database
                String query = "INSERT INTO users (name, surname, username, password) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, name);
                    statement.setString(2, surname);
                    statement.setString(3, username);
                    statement.setString(4, hashedPassword);

                    int rowsInserted = statement.executeUpdate();
                    if (rowsInserted > 0) {
                        // Registration successful
                        response.sendRedirect("login.html?registered=1");
                    } else {
                        // Registration failed
                        response.sendRedirect("register.html?error=registration_failed");
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace(); // Driver class not found
            response.sendRedirect("register.html?error=driver_not_found");
        } catch (SQLException e) {
            e.printStackTrace(); // SQL error
            response.sendRedirect("register.html?error=sql_error");
        }
    }

    private boolean isUsernameTaken(Connection connection, String username) throws SQLException {
        String query = "SELECT 1 FROM users WHERE username = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next(); // Returns true if the username exists
            }
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
