package client;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ChatHistoryManager {
    private static final String FILE_PATH_SUFFIX = "chatHistory.txt";
    private Map<String, StringBuilder> chatHistoryMap;

    public ChatHistoryManager() {
        chatHistoryMap = new HashMap<>();
    }

    /**
     * 添加消息到指定聊天对象的聊天记录
     */
    public void appendMessage(String chatName, String message) {
        if (!chatHistoryMap.containsKey(chatName)) {
            chatHistoryMap.put(chatName, new StringBuilder());
        }
        chatHistoryMap.get(chatName).append(message).append("\n");
        saveChatHistory(ChatClient.getUsername()); // 每次添加消息后保存到本地文件
    }

    /**
     * 获取指定聊天对象的聊天记录
     */
    public String getChatHistory(String chatName) {
        return chatHistoryMap.getOrDefault(chatName, new StringBuilder()).toString();
    }

    /**
     * 删除指定聊天对象的聊天记录
     */
    public void deleteChatHistory(String chatName) {
        chatHistoryMap.remove(chatName);
        saveChatHistory(ChatClient.getUsername()); // 删除后保存到本地文件
    }

    /**
     * 返回所有聊天对象
     */
    public Map<String, StringBuilder> getAllChatHistories() {
        return chatHistoryMap;
    }

    /**
     * 加载本地文件中的聊天记录
     */
    public void loadChatHistory(String usrname) {
        try (BufferedReader reader = new BufferedReader(new FileReader(usrname + "_" + FILE_PATH_SUFFIX))) {
            String line;
            String currentChat = null;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) { // 新的聊天对象
                    currentChat = line.substring(1); // 去掉前缀 "#"
                    chatHistoryMap.put(currentChat, new StringBuilder());
                } else if (currentChat != null) {
                    chatHistoryMap.get(currentChat).append(line).append("\n");
                }
            }
        } catch (IOException e) {
            System.out.println("无法加载聊天记录：" + e.getMessage());
        }
    }

    /**
     * 将聊天记录保存到本地文件
     */
    private void saveChatHistory(String usrname) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usrname + "_" + FILE_PATH_SUFFIX))) {
            for (Map.Entry<String, StringBuilder> entry : chatHistoryMap.entrySet()) {
                writer.write("#" + entry.getKey() + "\n"); // 用 # 标记聊天对象
                writer.write(entry.getValue().toString());
            }
        } catch (IOException e) {
            System.out.println("无法保存聊天记录：" + e.getMessage());
        }
    }
}
