const WebSocket = require('ws');
const wss = new WebSocket.Server({ port: 8080 });

// Utility function to log messages with a timestamp
const log = (message) => {
  console.log(`[${new Date().toISOString()}] ${message}`);
};

// Broadcast a message to all connected clients except the sender
const broadcastMessage = (message, sender) => {
  wss.clients.forEach(client => {
    if (client !== sender && client.readyState === WebSocket.OPEN) {
      client.send(message);
    }
  });
};

// Handle new client connections
wss.on('connection', (ws) => {
  log("A new client connected.");

  // Handle incoming messages from a client
  ws.on('message', (message) => {
    if (typeof message === 'string') {
      log(`Received: ${message}`);
      broadcastMessage(message, ws);
    } else {
      log("Received non-string message, ignoring.");
    }
  });

  // Handle client disconnection
  ws.on('close', () => {
    log("A client disconnected.");
  });

  // Handle client-specific errors
  ws.on('error', (err) => {
    log(`Client error: ${err.message}`);
  });
});

// Handle server-level errors
wss.on('error', (err) => {
  log(`Server error: ${err.message}`);
});

// Graceful shutdown to handle termination signals
const gracefulShutdown = () => {
  log("Initiating graceful shutdown...");

  // Notify and disconnect all clients
  wss.clients.forEach(client => {
    if (client.readyState === WebSocket.OPEN) {
      client.send("Server is shutting down.");
      client.close();
    }
  });

  // Close the WebSocket server
  wss.close(() => {
    log("WebSocket server shut down successfully.");
    process.exit(0);
  });
};

// Listen for termination signals
process.on('SIGINT', gracefulShutdown);  // Handle Ctrl+C
process.on('SIGTERM', gracefulShutdown); // Handle termination signal

// Server ready log
log("WebSocket server is running on ws://localhost:8080");
