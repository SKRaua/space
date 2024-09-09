package server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private Set<ClientHandler> clientHandlers;

    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 用户登录认证
            out.println("Enter your username:");
            this.username = in.readLine();
            System.out.println(username + " has joined the chat.");
            broadcastMessage("System: " + username + " has joined the chat!");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/private")) {
                    sendPrivateMessage(message);
                } else {
                    broadcastMessage(username + ": " + message);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                clientHandlers.remove(this);
                broadcastMessage("System: " + username + " has left the chat.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastMessage(String message) {
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.out.println(message);
        }
    }

    private void sendPrivateMessage(String message) {
        // 格式: /private <username> <message>
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String targetUsername = parts[1];
            String privateMessage = parts[2];
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.username.equals(targetUsername)) {
                    clientHandler.out.println("[Private] " + username + ": " + privateMessage);
                    this.out.println("[Private] to " + targetUsername + ": " + privateMessage);
                    return;
                }
            }
            this.out.println("User " + targetUsername + " not found.");
        }
    }
}
