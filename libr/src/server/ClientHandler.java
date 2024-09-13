package server;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * 客户端处理器
 */
public class ClientHandler implements Runnable {
    private Socket socket;// 连接套接字
    private PrintWriter out;// 向客户端输出
    private BufferedReader in;// 从客户端输入
    private String username;// 用户名
    private Set<ClientHandler> clientHandlers;// 客户端处理线程集合

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

    /**
     * 读取客户端传入信息的线程任务
     */
    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            out.println("[/register <用户名> <密码> <确认密码>] 来注册");
            out.println("[/login <用户名> <密码>] 来登陆");
            out.println("[/private <用户名> <消息>] 发起私聊");
            out.println("[/clientList] 获取用户列表");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/login")) {// 判断登陆指令
                    loginHandler(message);
                } else if (message.startsWith("/register")) {// 判断注册指令
                    registerHandler(message);
                } else if (username != null) {// 用户登陆
                    if (message.startsWith("/")) {// 判断指令信息
                        if (message.startsWith("/private")) {// 私聊
                            sendPrivateMessage(message);
                        } else if (message.startsWith("/clientList")) {// 用户列表
                            clientListMessage();
                        }
                    } else {// 默认：发送信息并存入数据库
                        broadcastMessage(username + ": " + message);
                    }
                } else {// 用户未登陆
                    out.println("请先登陆！");
                    out.println("[/register <用户名> <密码> <确认密码>] 来注册");
                    out.println("[/login <用户名> <密码>] 来登陆");
                }
            }
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

    /**
     * 注册指令处理
     * 
     * @param message 格式：/register <用户名> <密码> <确认密码>
     * @throws IOException
     */
    private void registerHandler(String message) throws IOException {
        String[] parts = message.split(" ", 4);
        if (parts.length == 4) {
            String username = parts[1];
            String password1 = parts[2];
            String password2 = parts[3];

            if (password1.equals(password2)) {// 两次输入密码相同
                // 调用服务器方法验证注册请求
                if (ChatServer.registerCheck(username, password1)) {
                    this.username = username;
                    out.println("欢迎 " + username + "！");
                    broadcastMessage("[System]: " + username + " 加入聊天");
                } else {
                    out.println("用户已存在或者注册异常");
                }
            } else {
                out.println("请保证两次输入密码一致");
            }
        }
    }

    /**
     * 登陆指令处理
     * 
     * @param message 格式：/login <用户名> <密码>
     * @throws IOException
     */
    private void loginHandler(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];
            // 调用服务器方法验证登陆请求
            if (ChatServer.loginCheck(username, password)) {
                this.username = username;
                out.println("欢迎 " + username + "！");
                broadcastMessage("[System]: " + username + " 加入聊天");
            } else {
                out.println("用户名或密码错误，请重试");
            }
        }
    }

    /**
     * 广播系统信息
     * 
     * @param message
     * @throws IOException
     */
    private void broadcastMessage(String message) throws IOException {
        // 遍历客户端处理线程集合发送信息
        for (ClientHandler clientHandler : clientHandlers) {
            clientHandler.out.println(message);
        }
    }

    /**
     * 发送私聊信息
     * 
     * @param message 格式：/private <用户名> <消息>
     * @throws IOException
     */
    private void sendPrivateMessage(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String targetUsername = parts[1];
            String privateMessage = parts[2];
            // 查找发送对象
            for (ClientHandler clientHandler : clientHandlers) {
                if (clientHandler.username.equals(targetUsername)) {
                    // 发送私聊信息
                    clientHandler.out.println("[Private] " + username + ": " + privateMessage);
                    this.out.println("[Private] to " + targetUsername + ": " + privateMessage);
                    return;
                }
            }
            this.out.println("未找到用户 " + targetUsername);
        }
    }

    /**
     * 请求在线用户数与用户列表（包括未登陆用户）
     */
    private void clientListMessage() throws IOException {
        this.out.println(clientHandlers.size() + "在线用户：");
        for (ClientHandler clientHandler : clientHandlers) {
            this.out.print("[" + clientHandler.username + "] ");
        }
    }
}
