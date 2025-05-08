package com.example.chat.service;

import java.sql.*;

public class sql {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/Chat";
    private static final String JDBC_USER = "root";
    private static final String JDBC_PASSWORD = "liu123";
    public void updateUserRoomAndActivate(String userId, String roomId) throws SQLException {
        // 1. 更新用户的 room_at 字段
        String updateUserSql = "UPDATE user SET room_at = ? WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(updateUserSql)) {

            pstmt.setString(1, roomId);
            pstmt.setString(2, userId);
            int affectedRows = pstmt.executeUpdate();

            System.out.println("更新用户房间状态影响行数: " + affectedRows);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        // 2. 更新或插入房间状态
        String roomCheckSql = "SELECT roomId FROM room WHERE roomId = ?";
        try (Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(roomCheckSql)) {

            pstmt.setString(1, roomId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // 房间存在，更新激活状态
                    String updateRoomSql = "UPDATE room SET isActive = 1 WHERE roomId = ?";
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateRoomSql)) {
                        updateStmt.setString(1, roomId);
                        updateStmt.executeUpdate();
                        System.out.println("房间已存在，激活状态已更新");
                    }
                } else {
                    // 房间不存在，插入新房间
                    String insertRoomSql = "INSERT INTO room (roomId, isActive) VALUES (?, 1)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertRoomSql)) {
                        insertStmt.setString(1, roomId);
                        insertStmt.executeUpdate();
                        System.out.println("新房间已创建并激活");
                    }
                }
            }
        }
    }


}
