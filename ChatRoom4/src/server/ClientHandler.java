package server;

import java.io.*;
import java.net.*;
import java.sql.SQLException;
import java.util.Set;

/**
 * 客户端处理器，一个客户端对应一个线程
 */
public class ClientHandler implements Runnable {
    private Socket socket; // 连接套接字
    private PrintWriter out; // 向客户端输出
    private BufferedReader in; // 从客户端输入
    private String username; // 用户名

    /**
     * 实例客户端处理器
     * 
     * @param socket        通信套接字
     * @param clientManager 客户端管理器
     */
    public ClientHandler(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * 读取客户端传入信息的线程任务
     */
    @Override
    public void run() {
        try {
            // 向客户端发送欢迎消息和指令说明
            out.println("[/register <用户名> <密码> <确认密码>] 来注册");
            out.println("[/login <用户名> <密码>] 来登陆");
            out.println("[/private <用户名> <消息>] 发送私密信息");
            out.println("[/createChat <聊天名称>] 创建聊天");
            out.println("[/joinChat <聊天名称>] 加入聊天");
            out.println("[/leaveChat <聊天名称>] 离开聊天");
            out.println("[/clientList] 获取用户列表");

            String message;
            while ((message = in.readLine()) != null) {
                if (message.startsWith("/login")) { // 判断登陆指令
                    loginHandler(message);
                } else if (message.startsWith("/register")) { // 判断注册指令
                    registerHandler(message);
                } else if (username != null) { // 用户登陆
                    if (message.startsWith("/")) { // 判断指令信息
                        if (message.startsWith("/private")) { // 私聊
                            privateMessage(message);
                        } else if (message.startsWith("/createChat")) { // 创建群聊
                            createChat(message);
                        } else if (message.startsWith("/joinChat")) { // 加入群聊
                            joinChatChat(message);
                        } else if (message.startsWith("/leaveChat")) { // 离开群聊
                            leaveChatChat(message);
                        } else if (message.startsWith("/chat")) { // 群聊信息
                            chatMessage(message);
                        } else if (message.startsWith("/clientList")) { // 用户列表
                            sendClientList();
                        }
                    } else { // 默认：发送信息到群聊或公共频道
                        ChatServer.getClientManager().broadcastMessageToAll("[" + username + "]: " + message);
                    }
                } else { // 用户未登陆
                    out.println("请先登陆！");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeConnection();
        }
    }

    /**
     * 发送消息
     * 
     * @param message 消息内容
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
        out.println(message);
    }

    /**
     * 处理注册请求
     * 
     * @param message 格式：/register <用户名> <密码> <确认密码>
     * @throws IOException 注册异常
     */
    private void registerHandler(String message) throws IOException {
        String[] parts = message.split(" ", 4);
        if (parts.length == 4) {
            String username = parts[1];
            String password1 = parts[2];
            String password2 = parts[3];

            if (password1.equals(password2)) { // 两次输入密码相同
                try { // 调用数据库连接，验证注册请求
                    boolean result = ChatServer.getDbOperations().registerUser(username, password1);
                    if (result) {
                        this.username = username;
                        ChatServer.getClientManager().loadChat(this);// 同步聊天窗口
                        Logger.log("[System]: [" + username + "] 加入聊天");
                        out.println("欢迎 " + username + "！");
                        ChatServer.getClientManager().broadcastMessageToAll("[System]: [" + username + "] 加入聊天");
                    } else {
                        out.println("注册异常，换个用户名试试");
                    }
                } catch (SQLException e) {
                    out.println("注册异常");
                    e.printStackTrace();
                }
            } else {
                out.println("请保证两次输入密码一致");
            }
        } else {
            out.println("格式错误");
        }
    }

    /**
     * 处理登录请求
     * 
     * @param message 格式：/login <用户名> <密码>
     * @throws IOException 登陆异常
     */
    private void loginHandler(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            try { // 调用数据库连接验证登录请求
                boolean result = ChatServer.getDbOperations().loginUser(username, password);
                if (result) {
                    this.username = username;
                    out.println("/login " + username);
                    ChatServer.getClientManager().loadChat(this);// 同步聊天窗口
                    Logger.log("[System]: [" + username + "] 加入聊天");
                    out.println("欢迎 " + username + "！");
                    ChatServer.getClientManager().broadcastMessageToAll("[System]: [" + username + "] 加入聊天");
                } else {
                    out.println("用户名或密码错误");
                }
            } catch (SQLException e) {
                out.println("登录异常");
                e.printStackTrace();
            }
        } else {
            out.println("格式错误");
        }
    }

    /**
     * 发送私聊消息
     *
     * @param message 格式：/private <用户名> <消息>
     * @throws IOException 私聊异常
     */
    private void privateMessage(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String targetUsername = parts[1];
            String privateMessage = parts[2];
            ClientHandler targetClient = ChatServer.getClientManager().findClient(targetUsername);
            if (targetClient != null) {
                targetClient.sendMessage("[Private] [" + username + "]: " + privateMessage);
                this.sendMessage("[Private] to [" + targetUsername + "]: " + privateMessage);
            } else {
                this.sendMessage("未找到用户 " + targetUsername);
            }
        }
    }

    /**
     * 发送群聊消息
     *
     * @param message 格式：/chat <群聊> <消息>
     * @throws IOException 聊天异常
     */
    private void chatMessage(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String chatName = parts[1];
            String chatMessage = parts[2];
            ChatServer.getClientManager().broadcastMessage(chatName,
                    "/chat " + chatName + " [" + username + "]: " + chatMessage);
        }
    }

    /**
     * 处理群聊创建请求
     * 
     * @param message 格式：/createChat <群聊名称>
     * @throws IOException 创建异常
     */
    private void createChat(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String chatName = parts[1];
            if (ChatServer.getClientManager().createChat(chatName, this)) {
                sendMessage("/chat " + chatName + " 群聊 " + chatName + " 创建成功");
            } else {
                sendMessage("群聊 " + chatName + " 已存在");
            }

        }
    }

    /**
     * 处理加入群聊请求
     * 
     * @param message 格式：/joinChat <群聊名称>
     * @throws IOException 加入异常
     */
    private void joinChatChat(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String chatName = parts[1];
            ChatServer.getClientManager().addMemberToChat(chatName, this);
            ChatServer.getClientManager().broadcastMessage(chatName,
                    "/chat " + chatName + " " + username + " 成功加入 " + chatName);
        }
    }

    /**
     * 处理离开群聊请求
     * 
     * @param message 格式：/leaveChat <群聊名称>
     * @throws IOException 离开异常
     */
    private void leaveChatChat(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String chatName = parts[1];
            ChatServer.getClientManager().removeMemberFromChat(chatName, this);
        }
    }

    /**
     * 发送用户列表
     *
     * @throws IOException 发送异常
     */
    private void sendClientList() throws IOException {
        Set<String> clientList = ChatServer.getClientManager().getClientList();
        this.sendMessage("在线用户： " + String.join(", ", clientList));
    }

    /**
     * 获取用户名
     * 
     * @return 用户名
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置用户名
     *
     * @param username 用户名
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 关闭客户端连接并清理资源
     */
    public void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 广播用户离开消息
            if (username != null) {
                Logger.log("[System]: [" + username + "] 离开聊天");
                ChatServer.getClientManager().broadcastMessageToAll("[System]: [" + username + "] 离开聊天。");
            }
        }
    }

}
