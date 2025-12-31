package com.example.demo.security;
public enum JwtErrorCode {
    TOKEN_EXPIRED(401, "Token has expired"),
    INVALID_TOKEN(400, "Invalid Access token"),
    TOKEN_MALFORMED(400, "Malformed Access token"),
    TOKEN_SIGNATURE_INVALID(401, "Invalid JWT signature");

    private final int code;
    private final String message;

    JwtErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() { return code; }
    public String getMessage() { return message; }
}
