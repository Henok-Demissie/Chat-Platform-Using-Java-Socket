let socket;
let username;

document.getElementById('joinBtn').addEventListener('click', () => {
  username = document.getElementById('username').value.trim();
  if (username) {
    connectToServer();
    document.getElementById('username').disabled = true;
    document.getElementById('joinBtn').disabled = true;
    updateConnectionStatus("Connecting...");
  } else {
    alert("Please enter a valid username.");
  }
});

document.getElementById('sendMessageBtn').addEventListener('click', () => {
  const message = document.getElementById('message').value.trim();
  if (message && socket && socket.readyState === WebSocket.OPEN) {
    socket.send(`${username}: ${message}`);
    document.getElementById('message').value = ''; // clear input field
  } else {
    alert("Please enter a message or wait for the connection to be established.");
  }
});

// Update connection status on the page
function updateConnectionStatus(status) {
  const statusElem = document.getElementById('status');
  if (statusElem) {
    statusElem.textContent = status;
  }
}

function connectToServer() {
  // Create a WebSocket connection to the server
  socket = new WebSocket('ws://localhost:8080'); // assuming the WebSocket server is running on port 8080

  socket.onopen = () => {
    console.log("Connected to the server");
    updateConnectionStatus("Connected to the server.");
    // Send the username to the server
    socket.send(username);
  };

  socket.onmessage = (event) => {
    const chatBox = document.getElementById('chatBox');
    const newMessage = document.createElement('div');
    newMessage.textContent = event.data;
    chatBox.appendChild(newMessage);
    chatBox.scrollTop = chatBox.scrollHeight; // scroll to the bottom
  };

  socket.onerror = (error) => {
    console.error("WebSocket error: ", error);
    updateConnectionStatus("Error: Unable to connect.");
    alert("Connection error. Please try again later.");
  };

  socket.onclose = () => {
    console.log("Connection closed");
    updateConnectionStatus("Disconnected from the server.");
    // Re-enable the join button and username input for rejoining
    document.getElementById('username').disabled = false;
    document.getElementById('joinBtn').disabled = false;
  };
}
