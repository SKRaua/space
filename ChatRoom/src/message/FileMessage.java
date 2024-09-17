package message;

public class FileMessage extends Message {
    private byte[] fileData; // 文件数据

    public FileMessage(int messageId, int senderId, int chatId, byte[] fileData) {
        super(senderId, chatId, messageId);
        this.fileData = fileData;
    }

    public byte[] getFileData() {
        return fileData;
    }

    @Override
    public String getMessageType() {
        return "file";
    }

    @Override
    public void displayMessage() {
        System.out.println("File Message from " + getSenderId() + " in chat " + getChatId()
                + " with file data of length " + fileData.length);
    }
}
