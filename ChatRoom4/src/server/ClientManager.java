package server;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ClientManager {
    private Map<String, Set<ServerIO>> chatHandlers; // 存储群聊及其成员
    private Set<ServerIO> allHandlers; // 存储所有在线客户端

    public ClientManager() {
        this.chatHandlers = new ConcurrentHashMap<>();// 线程安全集合
        this.allHandlers = ConcurrentHashMap.newKeySet();
    }

    /**
     * 添加客户端到客户端管理器
     *
     * @param client 客户端处理器
     */
    public void addClient(ServerIO client) {
        allHandlers.add(client);
    }

    /**
     * 移除客户端从客户端管理器
     *
     * @param client 客户端处理器
     */
    public void removeClient(ServerIO client) {
        allHandlers.remove(client);
    }

    /**
     * 查找客户端
     *
     * @param username 用户名
     * @return 找到的客户端处理器，或 null
     */
    public ServerIO findClient(String username) {
        for (ServerIO client : allHandlers) {
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
        for (ServerIO client : allHandlers) {
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
    public boolean createChat(String chatName, ServerIO creator) throws IOException {
        if (!chatHandlers.containsKey(chatName)) {
            Set<ServerIO> members = new HashSet<>();
            members.add(creator);
            chatHandlers.put(chatName, members);
            // creator.sendMessage("群聊 " + chatName + " 创建成功");
            // broadcastMessage(chatName, "[System]: [" + creator.getUsername() + "] 创建了群聊
            // " + chatName);
            return true;
        } else {
            // creator.sendMessage("群聊 " + chatName + " 已存在");
            return false;
        }
    }

    /**
     * 广播消息到群聊成员
     *
     * @param chatName 群聊名称
     * @param message  消息内容
     */
    public void broadcastMessage(String chatName, String message) {
        Set<ServerIO> members = chatHandlers.get(chatName);
        if (members != null) {
            for (ServerIO member : members) {
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
        for (ServerIO client : allHandlers) {
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
    public void addMemberToChat(String chatName, ServerIO member) {
        Set<ServerIO> members = chatHandlers.get(chatName);
        if (members != null) {
            members.add(member);
        }
    }

    /**
     * 移除成员从群聊
     *
     * @param chatName 群聊名称
     * @param member   成员
     */
    public void removeMemberFromChat(String chatName, ServerIO member) {
        Set<ServerIO> members = chatHandlers.get(chatName);
        if (members != null) {
            members.remove(member);
        }
    }

}
