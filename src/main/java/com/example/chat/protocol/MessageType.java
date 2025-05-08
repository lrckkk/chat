package com.example.chat.protocol;

public enum MessageType {
    LOGIN_REQUEST,   // 登录请求
    LOGIN_RESPONSE,  // 登录响应
    MESSAGE,         // 普通消息
    JOIN_ROOM,       // 加入聊天室
    LEAVE_ROOM,      // 离开聊天室
    HEARTBEAT,       // 心跳检测
    NOTIFICATION,    // 系统通知
    INFORMATION     //

}