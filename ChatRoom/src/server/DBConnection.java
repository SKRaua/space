package server;

import java.sql.*;
import java.util.*;

public class DBConnection {

    private static DBConnection instance;
    private Connection dbConn;
    private String url;
    private String user;
    private String password;

    public static final int SUCCESSED = -1;
    public static final int FAILED = -2;
    public static final int EXCEPTION = -3;

    private DBConnection(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        connect(url, user, password);
    }

    // 获取单例实例
    public static synchronized DBConnection getInstance(String url, String user, String password) {
        if (instance == null || instance.dbConn == null || !instance.url.equals(url)
                || !instance.user.equals(user) || !instance.password.equals(password)) {
            if (instance != null) {
                instance.closeConnection();// 断开数据库连接释放资源
            }
            instance = new DBConnection(url, user, password);// 实例新的单例
        }
        return instance;
    }


    private void connect(String url, String user, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            dbConn = DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            // dbConn = null;
            closeConnection();
        }
    }

    // 查找用户
    public synchronized ResultSet findUser(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        try {
            PreparedStatement pstmt = dbConn.prepareStatement(query);
            pstmt.setString(1, username);
            return pstmt.executeQuery();
        } catch (SQLException | NullPointerException e) {// 查询异常或连接为空
            e.printStackTrace();
            return null;
        }
    }

    // 注册用户
    public synchronized int registerUser(String username, String password) {
        try (ResultSet rs = findUser(username)) {
            if (rs != null && rs.next()) {
                return FAILED; // 用户已存在
            }

            String insertQuery = "INSERT INTO users (username, userPassword) VALUES (?, ?)";
            try (PreparedStatement pstmt = dbConn.prepareStatement(insertQuery)) {
                pstmt.setString(1, username);
                pstmt.setString(2, password);
                if (pstmt.executeUpdate() > 0) {
                    return SUCCESSED; // 插入成功
                } else {
                    return EXCEPTION; // 插入异常
                }
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return EXCEPTION; // 查询异常
        }
    }

    // 登录用户
    public synchronized int loginUser(String username, String password) {
        try (ResultSet rs = findUser(username)) {
            if (rs != null && rs.next()) {
                String storedPassword = rs.getString("userPassword");
                if (storedPassword.equals(password)) {
                    return SUCCESSED; // 登录成功
                }
            } else {
                return FAILED; // 用户名不存在
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return EXCEPTION; // 查询异常
        }
        return EXCEPTION;
    }

    // 执行查询操作并返回结果列表
    public synchronized List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> resultList = new ArrayList<>();// List为行，Map为属性列，Object为属性值
        try (PreparedStatement pstmt = dbConn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = metaData.getColumnName(i);
                    Object columnValue = rs.getObject(i);
                    row.put(columnName, columnValue);
                }
                resultList.add(row);
            }
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            Map<String, Object> row = new HashMap<>();
            row.put("查询异常", sql);
            resultList.add(row);
        }
        return resultList;
    }

    // 执行更新操作并返回修改行数
    public synchronized int executeUpdate(String sql) {
        try (PreparedStatement pstmt = dbConn.prepareStatement(sql)) {
            return pstmt.executeUpdate(); // 返回影响的行数
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return EXCEPTION; // 返回异常状态
        }
    }

    // 测试数据库连接
    public synchronized boolean testConnection() {
        String testQuery = "SELECT 1";
        try (PreparedStatement pstmt = dbConn.prepareStatement(testQuery);
                ResultSet rs = pstmt.executeQuery()) {
            return rs.next(); // 如果能获取结果，则连接有效
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
            return false; // 连接无效
        }
    }

    // 关闭数据库连接
    public synchronized void closeConnection() {
        try {
            if (dbConn != null && !dbConn.isClosed()) {
                dbConn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
