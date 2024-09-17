package message;

import java.time.ZonedDateTime;

public abstract class Message {
    private int messageId; // 消息ID//记录到数据库后生成
    private int senderId; // 发送者ID
    private int chatId; // 聊天ID (可以是群聊ID或私聊ID)

    // private long timestamp; // 时间戳
    private ZonedDateTime timestamp;

    public Message(int senderId, int chatId) {
        this.senderId = senderId;
        this.chatId = chatId;
        // this.timestamp = System.currentTimeMillis(); // 获取当前时间
    }

    public Message(int senderId, int chatId, int messageId) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.chatId = chatId;
        // this.timestamp = System.currentTimeMillis(); // 获取当前时间
    }

    public Message(int senderId, int chatId, int messageId, ZonedDateTime timestamp) {
        this.senderId = senderId;
        this.chatId = chatId;
        this.messageId = messageId;
        this.timestamp = timestamp;

        // this.timestamp = System.currentTimeMillis();
    }

    public int getMessageId() {
        return messageId;
    }

    public int getSenderId() {
        return senderId;
    }

    public int getChatId() {
        return chatId;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    // 抽象方法，子类实现
    public abstract String getMessageType();

    public abstract void displayMessage();
}
