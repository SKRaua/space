package message;

// import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

import chat.Chat;

public class SyncChats extends Message {// implements Serializable
    // private static final long serialVersionUID = 1L;

    private List<Chat> chats;

    public SyncChats(int senderId, int chatId, String content, int messageId, ZonedDateTime timestamp,
            List<Chat> chats) {
        super(senderId, chatId, messageId, timestamp);
        this.chats = chats;
    }

    public List<Chat> getChats() {
        return chats;
    }

    @Override
    public String getMessageType() {
        return "syncChats";
    }

    @Override
    public void displayMessage() {

    }
}
