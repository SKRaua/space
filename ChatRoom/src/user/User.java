package user;

import java.util.HashSet;
import java.util.Set;

public class User {
    private String userName;
    private int userID;
    private Set<Integer> chats; // 存储用户参与的群组 ID

    // Constructor
    public User(String userName, int userID) {
        this.userName = userName;
        this.userID = userID;
        this.chats = new HashSet<>();
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public Set<Integer> getChats() {
        return chats;
    }

    public void setChats(Set<Integer> chats) {
        this.chats = chats;
    }

    // Add a group to user's group list
    public void addGroup(int chatID) {
        this.chats.add(chatID);
    }

    // Remove a group from user's group list
    public void removeGroup(int groupID) {
        this.chats.remove(groupID);
    }
}
