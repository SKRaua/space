package server;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.sql.*;

/**
 * 聊天室服务端
 */
public class ChatServer {
    private static final int PORT = 12345;// 连接端口
    private static Set<ClientHandler> clientHandlers;// 客户机处理线程集
    private static Connection dbConn;// 数据库连接

    /**
     * 实例服务端
     * 
     * @param url       服务端连接的数据库地址（mysql）
     * @param user      数据库用户
     * @param passsword 数据库密码
     */
    public ChatServer(String url, String user, String passsword) {
        //connectToDatabase(url, user, passsword);// 连接数据库
        clientHandlers = new HashSet<>();
        buildConnection();// 等待客户端连接（会在等待连接中阻塞程序）
    }

    /**
     * 尝试与新的客户端连接
     */
    private void buildConnection() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server is running...");
            while (true) {
                Socket clientSocket = serverSocket.accept();// 等待客户端连接（阻塞循环）
                ClientHandler clientHandler = new ClientHandler(clientSocket, clientHandlers);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();// 实例新的客户端处理线程
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 连接到数据库
     * 
     * @param url       服务端连接的数据库地址（mysql）
     * @param user      数据库用户
     * @param passsword 数据库密码
     */
    public boolean connectToDatabase(String url, String user, String passsword) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConn = DriverManager.getConnection(url, user, passsword);
            return true;// 成功连接
        } catch (ClassNotFoundException e) {
            System.out.println("数据库驱动加载异常");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.out.println("数据库连接异常");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向数据库检查注册请求
     * 
     * @param usernameIn 用户名输入
     * @param passwordIn 密码输入
     * @return 是否成功注册
     */
    public static boolean registerCheck(String usernameIn, String passwordIn) {
        PreparedStatement pstmt;
        ResultSet rs;
        // 查询用户名是否已存在
        String checkUsername = "SELECT username FROM users WHERE username = ?";
        // 插入新的用户信息
        String register = "INSERT INTO users (username, userPassword) VALUES (?,?);";
        try {
            pstmt = dbConn.prepareStatement(checkUsername);
            pstmt.setString(1, usernameIn);
            rs = pstmt.executeQuery();
            if (rs.next()) {// 用户已存在
                System.out.println("用户已存在");
                return false;
            } else {// 添加新用户数据
                pstmt = dbConn.prepareStatement(register);
                pstmt.setString(1, usernameIn);
                pstmt.setString(2, passwordIn);
                if (pstmt.executeUpdate() == 0) {// 数据库操作异常
                    return false;
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("数据库操作异常");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向数据库检查登陆请求
     * 
     * @param usernameIn 用户名输入
     * @param passwordIn 密码输入
     * @return 是否成功登陆
     */
    public static boolean loginCheck(String usernameIn, String passwordIn) {
        PreparedStatement pstmt;
        ResultSet rs;
        // 查询对应用户的密码
        String sql = "SELECT userPassword FROM users WHERE username = ?";
        try {
            pstmt = dbConn.prepareStatement(sql);
            pstmt.setString(1, usernameIn); // 设置用户名查找
            rs = pstmt.executeQuery();
            if (rs.next()) {// 找到用户
                String userPassword = rs.getString("userPassword");
                System.out.println("Found user: " + usernameIn + " with password: " + userPassword);
                return passwordIn.equals(userPassword); // 验证密码是否匹配
            } else {// 未找到用户
                System.out.println("未找到用户：" + usernameIn);
            }
        } catch (SQLException e) {
            System.out.println("数据库查询异常");
            e.printStackTrace();
        }
        return false;// 用户名或密码不匹配
    }

    public String printLog(String log) {
        return log;
    }

    public String close(String log) {
        return log;
    }

}
