package message;

import java.util.*;
import chat.Chat;
import java.time.ZonedDateTime;

public class SyncMessage extends Message {

    // private String content; // 文本消息内容
    // private String messageType;// 文本类型
    private List<Chat> chats;
    private List<TextMessage> messages;

    public SyncMessage(int senderId, int chatId, List<Chat> chats, List<TextMessage> messages,
            ZonedDateTime timestamp) {
        super(senderId, chatId, 0, timestamp);
        this.chats = chats;
        this.messages = messages;
        // this.content = content;
        // this.messageType = "chat";
    }

    public List<Chat> getChats() {
        return chats;
    }

    public List<TextMessage> getContent() {
        return messages;
    }

    // public SyncMessage(int senderId, int chatId, String content) {// String
    // messageType
    // super(senderId, chatId);
    // this.content = content;
    // // this.messageType = messageType;
    // }

    // public SyncMessage(int senderId, int chatId, String content, int messageId,
    // ZonedDateTime timestamp) {// String
    // // messageType
    // super(senderId, chatId, messageId, timestamp);
    // this.content = content;
    // this.messageType = messageType;
    // }

    // public String getContent() {
    // return content;
    // }

    @Override
    public String getMessageType() {
        return "sync";
    }

    @Override
    public void displayMessage() {
        System.out.println("Sync Message from " + getSenderId() + " in chat " + getChatId());
    }
}
