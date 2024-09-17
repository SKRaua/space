package chat;

import java.util.HashSet;
import java.util.Set;

public class Chat {
    private int chatId; // 聊天ID
    private String chatName; // 聊天ID
    private Set<String> participants; // 参与者ID

    public Chat(int chatId, String chatName) {
        this.chatId = chatId;
        this.chatName = chatName;
        this.participants = new HashSet<>();
    }

    public Chat(int chatId) {
        this.chatId = chatId;
        this.participants = new HashSet<>();
    }

    public Chat(String chatName) {
        this.chatName = chatName;
        this.participants = new HashSet<>();
    }

    public int getChatId() {
        return chatId;
    }

    public String getchatName() {
        return chatName;
    }

    public Set<String> getParticipants() {
        return participants;
    }

    public void addParticipant(String userId) {
        participants.add(userId);
    }

    public void removeParticipant(String userId) {
        participants.remove(userId);
    }

    public boolean isPrivateChat() {
        return participants.size() == 2;
    }
}
