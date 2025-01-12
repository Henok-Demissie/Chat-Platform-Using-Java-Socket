import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String username;
    private Scanner scanner;

    // Constructor: Initializes the client with the socket and username
    public Client(Socket socket, String username) {
        try {
            // Assign socket, bufferedReader, and bufferedWriter objects
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.username = username;
            this.scanner = new Scanner(System.in); // Initializing the scanner once here

            // Send the username to the server on initial connection
            sendUsernameToServer();
        } catch (IOException e) {
            System.out.println("Error initializing client connection.");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to send the username to the server
    private void sendUsernameToServer() {
        try {
            bufferedWriter.write(username);
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            System.out.println("Error sending username to server.");
        }
    }

    // Method to send messages from the client to the server
    public void sendMessage() {
        try {
            while (socket.isConnected()) {
                String messageToSend = scanner.nextLine();
                if (!messageToSend.trim().isEmpty()) {
                    bufferedWriter.write(username + ": " + messageToSend);
                    bufferedWriter.newLine();
                    bufferedWriter.flush(); // Ensure the message is sent
                }
            }
        } catch (IOException e) {
            System.out.println("Error sending message.");
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    // Method to listen for incoming messages from the server (in a separate thread)
    public void listenForMessages() {
        new Thread(() -> {
            String msgFromGroupChat;
            try {
                while (socket.isConnected()) {
                    msgFromGroupChat = bufferedReader.readLine();
                    if (msgFr
