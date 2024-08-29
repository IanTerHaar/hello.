<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            <!-- Sidebar content -->
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

<script type="text/javascript">
    // This code is evaluated by the server before being sent to the client
    const username = encodeURIComponent('<%= session.getAttribute("username") %>');
    let socket = new WebSocket(`ws://${window.location.host}/chat/${username}`);

    // Handle incoming WebSocket messages
    socket.onmessage = function(event) {
        let messageData = JSON.parse(event.data);
        let chatWindow = document.getElementById("chatWindow");

        let message = document.createElement("div");
        message.className = messageData.sender === decodeURIComponent(username) ? 'message sent' : 'message received';
        message.innerHTML = `<p>${messageData.message}</p><span class="sender">${messageData.sender}</span>`;

        chatWindow.appendChild(message);
    };


    socket.onopen = function(event) {
        console.log('WebSocket connection opened for user: ' + decodeURIComponent(username));
    };

    socket.onerror = function(event) {
        console.error('WebSocket error:', event);
    };

    socket.onclose = function(event) {
        console.log('WebSocket connection closed');
    };

    function sendMessage() {
        const messageInput = document.getElementById('messageInput');
        const messageText = messageInput.value;

        if (messageText.trim()) {
            let messageData = {
                sender: decodeURIComponent(username),
                message: messageText
            };

            socket.send(JSON.stringify(messageData));
            messageInput.value = '';
        }
    }
</script>

</body>
</html>
