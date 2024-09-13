package client;

public class StartClient {

    /**
     * 启动客户端
     * 
     * @param args
     */
    public static void main(String[] args) {
        // 实例客户端并为其构建UI
        // new ChatClientGUI("192.168.139.133", 22345);
        new ChatClientGUI("localhost", 12345);
    }
}
