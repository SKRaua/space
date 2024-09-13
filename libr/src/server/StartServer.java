package server;

public class StartServer {

    /**
     * 启动服务端
     * 
     * @param args
     */
    public static void main(String[] args) throws Exception {
        String url = "jdbc:mysql://localhost:3306/ChatRoomData";
        String user = "root";
        String passsword = "88888888";
        // 传入数据库信息，实例聊天室服务端
        new ChatServer(url, user, passsword);
    }
}
