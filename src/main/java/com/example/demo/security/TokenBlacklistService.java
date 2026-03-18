package com.example.demo.security;

import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory token blacklist service.
 * Stores invalidated JWT tokens until they naturally expire,
 * so that logged-out tokens cannot be reused.
 */
@Service
public class TokenBlacklistService {

    // Map of token -> expiry time (ms). ConcurrentHashMap for thread safety.
    private final Map<String, Long> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * Add a token to the blacklist with its expiration time.
     * Expired tokens are cleaned up lazily on each call.
     *
     * @param token     the raw JWT token string
     * @param expiresAt the token's expiration date
     */
	public void blacklist(String token, Date expiresAt) {
        cleanupExpiredTokens();
        blacklistedTokens.put(token, expiresAt.getTime());
    }

    /**
     * Check whether a token has been blacklisted.
     *
     * @param token the raw JWT token string
     * @return true if the token is blacklisted (and not yet expired)
     */
    public boolean isBlacklisted(String token) {
        Long expiryMs = blacklistedTokens.get(token);
        if (expiryMs == null) {
            return false;
        }
        // If the token has already expired naturally, remove it and treat as not blacklisted
        if (System.currentTimeMillis() > expiryMs) {
            blacklistedTokens.remove(token);
            return false;
        }
        return true;
    }

    /**
     * Remove all tokens that have already expired to prevent memory leaks.
     */
    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        blacklistedTokens.entrySet().removeIf(entry -> entry.getValue() < now);
    }
}
