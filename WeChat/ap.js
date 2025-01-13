let socket;
let username;

// Event listener for the Join button
document.getElementById('joinBtn').addEventListener('click', handleJoin);

// Event listener for the Send Message button
document.getElementById('sendMessageBtn').addEventListener('click', handleSendMessage);

// Update connection status on the page
function updateConnectionStatus(status) {
  const statusElem = document.getElementById('status');
  if (statusElem) {
    statusElem.textContent = status;
  }
}

// Handle user joining the chat
function handleJoin() {
  username = document.getElementById('username').value.trim();
  if (!username) {
    alert("Please enter a valid username.");
    return;
  }

  connectToServer();
  toggleInputFields(true);
  updateConnectionStatus("Connecting...");
}

// Handle sending messages
function handleSendMessage() {
  const message = document.getElementById('message').value.trim();
  if (!message) {
    alert("Please enter a message.");
    return;
  }

  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(`${username}: ${message}`);
    document.getElementById('message').value = ''; // Clear input field
  } else {
    alert("Cannot send message. Ensure the connection is established.");
  }
}

// Toggle input fields and buttons
function toggleInputFields(disable) {
  document.getElementById('username').disabled = disable;
  document.getElementById('joinBtn').disabled = disable;
}

// Connect to the WebSocket server
function connectToServer() {
  socket = new WebSocket('ws://localhost:8080');

  socket.onopen = () => {
    console.log("Connected to the server.");
    updateConnectionStatus("Connected to the server.");
    socket.send(username); // Send username to the server
  };

  socket.onmessage = (event) => {
    displayMessage(event.data);
  };

  socket.onerror = (error) => {
    console.error("WebSocket error:", error);
    updateConnectionStatus("Error: Unable to connect.");
    alert("Connection error. Please try again later.");
    toggleInputFields(false);
  };

  socket.onclose = () => {
    console.log("Connection closed.");
    updateConnectionStatus("Disconnected from the server.");
    toggleInputFields(false); // Re-enable inputs for rejoining
  };
}

// Display a new message in the chat box
function displayMessage(message) {
  const chatBox = document.getElementById('chatBox');
  const newMessage = document.createElement('div');
  newMessage.textContent = message;
  chatBox.appendChild(newMessage);
  chatBox.scrollTop = chatBox.scrollHeight; // Scroll to the bottom
}
