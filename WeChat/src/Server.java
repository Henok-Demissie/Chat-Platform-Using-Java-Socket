import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private ServerSocket serverSocket;

    // Constructor that initializes the ServerSocket
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    // Method to start the server and handle client connections
    public void startServer() {
        try {
            // Server is running until it's closed
            while (!serverSocket.isClosed()) {
                System.out.println("Waiting for clients to connect...");

                // Accept incoming client connections
                Socket socket = serverSocket.accept();
                System.out.println("A new client has connected!");

                // Create a new ClientHandler to manage the client
                ClientHandler clientHandler = new ClientHandler(socket);

                // Start a new thread to handle communication with the client
                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        } catch (IOException e) {
            System.out.println("Error while accepting client connections: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Ensure the server socket is closed properly even in case of an error
            closeServerSocket();
        }
    }

    // Method to close the server socket gracefully
    public void closeServerSocket() {
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                System.out.println("Server socket closed.");
            }
        } catch (IOException e) {
            System.out.println("Error while closing the server socket: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Main method to run the server and accept client connections
    public static void main(String[] args) {
        try {
            // Create a ServerSocket listening on port 1357
            ServerSocket serverSocket = new ServerSocket(1357);
            Server server = new Server(serverSocket);

            // Start the server
            server.startServer();
        } catch (IOException e) {
            System.out.println("Error while starting the server: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
