package com.ianterhaar;

import java.sql.Timestamp;

public class Message {
    private int conversationId;
    private String sender; // Changed from `username` to `sender`
    private String message;
    private Timestamp timestamp;

    // Constructor with sender
    public Message(int conversationId, String sender, String message, Timestamp timestamp) {
        this.conversationId = conversationId;
        this.sender = sender; // Changed from `username` to `sender`
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public int getConversationId() {
        return conversationId;
    }

    public void setConversationId(int conversationId) {
        this.conversationId = conversationId;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "Message{" +
                "conversationId=" + conversationId +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
