<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat Application</title>
    <link rel="stylesheet" href="assets/styles/home.css">
    <link rel="icon" href="assets/images/favIcon_raw.png" type="image/png">
</head>
<body>
<div class="chat-container">
    <header>
        <h2 id="greeting">Hello, <%= session.getAttribute("username") %></h2>
    </header>

    <div class="chat-content">
        <aside class="sidebar">
            <div class="conversation-list">
                <c:if test="${not empty conversations}">
                    <c:forEach var="conversation" items="${conversations}">
                        <!-- Assign data attributes to pass conversation details -->
                        <div class="conversation-item" data-username="${conversation}" onclick="selectConversation(this)">
                                ${conversation}
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${empty conversations}">
                    <p>No conversations found.</p>
                </c:if>
            </div>
        </aside>

        <section class="main-chat">
            <div class="chat-header">
                <h3 id="activeChatName">Active Chat</h3>
            </div>

            <div class="chat-window" id="chatWindow">
                <!-- Chat messages will be dynamically inserted here -->
                <c:if test="${not empty messages}">
                    <c:forEach var="message" items="${messages}">
                        <div class="message">
                            <span class="username">${message.username}:</span>
                            <span class="text">${message.message}</span>
                            <span class="timestamp">${message.timestamp}</span>
                        </div>
                    </c:forEach>
                </c:if>
                <c:if test="${empty messages}">
                    <p>No messages found. Start a conversation.</p>
                </c:if>
            </div>

            <div class="message-input">
                <input type="text" id="messageInput" placeholder="Type your message">
                <button onclick="sendMessage()">SEND</button>
            </div>
        </section>
    </div>
</div>

    <script src="js/chat.js"></script>

</body>
</html>
