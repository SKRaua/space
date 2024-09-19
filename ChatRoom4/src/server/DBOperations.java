package server;

import java.sql.*;
import java.util.*;

/**
 * 需要使用的与数据库交互的所有操作
 */
public class DBOperations {

    private DBConnManager connectionManager;// 数据库连接管理器

    /**
     * 实例连接管理类，传入配置信息
     * 
     * @param dbConfig 连接配置
     */
    public DBOperations(String[] dbConfig) {
        this.connectionManager = new DBConnManager(dbConfig);
    }

    /**
     * 获得数据库连接
     * 
     * @return 数据库连接
     * @throws SQLException 连接异常
     */
    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    /**
     * 找到一个用户返回结果集
     * 
     * @param username 用户名
     * @return 用户结果集
     * @throws SQLException 查询异常
     */
    public ResultSet findUser(String username) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?")) {
            pstmt.setString(1, username);
            return pstmt.executeQuery();
        }
    }

    /**
     * 处理一个注册请求写入数据库
     * 
     * @param username 用户名
     * @param password 密码
     * @return 注册结果
     * @throws SQLException 注册异常
     */
    public boolean registerUser(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO users (user_name, user_password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * 处理一个登陆请求写入数据库
     * 
     * @param username 用户名
     * @param password 密码
     * @return 登陆结果
     * @throws SQLException 登陆异常
     */
    public boolean loginUser(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT user_password FROM users WHERE user_name = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("user_password");
                    return storedPassword.equals(password);
                } else {
                    return false; // 用户名不存在
                }
            }
        }
    }

    /**
     * 新建聊天
     * 
     * @param creator  创建者
     * @param chatName 聊天名
     * @return 创建是否成功
     * @throws SQLException 创建异常
     */
    public boolean createChat(String creator, String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO chats (chat_name) VALUES (?)")) {
            pstmt.setString(1, chatName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0 && addMemberToChat(creator, chatName);
        }
    }

    /**
     * 删除聊天
     * 
     * @param chatName 聊天名
     * @return 是否成功删除
     * @throws SQLException 删除异常
     */
    public boolean deleteChat(String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement deleteChatStmt = conn.prepareStatement("DELETE FROM chats WHERE chat_name = ?")) {
            // 删除 chats 表中的群聊，群聊用户关系表需要加ON DELETE CASCADE
            deleteChatStmt.setString(1, chatName);
            return deleteChatStmt.executeUpdate() > 0;

        }
    }

    /**
     * 添加成员到聊天
     * 
     * @param username 用户名
     * @param chatName 聊天名
     * @return 是否成功添加
     * @throws SQLException 添加异常
     */
    public boolean addMemberToChat(String username, String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO user_chat_map (user_name, chat_name) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, chatName);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * 从聊天移出成员
     * 
     * @param username 用户名
     * @param chatName 聊天名
     * @return 是否成功移出
     * @throws SQLException 移出异常
     */
    public boolean removeMemberFromChat(String username, String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("DELETE FROM user_chat_map WHERE user_name = ? AND chat_name = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, chatName);
            return pstmt.executeUpdate() > 0;
        }
    }

    /**
     * 获取一个用户加入到所有群聊
     * 
     * @param username 用户名
     * @return 加入到所有群聊
     * @throws SQLException 查询异常
     */
    public List<String> getUserChats(String username) throws SQLException {
        List<String> chatList = new ArrayList<>();
        String sql = "SELECT chat_name FROM user_chat_map WHERE user_name = ?";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    chatList.add(rs.getString("chat_name"));
                }
            }
        }
        return chatList;
    }

    /**
     * 执行查询语句
     * 
     * @param sql SQL查询语句
     * @return 查询结果 List为每行，Map为每列
     * @throws SQLException
     */
    public List<Map<String, Object>> executeQuery(String sql) throws SQLException {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
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
        }
        return resultList;
    }

    /**
     * 执行SQL语句
     * 
     * @param sql 语句
     * @return 影响到行数
     * @throws SQLException
     */
    public int executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        }
    }

    /**
     * 用指定配置重新连接数据库
     * 
     * @param dbConfig 连接配置
     * @return 是否连接成功
     */
    public boolean reconnectDatabase(String[] dbConfig) {
        this.connectionManager = new DBConnManager(dbConfig);
        return testConnection();
    }

    /**
     * 测试连接
     * 
     * @return 连接是否成功
     */
    public boolean testConnection() {
        String testQuery = "SELECT 1";
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(testQuery);
                ResultSet rs = pstmt.executeQuery()) {
            return rs.next(); // 如果有结果返回，则连接有效
        } catch (SQLException e) {
            e.printStackTrace();
            return false; // 出现异常，连接无效
        }
    }
}
