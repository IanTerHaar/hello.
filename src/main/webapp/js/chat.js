// // Fetch the username from the session and encode it
// const username = encodeURIComponent('<%= session.getAttribute("username") %>');
// let socket = new WebSocket(`ws://${window.location.host}/chat/${username}`);
//
//
//
//
// // Handle incoming WebSocket messages
// socket.onmessage = function(event) {
//     let messageData = JSON.parse(event.data);
//     let chatWindow = document.getElementById("chatWindow");
//
//     // Create a new div element to display the incoming message
//     let message = document.createElement("div");
//     message.className = messageData.sender === decodeURIComponent(username) ? 'message sent' : 'message received';
//     message.innerHTML = `<p>${messageData.message}</p><span class="sender">${messageData.sender}</span>`;
//
//     // Append the new message to the chat window
//     chatWindow.appendChild(message);
// };
//
// // Handle WebSocket connection open event
// socket.onopen = function(event) {
//     console.log('WebSocket connection opened for user: ' + decodeURIComponent(username));
// };
//
// // Handle WebSocket connection errors
// socket.onerror = function(event) {
//     console.error('WebSocket error:', event);
// };
//
// // Handle WebSocket connection close event
// socket.onclose = function(event) {
//     console.log('WebSocket connection closed');
// };
//
// // Function to send a message through the WebSocket
// function sendMessage() {
//     const messageInput = document.getElementById('messageInput');
//     const messageText = messageInput.value;
//
//     if (messageText.trim()) {
//         let messageData = {
//             sender: decodeURIComponent(username),
//             message: messageText
//         };
//
//         // Send the message as a JSON string through the WebSocket
//         socket.send(JSON.stringify(messageData));
//
//         // Clear the input field after sending the message
//         messageInput.value = '';
//     }
// }
