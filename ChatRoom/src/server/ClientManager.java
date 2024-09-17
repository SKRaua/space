package server;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import message.*;

public class ClientManager {

    // private Map<String, Set<ClientHandler>> groupHandlers; // 存储群聊及其成员
    private Set<ClientHandler> allHandlers; // 存储所有在线客户端

    public ClientManager() {
        // this.groupHandlers = new ConcurrentHashMap<>(); // 线程安全集合
        this.allHandlers = ConcurrentHashMap.newKeySet();
    }

    public synchronized void addClient(ClientHandler client) {
        allHandlers.add(client);
    }

    public synchronized void removeClient(ClientHandler client) {
        allHandlers.remove(client);
        // 从所有群聊中移除客户端
        // for (Set<ClientHandler> handlers : groupHandlers.values()) {
        // handlers.remove(client);
        // }
    }

    // public synchronized void sendOnlineMessage(int chatID, Message message) {
    // // Set<ClientHandler> handlers = groupHandlers.get(groupName);
    // Set<ClientHandler> handlers;

    // if (handlers != null) {
    // for (ClientHandler handler : handlers) {
    // try {
    // handler.sendMessage(message);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // }

    // public synchronized void broadcastMessage(String groupName, Message message)
    // {
    // Set<ClientHandler> handlers = groupHandlers.get(groupName);
    // if (handlers != null) {
    // for (ClientHandler handler : handlers) {
    // try {
    // handler.sendMessage(message);
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // }
    // }
    // }

    // public synchronized boolean createGroupChat(String groupName, ClientHandler
    // creator) {
    // if (groupHandlers.containsKey(groupName)) {
    // return false; // 群聊已存在
    // } else {
    // groupHandlers.put(groupName, ConcurrentHashMap.newKeySet());
    // groupHandlers.get(groupName).add(creator);
    // return true;
    // }
    // }

    // public synchronized void addMemberToGroup(String groupName, ClientHandler
    // member) {
    // groupHandlers.computeIfAbsent(groupName, k ->
    // ConcurrentHashMap.newKeySet()).add(member);
    // }

    // public synchronized void removeMemberFromGroup(String groupName,
    // ClientHandler member) {
    // Set<ClientHandler> handlers = groupHandlers.get(groupName);
    // if (handlers != null) {
    // handlers.remove(member);
    // if (handlers.isEmpty()) {
    // groupHandlers.remove(groupName);
    // }
    // }
    // }

    public synchronized Set<String> getClientList() {
        Set<String> clientList = new HashSet<>();
        for (ClientHandler handler : allHandlers) {
            clientList.add(handler.getUserName());
        }
        return clientList;
    }

    // public ClientHandler findClient(String username) {
    // for (ClientHandler handler : allHandlers) {
    // if (handler.getUserID().equals(username)) {
    // return handler;
    // }
    // }
    // return null;
    // }

    public void broadcastMessageToAll(Message message) {
        for (ClientHandler handler : allHandlers) {
            handler.sendMessage(message);
        }
    }
}
