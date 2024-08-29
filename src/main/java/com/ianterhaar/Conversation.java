package com.ianterhaar;

public class Conversation {
    private int id;
    private String username;

    public Conversation(int id, String username) {
        this.id = id;
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
