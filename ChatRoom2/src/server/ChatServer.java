package server;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static final int PORT = 12345;// 连接端口
    private static Set<ClientHandler> clientHandlers;// 客户机处理程序集

    public ChatServer() {
        clientHandlers = new HashSet<>();
        buildConnection();
    }

    // 尝试与新的客户端连接
    private void buildConnection() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();// 等待客户端连接
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();// 实例新的客户端处理线程
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
