package server;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.*;

import message.*;
import user.*;

public class ClientHandler implements Runnable {
    private MessageIO serverIO; // 服务端IO
    // private int userID; // 用户ID
    // private String username;
    private User user;

    /**
     * 实例客户端处理器
     *
     * @param socket 通信套接字
     */
    public ClientHandler(Socket socket) throws IOException {
        this.serverIO = new MessageIO(socket);
        user = new User("游客", -1);
    }

    /**
     * 读取客户端传入信息的线程任务
     */
    @Override
    public void run() {
        try {
            ChatServer.getDbOperations().getMessagesSince(getUserID(), null);
            // 向客户端发送欢迎消息和指令说明
            sendMessage(new TextMessage(0, 0, "欢迎来到聊天室！"));
            sendMessage(new TextMessage(0, 0, "[/register <用户名> <密码> <确认密码>] 来注册"));
            sendMessage(new TextMessage(0, 0, "[/login <用户名> <密码>] 来登陆"));
            sendMessage(new TextMessage(0, 0, "[/private <用户名> <消息>] 发起私聊"));
            sendMessage(new TextMessage(0, 0, "[/clientList] 获取用户列表"));

            while (true) {
                Message message = serverIO.receiveMessage();
                handleMessage(message);
            }
        } catch (IOException | ClassNotFoundException | SQLException e) {
            sendMessage(new TextMessage(0, 0, "连接异常"));
            e.printStackTrace();
        } finally {
            try {
                serverIO.close();
                ChatServer.getClientManager().removeClient(this);
                if (getUserID() == -1) {// if (userID == -1) {
                    ChatServer.getClientManager()
                            .broadcastMessageToAll(new TextMessage(0, 0, getUserID() + " 离开聊天"));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Message message) throws IOException {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            switch (textMessage.getMessageType()) {
                case "chat":// 群组或私聊
                    chatMessage(textMessage);
                    break;
                // case "text":// 世界聊天
                // groupMessage(textMessage);
                // break;
                case "register":
                    registerHandler(textMessage);
                    break;
                case "login":
                    loginHandler(textMessage);
                    break;
                // case "Private":
                // sendPrivateMessage(textMessage);
                // break;
                case "createGroup":
                    createChat(textMessage);
                    break;
                case "joinGroup":
                    joinGroupChat(textMessage);
                    break;
                case "leaveGroup":
                    leaveGroupChat(textMessage);
                    break;
                case "clientList":
                    sendClientList();
                    break;
                default:
                    sendMessage(new TextMessage(0, 0, "消息类型异常"));
                    break;
            }
        } else if (message instanceof FileMessage) {
            // 处理文件信息
            // ChatServer.getClientManager().broadcastMessageToAll(message);
        }
    }

    public void sendMessage(Message message) {
        try {
            serverIO.sendMessage(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getUserID() {
        return user.getUserID();
    }

    // public void setUserID(int userID) {
    // this.userID = userID;
    // }

    public String getUserName() {
        return user.getUserName();
    }

    // public void setUserName(String username) {
    // this.username = username;
    // }

    private void registerHandler(TextMessage message) {
        String[] parts = message.getContent().split(" ", 3);
        if (parts.length == 3) {
            String username = parts[0];
            String password1 = parts[1];
            String password2 = parts[2];
            ////////////////////////////////
            if (password1.equals(password2)) {
                try {
                    if (ChatServer.getDbOperations().registerUser(username, password1)) {
                        user.setUserName(username);
                        // setUserName(username);
                        user.setUserID(ChatServer.getDbOperations().findUserID(username));
                        // setUserID(ChatServer.getDbOperations().findUserID(username));
                        sendMessage(new TextMessage(0, 0, "用户ID异常"));
                        sendMessage(new TextMessage(0, 0, "欢迎 " + username + "！"));
                        ChatServer.getClientManager()
                                .broadcastMessageToAll(new TextMessage(0, 0, username + " 加入聊天"));
                    } else {
                        sendMessage(new TextMessage(0, 0, "用户已存在"));
                    }
                } catch (SQLException e) {
                    sendMessage(new TextMessage(0, 0, "注册异常"));
                    e.printStackTrace();
                }
            }
        } else {
            sendMessage(new TextMessage(0, 0, "请保证两次输入密码一致"));
        }
    }

    private void loginHandler(TextMessage message) {
        String[] parts = message.getContent().split(" ", 2);
        if (parts.length == 2) {
            String username = parts[0];
            String password = parts[1];
            ////////////////////////////////

            try {
                if (ChatServer.getDbOperations().loginUser(username, password)) {
                    user.setUserName(username);
                    // setUserName(username);
                    user.setUserID(ChatServer.getDbOperations().findUserID(username));
                    // setUserID(ChatServer.getDbOperations().findUserID(username));

                    sendMessage(new TextMessage(0, 0, "欢迎 " + username + "！"));
                    ChatServer.getClientManager()
                            .broadcastMessageToAll(new TextMessage(0, 0, username + " 加入聊天"));
                } else {
                    sendMessage(new TextMessage(0, 0, "密码或用户名错误"));
                }
            } catch (SQLException e) {
                sendMessage(new TextMessage(0, 0, "登录异常"));
                e.printStackTrace();
            }
        }
    }

    // private void sendPrivateMessage(TextMessage message) throws IOException {
    // String[] parts = message.getContent().split(" ", 2);
    // if (parts.length == 2) {
    // String targetUsername = parts[0];
    // String privateMessage = parts[1];
    // ClientHandler targetClient =
    // ChatServer.getClientManager().findClient(targetUsername);
    // if (targetClient != null) {
    // targetClient.sendMessage(new TextMessage("1", "Private", userID,
    // privateMessage));
    // this.sendMessage(new TextMessage("1", "Private", targetUsername,
    // privateMessage));
    // // Save private message to database
    // try {
    // ChatServer.getDbOperations().sendPrivateMessage(
    // UUID.randomUUID().toString(),
    // userID,
    // targetUsername,
    // privateMessage);
    // } catch (SQLException e) {
    // e.printStackTrace();
    // }
    // } else {
    // this.sendMessage(new TextMessage("1", "System", "", "未找到用户 " +
    // targetUsername));
    // }
    // }
    // }

    private void chatMessage(TextMessage message) {
        try {
            ChatServer.getDbOperations().recordMessage(message);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // String[] parts = message.getContent().split(" ", 2);
        // if (parts.length == 2) {
        // String chatName = parts[0];
        // String chatMessage = parts[1];
        // // ChatServer.getClientManager().broadcastMessage(groupName,
        // // new TextMessage(, "Group", userID, groupMessage));
        // // ChatServer.getClientManager().broadcastMessage(groupName,
        // // new TextMessage("Group", userID, groupMessage));
        // // Save group message to database
        // try {
        // // ChatServer.getDbOperations().recordMessage(
        // // user.getUserID(),
        // // groupName,
        // // groupMessage);
        // } catch (SQLException e) {
        // e.printStackTrace();
        // }
        // }
    }

    private void createChat(TextMessage message) {
        String[] parts = message.getContent().split(" ", 2);
        if (parts.length == 2) {
            String chatName = parts[0];
            String chatPassword = parts[1];
            try {
                if (ChatServer.getDbOperations().createChatGroup(getUserID(), chatName, chatPassword)) {
                    sendMessage(new TextMessage(0, 0, "群聊 " + chatName + " 创建成功"));
                } else {
                    sendMessage(new TextMessage(0, 0, "群聊 " + chatName + " 已存在"));
                }
            } catch (SQLException e) {
                sendMessage(new TextMessage(0, 0, "聊天创建异常"));
                e.printStackTrace();
            }
        }

        // String[] parts = message.getContent().split(" ", 2);
        // if (parts.length == 2) {
        // String groupName = parts[1];
        // if (ChatServer.getClientManager().createGroupChat(groupName, this)) {
        // sendMessage(new TextMessage(0, 0, "群聊 " + groupName + " 创建成功"));
        // } else {
        // sendMessage(new TextMessage(0, 0, "群聊 " + groupName + " 已存在"));
        // }
        // }
    }

    private void joinGroupChat(TextMessage message) throws IOException {
        String[] parts = message.getContent().split(" ", 2);
        if (parts.length == 2) {
            String chatName = parts[0];
            String chatPassword = parts[1];
            try {
                if (ChatServer.getDbOperations().joinChatGroup(user.getUserID(), chatName, chatPassword)) {
                    sendMessage(new TextMessage(0, 0, user.getUserName() + " 成功加入 " + chatName));
                } else {
                    sendMessage(new TextMessage(0, 0, "密码错误"));
                }
            } catch (SQLException e) {
                sendMessage(new TextMessage(0, 0, "加入聊天异常"));
                e.printStackTrace();
            }
            // ChatServer.getClientManager().addMemberToGroup(groupName, this);
            // ChatServer.getClientManager().broadcastMessage(groupName,
            // new TextMessage("1", "System", "", userID + " 成功加入 " + groupName));
        }
    }

    private void leaveGroupChat(TextMessage message) throws IOException {
        String[] parts = message.getContent().split(" ", 1);
        if (parts.length == 1) {
            String groupName = parts[0];
            // ChatServer.getClientManager().removeMemberFromGroup(groupName, this);
            try {
                ChatServer.getDbOperations().leaveChatGroup(user.getUserID(), groupName);
            } catch (SQLException e) {
                sendMessage(new TextMessage(0, 0, "离开群聊异常"));
                e.printStackTrace();
            }
            sendMessage(new TextMessage(0, 0, "已离开群聊 " + groupName));
        }
    }

    private void sendClientList() throws IOException {
        Set<String> clientList = ChatServer.getClientManager().getClientList();
        sendMessage(new TextMessage(0, 0, "在线用户： " + String.join(", ", clientList)));
    }
}
