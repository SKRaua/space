package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 客户端处理器
 */
public class ClientHandler implements Runnable {
    private Socket socket;
    // private PrintWriter out;
    private DataOutputStream out;
    // private BufferedReader in;
    private DataInputStream in;// = new DataInputStream(socket.getInputStream());
    private String username;
    private Set<ClientHandler> clientHandlers;

    /**
     * 实例客户端处理器
     * 
     * @param socket         通信套接字
     * @param clientHandlers 在线客户端集合
     */
    public ClientHandler(Socket socket, Set<ClientHandler> clientHandlers) {
        this.socket = socket;
        this.clientHandlers = clientHandlers;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            // in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new DataOutputStream(socket.getOutputStream());
            // out = new PrintWriter(socket.getOutputStream(), true);
            out.writeUTF("[/login <用户名>] 来登陆\n[/private <用户名> <信息>] 发起私聊\n[/clientList] 获取用户列表");
            out.flush();
            // out.println("[/login <用户名>] 来登陆");
            // out.println("[/private <用户名> <信息>] 发起私聊");
            // out.println("[/clientList] 获取用户列表");

            // String message;

            while (true) {
                String messageType = in.readUTF();
                if (messageType.startsWith("/login")) {// 判断登陆指令
                    loginHandler(messageType);
                } else if (username != null) {
                    if (messageType.startsWith("FILE_TRANSFER:")) {
                        fileHandler(messageType);
                    } else if (messageType.startsWith("/")) {// 判断指令信息
                        if (messageType.startsWith("/private")) {
                            sendPrivateMessage(messageType);
                        } else if (messageType.startsWith("/clientList")) {
                            clientListMessage();
                        }
                    } else {// 默认：发送信息
                        broadcastMessage(username + ": " + messageType);
                    }
                } else {
                    out.writeUTF("请先登陆！\n[/login <用户名>] 来登陆");
                    out.flush();
                    // out.println("请先登陆！");
                    // out.println("[/login <用户名>] 来登陆");
                }
            }

            // while ((message = in.readLine()) != null) {
            // if (message.startsWith("/login")) {// 判断登陆指令
            // loginHandler(message);
            // } else if (username != null) {
            // if (message.startsWith("/")) {// 判断指令信息
            // if (message.startsWith("/private")) {
            // sendPrivateMessage(message);
            // } else if (message.startsWith("/clientList")) {
            // clientListMessage();
            // }
            // } else {// 默认：发送信息
            // broadcastMessage(username + ": " + message);
            // }
            // } else {
            // out.println("请先登陆！");
            // out.println("[/login <用户名>] 来登陆");
            // }
            // }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
                clientHandlers.remove(this);
                broadcastMessage("[System]: " + username + " 离开聊天。");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loginHandler(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String username = parts[1];
            // String password = parts[2];
            // 用户登录认证
            boolean userExists = false;
            // 检查用户名是否已经存在
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.username != null && clientHandler.username.equals(username)) {
                    out.writeUTF(username + " 用户名已存在，请重试。\n[/login <用户名>] 来登陆");
                    out.flush();
                    // out.println(username + " 用户名已存在，请重试。");
                    // out.println("[/login <用户名>] 来登陆");
                    userExists = true;
                    break;
                }
            }
            // 如果用户名不存在，允许登录
            if (!userExists) {
                this.username = username;
                out.writeUTF("/login" + username);
                out.writeUTF(username + "欢迎 " + username + "！");
                out.flush();
                // out.println("欢迎 " + username + "！");
                broadcastMessage("[System]: " + username + " 加入聊天");
            }
        }
    }

    private void broadcastMessage(String message) throws IOException {
        for (ClientHandler clientHandler : clientHandlers) {
            // clientHandler.out.println(message);
            clientHandler.out.writeUTF(message);
            clientHandler.out.flush();
        }
    }

    private void sendPrivateMessage(String message) throws IOException {
        // 格式: /private <username> <message>
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String targetUsername = parts[1];
            String privateMessage = parts[2];
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.username.equals(targetUsername)) {
                    // clientHandler.out.println("[Private] " + username + ": " + privateMessage);
                    // this.out.println("[Private] to " + targetUsername + ": " + privateMessage);
                    clientHandler.out.writeUTF("[Private] " + username + ": " + privateMessage);
                    this.out.writeUTF("[Private] to " + targetUsername + ": " + privateMessage);
                    clientHandler.out.flush();
                    this.out.flush();
                    return;
                }
            }
            this.out.writeUTF("未找到用户 " + targetUsername);
        }
    }

    private void clientListMessage() throws IOException {
        // this.out.println(clientHandlers.size() + "在线用户：");
        this.out.writeUTF(clientHandlers.size() + "在线用户：");
        for (ClientHandler clientHandler : clientHandlers) {
            // this.out.print("[" + clientHandler.username + "] ");
            this.out.writeUTF("[" + clientHandler.username + "] ");
        }
        this.out.flush();
    }

    private void fileHandler(String message) throws IOException {
        String fileName = message.substring(14);
        int fileLength = in.readInt();
        byte[] fileBytes = new byte[fileLength];
        in.readFully(fileBytes);

        try (FileOutputStream fos = new FileOutputStream("received_" + fileName)) {
            fos.write(fileBytes);
            System.out.println("文件接收完成: " + fileName);
            broadcastMessage("[System]: " + username + " 发送文件：" + fileName);
        }
    }
}
