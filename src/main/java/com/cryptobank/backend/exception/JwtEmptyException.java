package com.cryptobank.backend.exception;

public class JwtEmptyException extends RuntimeException {
    public JwtEmptyException(String message) {
        super(message);
    }
}
