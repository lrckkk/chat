package com.example.chat.model;

import com.example.chat.protocol.MessageType;
import com.example.chat.protocol.StatusCode;
import lombok.Data;

import java.util.List;

@Data
public class Message {
    private MessageType type;
    private StatusCode status;
    private String sender;
    private String roomId;
    private String content;
    private long timestamp;
    private List<String>users;
    public Message() {
        this.timestamp = System.currentTimeMillis(); // 自动设置时间戳
    }
}