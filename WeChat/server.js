const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

// Utility function to log with timestamp
function log(message) {
  console.log(`[${new Date().toISOString()}] ${message}`);
}

wss.on('connection', (ws) => {
  log("A new client connected!");

  // Handle incoming messages
  ws.on('message', (message) => {
    if (typeof message === 'string') {
      log(`Received: ${message}`);
      // Broadcast the message to all clients except the sender
      wss.clients.forEach(client => {
        if (client !== ws && client.readyState === WebSocket.OPEN) {
          client.send(message);
        }
      });
    } else {
      log("Received non-string message, ignoring.");
    }
  });

  // Handle client disconnect
  ws.on('close', () => {
    log("A client disconnected");
  });

  // Handle errors
  ws.on('error', (err) => {
    log(`Error: ${err.message}`);
  });
});

// Handle server errors
wss.on('error', (err) => {
  log(`Server error: ${err.message}`);
});

// Graceful shutdown on termination signals
function gracefulShutdown() {
  log("Shutting down WebSocket server...");
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send("Server is shutting down.");
      client.close();
    }
  });
  wss.close(() => {
    log("WebSocket server closed.");
    process.exit(0);
  });
}

process.on('SIGINT', gracefulShutdown);  // Handle Ctrl+C termination
process.on('SIGTERM', gracefulShutdown); // Handle termination signal

log("WebSocket server is running on ws://localhost:8080");
