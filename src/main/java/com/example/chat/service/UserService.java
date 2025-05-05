package com.example.chat.service;

import com.example.chat.model.User;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import redis.clients.jedis.Jedis;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class UserService {
    public static final AttributeKey<User> USER_KEY = AttributeKey.valueOf("user");
//    private final Map<String, Channel> onlineUsers = new ConcurrentHashMap<>();
private static final Map<String, Channel> onlineUsers = new ConcurrentHashMap<>();
    private final Jedis jedis = new Jedis("localhost");

    public User authenticate(String username, String password) {
        // 实际应使用数据库验证
        if ("admin".equals(username) && "admin123".equals(password)) {
            User user = new User();
            user.setUserId("1");
            user.setUsername(username);
            return user;
        }
        return null;
    }

    public void addOnlineUser(User user, Channel channel) {
        onlineUsers.put(user.getUserId(), channel);
        jedis.hset("online_users", user.getUserId(), user.getUsername());
    }

    public void removeOnlineUser(String userId) {
        onlineUsers.remove(userId);
        jedis.hdel("online_users", userId);
    }
    public static Map<String, Channel> getOnlineUsers() {
        return onlineUsers;
    }

}