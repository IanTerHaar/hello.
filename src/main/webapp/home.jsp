<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>hello.</title>
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

                <c:set var="conversationList" value="${conversations}" />

                <c:if test="${not empty conversations}">
                    <c:forEach var="conversation" items="${conversations}">
                        <div class="conversation-item">${conversation}</div>
                    </c:forEach>
                </c:if>
                <c:if test="${empty conversations}">
                    <p>No conversations found.</p>
                </c:if>

<%--            <c:forEach var="conversation" items="${['Alice', 'Bob', 'Charlie']}">--%>
<%--                <div class="conversation-item">${conversation}</div>--%>
<%--            </c:forEach>--%>

            </div>
        </aside>

        <section class="main-chat">
            <div class="chat-header">
                <h3 id="activeChatName">Active Chat</h3>
            </div>

            <div class="chat-window" id="chatWindow">
                <!-- Chat messages will be dynamically inserted here -->
            </div>

            <div class="message-input">
                <input type="text" id="messageInput" placeholder="Type your message">
                <button onclick="sendMessage()">SEND</button>
            </div>
        </section>
    </div>
</div>

<script>
    let socket = new WebSocket("ws://localhost:8080/chat-app/chat");

    socket.onmessage = function(event) {
        let chatWindow = document.getElementById("chatWindow");
        let message = document.createElement("div");
        message.className = 'message received';
        message.innerHTML = `<p>${event.data}</p><span class="sender">Other</span>`;
        chatWindow.appendChild(message);
    };

    function sendMessage() {
        const messageInput = document.getElementById('messageInput');
        const messageText = messageInput.value;
        const username = '<%= session.getAttribute("userName") %>';

        if (messageText.trim()) {
            const chatWindow = document.getElementById('chatWindow');
            const newMessage = document.createElement('div');
            newMessage.className = 'message sent';
            newMessage.innerHTML = `<p>${messageText}</p><span class="sender">${username}</span>`;
            chatWindow.appendChild(newMessage);
            messageInput.value = ''; // Clear input field

            socket.send(messageText);
        }
    }
</script>
</body>
</html>
