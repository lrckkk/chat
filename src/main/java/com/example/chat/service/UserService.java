package com.example.chat.service;

import com.example.chat.model.User;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import lombok.Value;


import java.sql.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class UserService {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Chat";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "liu123";



    public static final AttributeKey<User> USER_KEY = AttributeKey.valueOf("user");

    // 用于保存在线用户信息：userId -> Channel
    private static final Map<String, Channel> onlineUsers = new ConcurrentHashMap<>();

    public User authenticate(String username, String password) throws SQLException {
        // 模拟数据库验证
//        if ("1".equals(username) && "admin123".equals(password)) {
//            User user = new User();
//            user.setUserId("1"); // 实际应用中应该用唯一 ID，如 UUID
//            user.setUsername(username);
//            return user;
//        }
        String sql = "SELECT username, password FROM user WHERE username = ? AND password = ?";
        Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
        System.out.println(conn);
        PreparedStatement pstmt = conn.prepareStatement(sql) ;

            // 设置查询参数
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setUserId("1");
                    user.setUsername(rs.getString("username"));
                    return user;
                }
            }

        return null;

    }

    public void addOnlineUser(User user, Channel channel) {
        onlineUsers.put(user.getUsername(), channel);
        System.out.println("<UNK>" + onlineUsers + "<UNK>");
    }


    public void removeOnlineUser(String userId) {
        onlineUsers.remove(userId);
    }

    public static Map<String, Channel> getOnlineUsers() {
        return onlineUsers;
    }
}
