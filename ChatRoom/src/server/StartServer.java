package server;

public class StartServer {

    /**
     * 启动服务端
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        int port = 57275;
        String url = "jdbc:mysql://localhost:3306/ChatRoomData";
        String user = "root";
        String password = "88888888";
        String[] dbConfig = new String[] { url, user, password };// MySQL
        // 传入服务器端口，数据库配置，实例聊天室服务端
        new ChatServer(port, dbConfig);
    }
}
