package server;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private Map<String, Set<ClientHandler>> chatHandlers; // 存储群聊及其成员
    private Set<ClientHandler> allHandlers; // 存储所有在线客户端
    private Map<ClientHandler, Thread> handlerThreads; // 存储所有在线客户端处理线程

    public ClientManager() {
        this.chatHandlers = new ConcurrentHashMap<>(); // 线程安全集合
        this.allHandlers = ConcurrentHashMap.newKeySet();
        this.handlerThreads = new ConcurrentHashMap<>();
    }

    /**
     * 添加客户端到客户端管理器
     *
     * @param client 客户端处理器
     */
    public void addClient(ClientHandler client, Thread handlerThread) {
        allHandlers.add(client);
        handlerThreads.put(client, handlerThread);

    }

    /**
     * 从数据库加载客户端的群聊信息，并将其加入群聊
     *
     * @param client 需要同步群聊信息的客户端
     */
    public void loadChat(ClientHandler client) {
        List<String> chatNames;
        try {
            chatNames = ChatServer.getDbOperations().getUserChats(client.getUsername());
            for (String chatName : chatNames) {// 添加有客户端在线的聊天并向在线客户端发送同步信息
                client.sendMessage("/chat " + chatName + " 欢迎回来");
                chatHandlers.computeIfAbsent(chatName, k -> new HashSet<>()).add(findClient(client.getUsername()));
            }
        } catch (SQLException | IOException e) {
            Logger.log("加载群聊异常");
            e.printStackTrace();
        }
    }

    /**
     * 从客户端管理器移除客户端
     *
     * @param client 客户端处理器
     */
    public void removeClient(ClientHandler client) {
        handlerThreads.get(client).interrupt();// 中断该处理线程
        allHandlers.remove(client);

        // 从所有群聊中移除该客户端
        for (Set<ClientHandler> members : chatHandlers.values()) {
            members.remove(client);
        }

        // 删除空的群聊
        chatHandlers.entrySet().removeIf(entry -> entry.getValue().isEmpty());
    }

    /**
     * 从客户端管理器移除客户端,服务端终端使用
     *
     * @param username 客户端用户名
     */
    public boolean removeClient(String username) {
        ClientHandler client = findClient(username);
        if (client != null) {
            removeClient(client);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 查找客户端
     *
     * @param username 用户名
     * @return 找到的客户端处理器，或 null
     */
    public ClientHandler findClient(String username) {
        for (ClientHandler client : allHandlers) {
            if (client.getUsername().equals(username)) {
                return client;
            }
        }
        return null;
    }

    /**
     * 获取在线用户列表
     *
     * @return 在线用户列表
     */
    public Set<String> getClientList() {
        Set<String> userList = new HashSet<>();
        for (ClientHandler client : allHandlers) {
            userList.add(client.getUsername());
        }
        return userList;
    }

    /**
     * 创建群聊
     *
     * @param chatName 群聊名称
     * @param creator  群聊创建者
     * @throws IOException
     */
    public boolean createChat(String chatName, ClientHandler creator) {
        if (!chatHandlers.containsKey(chatName)) {
            Set<ClientHandler> members = new HashSet<>();
            members.add(creator);
            chatHandlers.put(chatName, members);
            // 同步添加到数据库中
            try {
                ChatServer.getDbOperations().createChat(creator.getUsername(), chatName);
            } catch (SQLException e) {
                Logger.log("添加群聊发送数据库异常");
                e.printStackTrace();
                return false;
            } // 添加到数据库
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建群聊，服务端终端使用
     *
     * @param chatName 群聊名称
     * @param creator  群聊创建者
     * @throws IOException
     */
    public boolean createChat(String chatName) {
        Set<ClientHandler> members = new HashSet<>();
        chatHandlers.put(chatName, members);
        // 同步添加到数据库中
        try {
            ChatServer.getDbOperations().createChat("System", chatName);
            return true;
        } catch (SQLException e) {
            Logger.log("添加群聊发送数据库异常");
            e.printStackTrace();
            return false;
        } // 添加到数据库
    }

    /**
     * 广播消息到群聊成员
     *
     * @param chatName 群聊名称
     * @param message  消息内容
     */
    public void broadcastMessage(String chatName, String message) {
        Set<ClientHandler> members = chatHandlers.get(chatName);
        if (members != null) {
            for (ClientHandler member : members) {
                try {
                    member.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 广播消息到所有在线客户端
     * 
     * @param message 消息内容
     */
    public void broadcastMessageToAll(String message) {
        for (ClientHandler client : allHandlers) {
            try {
                client.sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 添加成员到群聊
     *
     * @param chatName 群聊名称
     * @param member   成员
     */
    public void addMemberToChat(String chatName, ClientHandler member) {
        chatHandlers.computeIfAbsent(chatName, k -> new HashSet<>()).add(member);
        // 同步添加到数据库中
        try {
            ChatServer.getDbOperations().addMemberToChat(member.getUsername(), chatName);
        } catch (SQLException e) {
            Logger.log("添加聊天成员发生数据库异常");
            e.printStackTrace();
        }
    }

    /**
     * 添加成员到群聊，服务端终端使用
     *
     * @param userName 成员
     * @param chatName 群聊名称
     */
    public boolean addMemberToChat(String userName, String chatName) {
        ClientHandler client = findClient(userName);
        if (client != null) {
            addMemberToChat(chatName, client);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 移除成员从群聊
     *
     * @param chatName 群聊名称
     * @param member   成员
     */
    public void removeMemberFromChat(String chatName, ClientHandler member) {
        Set<ClientHandler> members = chatHandlers.get(chatName);
        if (members != null) {
            members.remove(member);
            // 同步删除数据库中的成员
            try {
                ChatServer.getDbOperations().removeMemberFromChat(member.getUsername(), chatName);
            } catch (SQLException e) {
                Logger.log("删除聊天成员发生数据库异常");
                e.printStackTrace();
            }
        }
    }

    /**
     * 移除成员到群聊
     *
     * @param userName 成员
     * @param chatName 群聊名称
     */
    public boolean removeMemberFromChat(String userName, String chatName) {
        ClientHandler client = findClient(userName);
        if (client != null) {
            removeMemberFromChat(chatName, client);
            return true;
        } else {
            return false;
        }

    }
}
