let socket;
let username;

document.getElementById('joinBtn').addEventListener('click', () => {
  username = document.getElementById('username').value.trim();
  if (username) {
    connectToServer();
    document.getElementById('username').disabled = true;
    document.getElementById('joinBtn').disabled = true;
  } else {
    alert("Please enter a valid username.");
  }
});

document.getElementById('sendMessageBtn').addEventListener('click', () => {
  const message = document.getElementById('message').value.trim();
  if (message && socket) {
    socket.send(username + ": " + message);
    document.getElementById('message').value = ''; // clear input field
  }
});

function connectToServer() {
  // Create a WebSocket connection to the server
  socket = new WebSocket('ws://localhost:8080'); // assuming the WebSocket server is running on port 8080

  socket.onopen = () => {
    console.log("Connected to the server");
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
  };

  socket.onclose = () => {
    console.log("Connection closed");
  };
}
