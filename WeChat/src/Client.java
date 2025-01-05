import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;

    // Constructor: Initializes the client with the socket and username
    public Client(Socket socket, String username) {
        try {
            // Assign socket, bufferedReader, and bufferedWriter objects
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;

            // Send the username to the server on initial connection
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error initializing client connection.");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to send messages from the client to the server
    public void sendMessage() {
        try {
            // Create a scanner object to read user input from the console
            Scanner scanner = new Scanner(System.in);

            while (socket.isConnected()) {
                // Read user input and send the message to the server
                String messageToSend = scanner.nextLine();
                bufferedWriter.write(username + ": " + messageToSend);
                bufferedWriter.newLine();
                bufferedWriter.flush(); // Flush the output to ensure the message is sent
            }
        } catch (IOException e) {
            System.out.println("Error sending message.");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to listen for incoming messages from the server (in a separate thread)
    public void listenForMessage() {
        new Thread(() -> {
            String msgFromGroupChat;

            while (socket.isConnected()) {
                try {
                    // Read the message from the server and display it
                    msgFromGroupChat = bufferedReader.readLine();
                    if (msgFromGroupChat != null) {
                        System.out.println(msgFromGroupChat);
                    }
                } catch (IOException e) {
                    System.out.println("Error reading message.");
                    closeEverything(socket, bufferedReader, bufferedWriter);
                    break;
                }
            }
        }).start(); // Start the thread for listening to messages
    }

    // Method to close all resources safely (socket, bufferedReader, bufferedWriter)
    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); // Close the input stream
            }
            if (bufferedWriter != null) {
                bufferedWriter.close(); // Close the output stream
            }
            if (socket != null) {
                socket.close(); // Close the socket connection
            }
        } catch (IOException e) {
            System.out.println("Error closing resources.");
        }
    }

    // Main method to run the client application
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ask the user to enter their username for the group chat
        System.out.println("Enter your username for the group chat: ");
        String username = scanner.nextLine();

        try {
            // Create a socket connection to the server running on localhost:1357
            Socket socket = new Socket("localhost", 1357);
            Client client = new Client(socket, username); // Initialize the client object

            // Start listening for messages from the server
            client.listenForMessage();

            // Allow the user to send messages to the server
            client.sendMessage();
        } catch (IOException e) {
            System.out.println("Error: Could not connect to the server.");
            e.printStackTrace();
        }
    }
}
