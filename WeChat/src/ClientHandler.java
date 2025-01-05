import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientHandler implements Runnable {
    // List to keep track of all active client handlers
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    // Constructor to initialize the client handler, establish streams and handle username
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            // Initialize the input and output streams
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Read the username of the client
            this.clientUsername = bufferedReader.readLine();
            clientHandlers.add(this);  // Add this handler to the list of client handlers

            // Notify others that the user has entered the chat
            broadcastMessage("SERVER: " + clientUsername + " has entered the chat!");
        } catch (IOException e) {
            System.out.println("Error while establishing connection with client: " + e.getMessage());
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;

        // Continuously listen for messages from the client as long as the socket is connected
        while (socket.isConnected()) {
            try {
                // Read the incoming message from the client
                messageFromClient = bufferedReader.readLine();
                if (messageFromClient != null) {
                    broadcastMessage(messageFromClient);  // Broadcast the message to other clients
                }
            } catch (IOException e) {
                System.out.println("Error while reading message from client " + clientUsername + ": " + e.getMessage());
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    // Method to broadcast a message to all clients except the sender
    public void broadcastMessage(String messageToSend) {
        // Loop through all active client handlers and send the message
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                // Send message to all clients except the sender
                if (!clientHandler.clientUsername.equals(clientUsername)) {
                    clientHandler.bufferedWriter.write(messageToSend);
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                }
            } catch (IOException e) {
                System.out.println("Error while broadcasting message: " + e.getMessage());
                // Ensure proper cleanup for clients who encountered an issue
                closeEverything(clientHandler.socket, clientHandler.bufferedReader, clientHandler.bufferedWriter);
            }
        }
    }

    // Method to remove the current client handler from the list and notify others of the disconnection
    public void removeClientHandler() {
        clientHandlers.remove(this);  // Remove this client handler from the active list
        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");  // Notify others
    }

    // Method to close all resources properly
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        // Call method to remove this client handler from the list
        removeClientHandler();
        try {
            // Close the input, output streams, and the socket connection
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            System.out.println("Error while closing resources: " + e.getMessage());
        }
    }
}
