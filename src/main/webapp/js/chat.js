// Get the username from the JSP-provided global variable
const username = encodeURIComponent(chatUsername);

// Establish WebSocket connection
let socket = new WebSocket(`ws://${window.location.host}/chat/${username}`);

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

// Function to select a conversation and load its messages
function selectConversation(conversationId, conversationUsername) {
    // Update the active chat name in the UI
    document.getElementById('activeChatName').innerText = conversationUsername;

    // Clear the chat window
    let chatWindow = document.getElementById("chatWindow");
    chatWindow.innerHTML = '';

    // Make an AJAX request to load the conversation messages
    fetch(`chat?conversationId=${conversationId}`)
        .then(response => response.json())
        .then(messages => {
            messages.forEach(message => {
                let messageElement = document.createElement("div");
                messageElement.className = message.sender === chatUsername ? 'message sent' : 'message received';
                messageElement.innerHTML = `<p>${message.message}</p><span class="sender">${message.sender}</span>`;
                chatWindow.appendChild(messageElement);
            });
        });
}