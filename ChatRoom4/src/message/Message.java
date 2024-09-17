package message;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Serializable {
    private String sender;
    private String content;
    private String timestamp;
    private String chatName;

    public Message(String sender, String content, String chatName) {
        this.sender = sender;
        this.content = content;
        this.chatName = chatName;
        this.timestamp = getCurrentTimestamp();
    }

    private String getCurrentTimestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
    }

    public String getSender() {
        return sender;
    }

    public String getContent() {
        return content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getChatName() {
        return chatName;
    }

    @Override
    public String toString() {
        return "[" + timestamp + "] " + sender + ": " + content;
    }
}
