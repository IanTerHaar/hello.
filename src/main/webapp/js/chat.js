// Establish WebSocket connection
let socket = new WebSocket("ws://localhost:8080/chat-app/chat");

// Handle incoming messages from WebSocket
socket.onmessage = function(event) {
    let chatWindow = document.getElementById("chatWindow");
    let message = document.createElement("div");
    message.className = 'message received';
    message.innerHTML = `<p>${event.data}</p><span class="sender">Other</span>`;
    chatWindow.appendChild(message);
};

// Function to send messages via WebSocket
function sendMessage() {
    const messageInput = document.getElementById('messageInput');
    const messageText = messageInput.value;
    const username = '<%= session.getAttribute("username") %>';

    if (messageText.trim()) {
        const chatWindow = document.getElementById('chatWindow');
        const newMessage = document.createElement('div');
        newMessage.className = 'message sent';
        newMessage.innerHTML = `<p>${messageText}</p><span class="sender">${username}</span>`;
        chatWindow.appendChild(newMessage);
        messageInput.value = ''; // Clear input field

        // Send message to WebSocket server
        socket.send(messageText);
    }
}

// Function to select a conversation and load messages
function selectConversation(conversationElement) {
    const username = conversationElement.getAttribute('data-username');
    if (username) {
        window.location.href = `chat?otherUser=${encodeURIComponent(username)}`;
    } else {
        console.error("Username attribute is missing.");
    }
}