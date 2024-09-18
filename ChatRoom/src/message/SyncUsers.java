package message;

import java.time.ZonedDateTime;
import java.util.List;

// import chat.Chat;
import user.*;

public class SyncUsers extends Message {
    // private static final long serialVersionUID = 1L;

    private List<User> users;// 用户表
    private User user;// 本地用户

    public SyncUsers(int senderId, int chatId, String content, int messageId, ZonedDateTime timestamp,
            User user, List<User> users) {
        super(senderId, chatId, messageId, timestamp);
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String getMessageType() {
        return "SyncUsers";
    }

    @Override
    public void displayMessage() {

    }
}
