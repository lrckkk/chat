package com.example.chat.service;

import com.example.chat.model.Message;
import io.netty.channel.Channel; // 添加这个导入
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ChatRoomService {
    private final Map<String, CopyOnWriteArraySet<String>> rooms = new ConcurrentHashMap<>();

    public void joinRoom(String userId, String roomId) {
        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(userId);
    }

    public void leaveRoom(String userId, String roomId) {
        if (rooms.containsKey(roomId)) {
            rooms.get(roomId).remove(userId);
        }
    }

    public void broadcastMessage(Message message) {
        String roomId = message.getRoomId();
        if (rooms.containsKey(roomId)) {
            rooms.get(roomId).forEach(userId -> {
                // 需要确保UserService中的onlineUsers是静态可访问的
                Channel channel = UserService.getOnlineUsers().get(userId);
                if (channel != null && channel.isActive()) {
                    channel.writeAndFlush(message);
                }
            });
        }
    }
}