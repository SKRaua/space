import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        Class.forName("com.mysql.cj.jdbc.Driver");
        String url = "jdbc:mysql://localhost:3306/ChatRoomData";
        String user = "root";
        String passsword = "88888888";
        Connection conn = DriverManager.getConnection(url, user, passsword);

        PreparedStatement pstmt;
        ResultSet rs;
        String sql = "SELECT * FROM users";

        pstmt = conn.prepareStatement(sql);
        rs = pstmt.executeQuery();
        while (rs.next()) {
            String username = rs.getString("username");
            String userPassword = rs.getString("userPassword");
            System.out.println(username + " " + userPassword);
        }
    }
}