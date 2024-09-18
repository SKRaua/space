package client;

import user.*;

import java.util.List;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UsersCache {

    private Map<Integer, String> usersCache = new HashMap<>();

    public void saveUsersCache(User user) {
        int userID = user.getUserID();
        int userName = user.getUserID();
        File usersCacheFile = new File("usersCache.txt");
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(usersCacheFile))) {
            if (!usersCache.containsKey(userID)) {
                writer.write(userID + " " + userName + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // public void appendMessage(String chatName, String message) {
    // chatHistories.put(chatName, chatHistories.getOrDefault(chatName, "") +
    // message + "\n");
    // saveChatHistory(new Chat(chatName)); // Save updated history
    // }

    public Map<Integer, String> getAllCache() {
        return usersCache;
    }

    public String getUsername(int userID) {
        return usersCache.getOrDefault(userID, "");
    }

    public void addCache(List<User> userID) {
        for (User user : userID) {
            usersCache.put(user.getUserID(), user.getUserName());
        }
    }


}
