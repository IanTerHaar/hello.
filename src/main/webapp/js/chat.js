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
