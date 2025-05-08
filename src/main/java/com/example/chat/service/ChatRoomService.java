package com.example.chat.service;

import com.example.chat.model.Message;
import com.example.chat.protocol.MessageType;
import io.netty.channel.Channel; // 添加这个导入

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import com.example.chat.service.sql;

//import static com.example.chat.service.UserService;

public class ChatRoomService {
    private final static Map<String, CopyOnWriteArraySet<String>> rooms = new ConcurrentHashMap<>();//rooms是一个线程安全的map,用来维护聊天室数据
    private MessageType messageType;
    private UserService userService;
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/chat";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456";
    public void joinRoom(String userId, String roomId) throws SQLException {
        rooms.computeIfAbsent(roomId, k -> new CopyOnWriteArraySet<>()).add(userId);
        sql sql = new sql();
        sql.updateUserRoomAndActivate(userId, roomId);
        CopyOnWriteArraySet<String> userlist = rooms.get(roomId);
        System.out.println("用户列表:"+userlist);


//
    }
//
//    public void joinRoom(String userId, String roomId) {
//        try {
//            // 先检查房间是否存在（通过用户表间接判断）
//            String checkSql = "SELECT COUNT(*) FROM user WHERE room_at = ?";
//            try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//                 PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
//                pstmt.setString(1, roomId);
//                ResultSet rs = pstmt.executeQuery();
//                if (!rs.next() || rs.getInt(1) == 0) {
//                    throw new RoomNotFoundException("Room not exists and cannot be joined");
//                }
//            }
//
//            // 执行用户加入操作
//            sql sql = new sql();
//            sql.updateUserRoomAndActivate(userId, roomId);
//
//            // 广播用户加入事件
//            broadcastUserChange(roomId, "join", userId);
//        } catch (SQLException e) {
//            throw new RuntimeException("Join room failed", e);
//        }
//    }

//    public void leaveRoom(String userId, String roomId) {
//        if (rooms.containsKey(roomId)) {
//            rooms.get(roomId).remove(userId);
//        }
//    }


    public void leaveRoom(String userId, String roomId) {
        sql sql = new sql();
        sql.updateUserRoomAndActivate(userId, null);  // 清空用户房间信息

//        // 广播用户离开事件
//        broadcastUserChange(roomId, "leave", userId);

        // 异步检查房间状态
        CompletableFuture.runAsync(() -> {
            try {
                checkAndDeactivateRoom(roomId);
            } catch (SQLException e) {
                // 记录日志
            }
        });
    }

    private void checkAndDeactivateRoom(String roomId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM user WHERE room_at = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                new sql().updateRoomStatus(roomId);  // 更新房间状态
            }
        }
    }

//    // 新增用户变更广播方法
//    private void broadcastUserChange(String roomId, String action, String userId) {
//        Message msg = new Message();
//
//        msg.setType(MessageType.SYSTEM_NOTIFICATION);
//        msg.setRoomId(roomId);
//        msg.setContent(action + " user: " + userId);
//        broadcastMessage(msg);
//    }

    // 新增消息存储方法
    private void saveMessageToDatabase(Message message) {
        sql sql = new sql();
        sql.insertMessage(message.getContent(), message.getSender(), message.getRoomId());
    }
    // 修改消息广播方法
//    public void broadcastMessage(Message message) {
//        // 保存消息到数据库
//        saveMessageToDatabase(message);
//
//        // 广播消息给在线用户
//
//        Set<String> userChannels = UserService.getOnlineUsers().keySet();
//        for (String userId : userChannels) {
//            Channel channel = UserService.getOnlineUsers().get(userId);
//            if (channel.isActive() && roomId.equals(channel.attr(USER_ROOM_KEY).get())) {
//                channel.writeAndFlush(message);
//            }
//        }
//    }
    public void broadcastMessage(Message message) {
        String roomId = message.getRoomId();
        System.out.println("[广播消息] 房间ID: " + roomId + "，消息内容: " + message.getContent());
        saveMessageToDatabase(message);
        if (!rooms.containsKey(roomId)) {
            System.out.println("[广播消息] 房间 " + roomId + " 不存在，跳过广播");
            return;
        }

        for (String userId : rooms.get(roomId)) {
            System.out.println("[<UNK>] <UNK>ID: " + rooms + "<UNK>: ");
            System.out.println("尝试向用户 " + userId + " 发送消息");
            message.setReceiver(userId);//为web渲染设置接收者
            Channel channel = UserService.getOnlineUsers().get(userId);

            if (channel == null) {
                System.out.println("用户 " + userId + " 没有找到 Channel（可能不在线）");
            } else if (!channel.isActive()) {
                System.out.println("用户 " + userId + " 的 Channel 已断开");
            } else {
                System.out.println("向用户 " + userId + " 发送消息中...");
                channel.writeAndFlush(message);
            }
        }
    }

}