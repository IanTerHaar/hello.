// Get the username from the JSP-provided global variable
const username = encodeURIComponent(chatUsername);
let currentConversationId = null; // Declare the variable globally

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

    if (messageText.trim() && currentConversationId !== null) { // Check if it's set
        let messageData = {
            conversationId: currentConversationId, // Use it here
            sender: decodeURIComponent(username),
            message: messageText
        };

        socket.send(JSON.stringify(messageData));
        messageInput.value = '';
    } else {
        console.warn('No conversation selected or message is empty.');
    }
}


// Function to select a conversation and load its messages
function selectConversation(conversationId, conversationUsername) {
    currentConversationId = conversationId; // Set the global variable
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
