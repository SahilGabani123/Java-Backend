package com.example.demo.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

import org.springframework.stereotype.Component;

import com.example.demo.auth.User;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    private static final long EXPIRATION = 24 * 60 * 60 * 1000; // 1 day

    private static final String BASE64_SECRET =
            "xyHp+3+W5+fgpjt+9uDStaL8KsJmuus+KsGgEJpNby1U3N8izpxlbJBLw8jYZvI2DvswJD7DFb/+wg9PanrW1Q==";

    private final SecretKey key = Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(BASE64_SECRET)
    );
    


    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        claims.put("email", user.getEmail());
        claims.put("name", user.getName());
        claims.put("position", user.getPosition()); // if you have roles
        claims.put("field", user.getField());
        claims.put("phone_number", user.getPhoneNumber());
        return Jwts.builder()
        		.setClaims(claims)
                .setSubject(user.getEmail()) // subject still required
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
