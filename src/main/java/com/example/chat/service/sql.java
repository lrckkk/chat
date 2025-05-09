package com.example.chat.service;

import java.sql.*;

public class sql {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/chat";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "123456";
//    public void updateUserRoomAndActivate(String userId, String roomId) throws SQLException {
//        // 1. 更新用户的 room_at 字段
//        String updateUserSql = "UPDATE user SET room_at = ? WHERE username = ?";
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(updateUserSql)) {
//
//            pstmt.setString(1, roomId);
//            pstmt.setString(2, userId);
//            int affectedRows = pstmt.executeUpdate();
//
//            System.out.println("更新用户房间状态影响行数: " + affectedRows);
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//        // 2. 更新或插入房间状态
//        String roomCheckSql = "SELECT roomId FROM room WHERE roomId = ?";
//        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
//             PreparedStatement pstmt = conn.prepareStatement(roomCheckSql)) {
//
//            pstmt.setString(1, roomId);
//
//            try (ResultSet rs = pstmt.executeQuery()) {
//                if (rs.next()) {
//                    // 房间存在，更新激活状态
//                    String updateRoomSql = "UPDATE room SET isActive = 1 WHERE roomId = ?";
//                    try (PreparedStatement updateStmt = conn.prepareStatement(updateRoomSql)) {
//                        updateStmt.setString(1, roomId);
//                        updateStmt.executeUpdate();
//                        System.out.println("房间已存在，激活状态已更新");
//                    }
//                } else {
//                    // 房间不存在，插入新房间
//                    String insertRoomSql = "INSERT INTO room (roomId, isActive) VALUES (?, 1)";
//                    try (PreparedStatement insertStmt = conn.prepareStatement(insertRoomSql)) {
//                        insertStmt.setString(1, roomId);
//                        insertStmt.executeUpdate();
//                        System.out.println("新房间已创建并激活");
//                    }
//                }
//            }
//        }
//    }


    // 新增消息存储方法
    public void insertMessage(String content, String username, String roomId) {
        String sql = "INSERT INTO message (content, username, room_at) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, content);
            pstmt.setString(2, username);
            pstmt.setString(3, roomId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }
//
    // 重构房间更新方法（使用事务+ON DUPLICATE KEY）
    public void updateUserRoomAndActivate(String userId, String roomId) {
        String updateSql = "UPDATE user SET room_at = ? WHERE username = ?";
        String insertSql = "INSERT INTO room (roomId, isActive) VALUES (?, 1) ON DUPLICATE KEY UPDATE last_active_time=NOW()";

        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
            conn.setAutoCommit(false);  // 开启事务

            // 更新用户房间信息
            try (PreparedStatement pstmtUser = conn.prepareStatement(updateSql)) {
                pstmtUser.setString(1, roomId);
                pstmtUser.setString(2, userId);
                pstmtUser.executeUpdate();
            }
            if (roomId!=null)
            {
                System.out.println("roomId!=null");
                // 插入/更新房间信息
                try (PreparedStatement pstmtRoom = conn.prepareStatement(insertSql)) {
                    pstmtRoom.setString(1, roomId);
                    pstmtRoom.executeUpdate();
                }
            }


            conn.commit();  // 提交事务
        } catch (SQLException e) {
            throw new RuntimeException("Transaction failed", e);
        }
    }

    // 新增房间状态维护方法
    public void updateRoomStatus(String roomId) {
        String sql = "UPDATE room SET isActive = CASE WHEN EXISTS (" +
                "SELECT 1 FROM user WHERE room_at = ?) THEN 1 ELSE 0 END";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update room status", e);
        }
    }
//注册账号
    public void Register(String username, String password) {
        String sql = "INSERT INTO user (username, password, room_at) VALUES (?, ?, null)";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
//            pstmt.setString(3, null);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }


}
