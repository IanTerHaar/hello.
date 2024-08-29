package com.ianterhaar;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.PathParam;
import java.util.logging.Logger;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;

@ServerEndpoint("/chat/{username}")
public class ChatEndpoint {

    private static final Logger logger = Logger.getLogger(ChatEndpoint.class.getName());

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
    public void onMessage(String message, Session session) {
        logger.info("Received message: " + message);
        try {
            // Broadcast the message to all connected sessions
            for (Session s : session.getOpenSessions()) {
                if (s.isOpen()) {
                    s.getBasicRemote().sendText(message);
                }
            }
        } catch (Exception e) {
            logger.severe("Error sending message: " + e.getMessage());
        }
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
