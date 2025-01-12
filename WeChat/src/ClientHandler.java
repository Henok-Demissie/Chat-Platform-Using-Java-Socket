import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable {
    // Thread-safe list to keep track of all active client handlers
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
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

            // Read the username of the client and store it
            this.clientUsername = bufferedReader.readLine();
            synchronized (clientHandlers) {
                clientHandlers.add(this);  // Add this handler to the list of client handlers
            }

            // Notify other clients that the user has entered the chat
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
        synchronized (clientHandlers) {
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
    }

    // Method to remove the current client handler from the list and notify others of the di
