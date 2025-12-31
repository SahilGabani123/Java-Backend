package com.example.demo.exception;

import com.example.demo.security.JwtErrorCode;

public class JwtAuthenticationException extends RuntimeException {

    private final int code;

    public JwtAuthenticationException(JwtErrorCode error) {
        super(error.getMessage());
        this.code = error.getCode();
    }

    public int getCode() {
        return code;
    }
}
