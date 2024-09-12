package client;

import java.io.*;
import java.net.*;

public class ChatClient {
    private String username;
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public ChatClient(String serverAddress, int serverPort) {
        try {
            this.socket = new Socket(serverAddress, serverPort);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

}
