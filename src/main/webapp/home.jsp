<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isELIgnored="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Chat Application</title>
    <link rel="stylesheet" href="assets/styles/home.css">
    <link rel="icon" href="assets/images/favIcon_raw.png" type="image/png">

    <!-- Pass the username from JSP to JavaScript -->
    <script type="text/javascript">
        var chatUsername = '<%= session.getAttribute("username") %>';
    </script>
    <!-- Include the external JavaScript file -->
    <script src="js/chat.js"></script>
</head>
<body>
<div class="chat-container">
    <header>
        <h2 id="greeting">Hello, <%= session.getAttribute("username") %></h2>
    </header>

    <div class="chat-content">
        <aside class="sidebar">
            <div class="conversation-list">
                <!-- Loop through the list of conversations and display each one -->
                <c:if test="${not empty conversations}">
                    <c:forEach var="conversation" items="${conversations}">
                        <div class="conversation-item">
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
            </div>

            <div class="message-input">
                <input type="text" id="messageInput" placeholder="Type your message">
                <button onclick="sendMessage()">SEND</button>
            </div>
        </section>
    </div>
</div>

</body>
</html>
