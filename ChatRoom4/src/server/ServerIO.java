package server;

import java.io.*;
import java.net.*;
import java.util.Set;

/**
 * 客户端处理器，一个客户端对应一个线程
 */
public class ServerIO implements Runnable {
    private Socket socket; // 连接套接字
    private PrintWriter out; // 向客户端输出
    private BufferedReader in; // 从客户端输入
    private String username; // 用户名
    // private ClientManager clientManager; // 客户端管理器

    /**
     * 实例客户端处理器
     * 
     * @param socket        通信套接字
     * @param clientManager 客户端管理器
     */
    public ServerIO(Socket socket) throws IOException {// , ClientManager clientManager
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
        // this.clientManager = clientManager;
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
            out.println("[/createGroup <群聊名称>] 创建群聊");
            out.println("[/joinGroup <群聊名称>] 加入群聊");
            out.println("[/leaveGroup <群聊名称>] 离开群聊");
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
                            sendPrivateMessage(message);
                        } else if (message.startsWith("/createGroup")) { // 创建群聊
                            createGroupChat(message);
                        } else if (message.startsWith("/joinGroup")) { // 加入群聊
                            joinGroupChat(message);
                        } else if (message.startsWith("/leaveGroup")) { // 离开群聊
                            leaveGroupChat(message);
                        } else if (message.startsWith("/group")) { // 群聊信息
                            groupMessage(message);
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
            try {
                socket.close();
                ChatServer.getClientManager().removeClient(this);
                ChatServer.getClientManager().broadcastMessageToAll("[System]: [" + username + "] 离开聊天。");
            } catch (IOException e) {
                e.printStackTrace();
            }
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
     * @throws IOException
     */
    private void registerHandler(String message) throws IOException {
        String[] parts = message.split(" ", 4);
        if (parts.length == 4) {
            String username = parts[1];
            String password1 = parts[2];
            String password2 = parts[3];

            if (password1.equals(password2)) { // 两次输入密码相同
                // 调用数据库连接，验证注册请求
                int result = ChatServer.getDbOperations().registerUser(username, password1);
                switch (result) {
                    case 1:
                        this.username = username;
                        out.println("欢迎 " + username + "！");
                        ChatServer.getClientManager().broadcastMessageToAll("[System]: [" + username + "] 加入聊天");
                        break;
                    case 0:
                        out.println("用户已存在");
                        break;
                    case -1:
                        out.println("注册异常");
                        break;
                }
            } else {
                out.println("请保证两次输入密码一致");
            }
        }
    }

    /**
     * 处理登录请求
     * 
     * @param message 格式：/login <用户名> <密码>
     * @throws IOException
     */
    private void loginHandler(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String username = parts[1];
            String password = parts[2];

            // 调用数据库连接验证登录请求
            int result = ChatServer.getDbOperations().loginUser(username, password);
            switch (result) {
                case 1:
                    this.username = username;
                    out.println("欢迎 " + username + "！");
                    ChatServer.getClientManager().broadcastMessageToAll("[System]: [" + username + "] 加入聊天");
                    break;
                case 0:
                    out.println("用户不存在");
                    break;
                case -1:
                    out.println("登录异常");
                    break;
            }
        }
    }

    /**
     * 发送私聊消息
     *
     * @param message 格式：/private <用户名> <消息>
     * @throws IOException
     */
    private void sendPrivateMessage(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String targetUsername = parts[1];
            String privateMessage = parts[2];
            ServerIO targetClient = ChatServer.getClientManager().findClient(targetUsername);
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
     * @param message 格式：/group <群聊> <消息>
     * @throws IOException
     */
    private void groupMessage(String message) throws IOException {
        String[] parts = message.split(" ", 3);
        if (parts.length == 3) {
            String groupName = parts[1];
            String groupMessage = parts[2];
            ChatServer.getClientManager().broadcastMessage(groupName,
                    "/group " + groupName + " [" + username + "]: " + groupMessage);
        }
    }

    /**
     * 处理群聊创建请求
     * 
     * @param message 格式：/createGroup <群聊名称>
     * @throws IOException
     */
    private void createGroupChat(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String groupName = parts[1];
            if (ChatServer.getClientManager().createGroupChat(groupName, this)) {
                sendMessage("/group " + groupName + " 群聊 " + groupName + " 创建成功");
            } else {
                sendMessage("群聊 " + groupName + " 已存在");
            }

        }
    }

    /**
     * 处理加入群聊请求
     * 
     * @param message 格式：/joinGroup <群聊名称>
     * @throws IOException
     */
    private void joinGroupChat(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String groupName = parts[1];
            ChatServer.getClientManager().addMemberToGroup(groupName, this);
            ChatServer.getClientManager().broadcastMessage(groupName,
                    "/group " + groupName + " " + username + " 成功加入 " + groupName);
        }
    }

    /**
     * 处理离开群聊请求
     * 
     * @param message 格式：/leaveGroup <群聊名称>
     * @throws IOException
     */
    private void leaveGroupChat(String message) throws IOException {
        String[] parts = message.split(" ", 2);
        if (parts.length == 2) {
            String groupName = parts[1];
            ChatServer.getClientManager().removeMemberFromGroup(groupName, this);
        }
    }

    /**
     * 发送用户列表
     *
     * @throws IOException
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

}
