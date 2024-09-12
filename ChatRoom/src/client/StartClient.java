package client;

public class StartClient {

    /**
     * 启动客户端
     * 
     * @param args
     */
    public static void main(String[] args) {
        // 实例客户端并为其构建ui
        ChatClient client = new ChatClient("localhost", 12345);
        new ChatClientGUI(client);
    }
}
