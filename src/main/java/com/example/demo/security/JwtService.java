package com.example.demo.security;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.example.demo.exception.JwtAuthenticationException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {

	private static final String BASE64_SECRET = "xyHp+3+W5+fgpjt+9uDStaL8KsJmuus+KsGgEJpNby1U3N8izpxlbJBLw8jYZvI2DvswJD7DFb/+wg9PanrW1Q==";
	private final SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(BASE64_SECRET));

	private Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
	}

	private Date extractExpiration(String token) {
		return extractAllClaims(token).getExpiration();
	}

	private boolean isTokenExpired(String token) {
		return extractExpiration(token).before(new Date());
	}

	public boolean isValid(String token) {
		try {
			Claims claims = extractAllClaims(token);
			return claims.getExpiration() != null && !isTokenExpired(token);
		} catch (Exception e) {
			return false;
		}
	}

	public void validateToken(String token) {
		try {
			extractAllClaims(token);
		} catch (ExpiredJwtException e) {
			throw new JwtAuthenticationException(JwtErrorCode.TOKEN_EXPIRED);
		} catch (MalformedJwtException e) {
			throw new JwtAuthenticationException(JwtErrorCode.TOKEN_MALFORMED);
		} catch (SignatureException e) {
			throw new JwtAuthenticationException(JwtErrorCode.TOKEN_SIGNATURE_INVALID);
		} catch (Exception e) {
			throw new JwtAuthenticationException(JwtErrorCode.INVALID_TOKEN);
		}
	}

	// 🔹 Create Authentication object
	public Authentication getAuthentication(String token) {
		Claims claims = extractAllClaims(token);
		String username = claims.getSubject();

		UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername(username).password("")
				.authorities("ROLE_USER").build();

		return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
	}
}
