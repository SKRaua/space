package server;

import java.sql.*;
import java.util.*;

public class DBOperations {

    private DBConnManager connectionManager;

    public DBOperations(String[] dbConfig) {
        this.connectionManager = new DBConnManager(dbConfig);
    }

    private Connection getConnection() throws SQLException {
        return connectionManager.getConnection();
    }

    public ResultSet findUser(String username) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE user_name = ?")) {
            pstmt.setString(1, username);
            return pstmt.executeQuery();
        }
    }

    public int registerUser(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO users (user_name, user_password) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeUpdate();
        }
    }

    public int loginUser(String username, String password) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("SELECT user_password FROM users WHERE user_name = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("user_password");
                    return storedPassword.equals(password) ? 1 : 0;
                } else {
                    return -1; // 用户名不存在
                }
            }
        }
    }

    public boolean createChat(String creator, String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO chats (chat_name) VALUES (?)")) {
            pstmt.setString(1, chatName);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0 && addMemberToChat(creator, chatName);
        }
    }

    public boolean deleteChat(String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement deleteChatStmt = conn.prepareStatement("DELETE FROM chats WHERE chat_name = ?")) {

            // 删除 chats 表中的群聊，群聊用户关系表需要加ON DELETE CASCADE
            deleteChatStmt.setString(1, chatName);
            return deleteChatStmt.executeUpdate() > 0;

        }
    }

    public boolean addMemberToChat(String username, String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO user_chat_map (user_name, chat_name) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, chatName);
            return pstmt.executeUpdate() > 0;
        }
    }

    public boolean removeMemberFromChat(String username, String chatName) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("DELETE FROM user_chat_map WHERE user_name = ? AND chat_name = ?")) {
            pstmt.setString(1, username);
            pstmt.setString(2, chatName);
            return pstmt.executeUpdate() > 0;
        }
    }

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

    // public Map<String, Set<String>> removeMemberFromChat(String username, String
    // chatName) {
    // try (Connection conn = getConnection();
    // PreparedStatement pstmt = conn
    // .prepareStatement("")) {
    // pstmt.setString(1, username);
    // pstmt.setString(2, chatName);
    // return pstmt.executeUpdate() > 0;
    // } catch (SQLException e) {
    // e.printStackTrace();
    // return false;
    // }
    // }

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

    public int executeUpdate(String sql) throws SQLException {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        }
    }

    public boolean reconnectDatabase(String[] dbConfig) {
        this.connectionManager = new DBConnManager(dbConfig);
        return testConnection();
    }

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
