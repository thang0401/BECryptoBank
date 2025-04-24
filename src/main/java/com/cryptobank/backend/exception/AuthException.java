package com.cryptobank.backend.exception;

public class AuthException extends RuntimeException {
    private final String userId;

    public AuthException(String message) {
        super(message);
        this.userId = null;
    }

    public AuthException(String message, String userId) {
        super(message);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
}