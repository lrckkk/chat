package com.example.chat.protocol;

import lombok.Getter;

@Getter
public enum StatusCode {
    SUCCESS(200, "OK"),
    UNAUTHORIZED(401, "Unauthorized"),
    INTERNAL_ERROR(500, "Internal Server Error");

    // Getters
    private final int code;
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}