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
                PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            return pstmt.executeQuery();
        }
    }

    public int registerUser(String username, String password) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn
                        .prepareStatement("INSERT INTO users (username, userPassword) VALUES (?, ?)")) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int loginUser(String username, String password) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement("SELECT userPassword FROM users WHERE username = ?")) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("userPassword");
                    return storedPassword.equals(password) ? 1 : 0;
                } else {
                    return -1; // 用户名不存在
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public List<Map<String, Object>> executeQuery(String sql) {
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
        } catch (SQLException e) {
            e.printStackTrace();
            Map<String, Object> row = new HashMap<>();
            row.put("查询异常 ", sql);
            resultList.add(row);
        }
        return resultList;
    }

    public int executeUpdate(String sql) {
        try (Connection conn = getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
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
