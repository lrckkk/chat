package com.example.chat.service;

import com.example.chat.model.User;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    public static final AttributeKey<User> USER_KEY = AttributeKey.valueOf("user");

    // 用于保存在线用户信息：userId -> Channel
    private static final Map<String, Channel> onlineUsers = new ConcurrentHashMap<>();

    public User authenticate(String username, String password) {
        // 模拟数据库验证
        if ("admin".equals(username) && "admin123".equals(password)) {
            User user = new User();
            user.setUserId("1"); // 实际应用中应该用唯一 ID，如 UUID
            user.setUsername(username);
            return user;
        }
        return null;
    }

    public void addOnlineUser(User user, Channel channel) {
        onlineUsers.put(user.getUserId(), channel);
        System.out.println("<UNK>" + onlineUsers + "<UNK>");
    }

    public void removeOnlineUser(String userId) {
        onlineUsers.remove(userId);
    }

    public static Map<String, Channel> getOnlineUsers() {
        return onlineUsers;
    }
}
