import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;
    private final Set<PrintWriter> clientWriters = new HashSet<>();
    private final Map<String, PrintWriter> userWriters = new HashMap<>();
    private final Map<String, String> userCredentials = new HashMap<>();

    public static void main(String[] args) {
        new ChatServer().start();
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started...");
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            System.err.println("Server exception: " + e.getMessage());
        }
    }

    private class ClientHandler extends Thread {
        private final Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                initializeStreams();
                authenticateUser();
                handleClientMessages();
            } catch (IOException e) {
                System.err.println("Client handler exception: " + e.getMessage());
            } finally {
                cleanup();
            }
        }

        private void initializeStreams() throws IOException {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        }

        private void authenticateUser() throws IOException {
            while (true) {
                out.println("Enter username:");
                username = in.readLine();
                if (username == null || userWriters.containsKey(username)) {
                    continue;
                }
                out.println("Enter password:");
                String password = in.readLine();
                if (userCredentials.containsKey(username) && !userCredentials.get(username).equals(password)) {
                    out.println("Incorrect password. Try again.");
                } else {
                    userWriters.put(username, out);
                    synchronized (clientWriters) {
                        clientWriters.add(out);
                    }
                    out.println("Welcome " + username + "!");
                    break;
                }
            }
        }

        private void handleClientMessages() throws IOException {
            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/private")) {
                    handlePrivateMessage(message);
                } else {
                    broadcastMessage(username + ": " + message);
                }
            }
        }

        private void handlePrivateMessage(String message) {
            String[] parts = message.split(" ", 3);
            if (parts.length < 3) return;
            String recipient = parts[1];
            String privateMessage = parts[2];
            PrintWriter recipientWriter = userWriters.get(recipient);
            if (recipientWriter != null) {
                recipientWriter.println("(Private) " + username + ": " + privateMessage);
            }
        }

        private void broadcastMessage(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }

        private void cleanup() {
            try {
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
            synchronized (clientWriters) {
                userWriters.remove(username);
                clientWriters.remove(out);
            }
        }
    }
}
