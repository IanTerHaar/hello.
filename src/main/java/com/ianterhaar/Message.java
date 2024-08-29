package com.ianterhaar;  // Ensure this matches your project structure

import java.sql.Timestamp;

public class Message {
    private String username;
    private String message;
    private Timestamp timestamp;

    // Constructor
    public Message(String username, String message, Timestamp timestamp) {
        this.username = username;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters
    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    // Setters (optional, if needed)
    public void setUsername(String username) {
        this.username = username;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "username='" + username + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
