package message;

import java.time.ZonedDateTime;

public class TextMessage extends Message {
    private String content; // 文本消息内容
    private String messageType;// 文本类型

    public TextMessage(int senderId, int chatId, String content) {
        super(senderId, chatId);
        this.content = content;
        this.messageType = "chat";
    }

    public TextMessage(int senderId, int chatId, String content, String messageType) {
        super(senderId, chatId);
        this.content = content;
        this.messageType = messageType;
    }

    public TextMessage(int senderId, int chatId, String content, int messageId, ZonedDateTime timestamp,
            String messageType) {
        super(senderId, chatId, messageId, timestamp);
        this.content = content;
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    @Override
    public String getMessageType() {
        return messageType;
    }

    @Override
    public void displayMessage() {
        System.out.println("Text Message from " + getSenderId() + " in chat " + getChatId() + ": " + content);
    }
}
