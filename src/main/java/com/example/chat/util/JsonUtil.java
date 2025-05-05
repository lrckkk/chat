package com.example.chat.util;

import com.example.chat.model.Message;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonUtil {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Message parseMessage(byte[] bytes) throws Exception {
        return mapper.readValue(bytes, Message.class);
    }

    public static byte[] serializeMessage(Message message) throws Exception {
        return mapper.writeValueAsBytes(message);
    }
}