package client;

import chat.Chat;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class ChatHistoryManager {

    private Map<Integer, String> chatsCache = new HashMap<>();

    private Map<String, String> chatHistories = new HashMap<>();

    public void saveChatHistory(Chat chat) {
        int chatID = chat.getChatId();
        String chatName = chat.getChatName();
        File chatFile = new File(chatName + ".txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatFile))) {
            if (chatHistories.containsKey(chatName)) {
                writer.write(chatHistories.get(chatName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        File chatsCacheFile = new File("chatsCache.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(chatsCacheFile))) {
            if (!chatsCache.containsKey(chatID)) {
                writer.write(chatID + " " + chatName + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void appendMessage(String chatName, String message) {
        chatHistories.put(chatName, chatHistories.getOrDefault(chatName, "") + message + "\n");
        saveChatHistory(new Chat(chatName)); // Save updated history
    }

    public Map<String, String> getAllChatHistories() {
        return chatHistories;
    }

    public String getChatHistory(String chatName) {
        return chatHistories.getOrDefault(chatName, "");
    }

    public Map<Integer, String> getAllCache() {
        return chatsCache;
    }

    public String getChatname(int chatID) {
        return chatsCache.getOrDefault(chatID, "");
    }
}
