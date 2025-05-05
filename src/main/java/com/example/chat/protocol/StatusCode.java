package com.example.chat.protocol;

public enum StatusCode {
    SUCCESS(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_ERROR(500, "Internal Server Error");

    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    // Getters
    public int getCode() {
        return code;
    }
    public String getMessage() {
        return message;
    }
}