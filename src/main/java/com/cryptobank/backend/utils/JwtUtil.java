package com.cryptobank.backend.utils;

import com.cryptobank.backend.DTO.AuthResponse;
import com.cryptobank.backend.DTO.UserInformation;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

	@Value("${jwt.secret-key}")
    private String SECRET_KEY;
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 120; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
    }

    // Generic token generation with custom expiration
    public String generateToken(String username, long expiration) {
        return Jwts.builder()
                .header()
                .type("JWT")
                .and()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String generateToken(UserInformation userInformation, long expiration) {
        return Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .subject(userInformation.getId())
            .claim("id", userInformation.getId())
            .claim("email", userInformation.getEmail())
            .claim("fullName", userInformation.getFullName())
            .claim("username", userInformation.getUsername())
            .claim("avatar", userInformation.getAvatar())
            .claim("kycStatus", userInformation.getKycStatus())
            .claim("walletAddress", userInformation.getWalletAddress())
            .claim("isBankAccount", userInformation.getIsBankAccount())
            .claim("isReferralCode", userInformation.getIsReferralCode())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), Jwts.SIG.HS256)
            .compact();
    }

    public AuthResponse generateToken(Map<String, ?> claims) {
        Validate.notNull(claims.get("id"), "Require 'id' in claims");
        JwtBuilder jwtBuilder = Jwts.builder()
            .header()
            .type("JWT")
            .and()
            .claims(claims)
            .subject(claims.get("id").toString())
            .issuedAt(new Date())
            .signWith(getSigningKey(), Jwts.SIG.HS256);
        return new AuthResponse(claims.get("id").toString(),
            jwtBuilder.expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION)).compact(),
            jwtBuilder.expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION)).compact());
    }

    // Generate access token with default expiration (30 minutes)
    public String generateAccessToken(String username) {
        return generateToken(username, ACCESS_TOKEN_EXPIRATION);
    }

    // Generate refresh token with default expiration (1 day)
    public String generateRefreshToken(String username) {
        return generateToken(username, REFRESH_TOKEN_EXPIRATION);
    }

    // Overloaded method: Generate refresh token with custom expiration
    public String generateRefreshToken(String username, long expirationInSeconds) {
        return generateToken(username, expirationInSeconds * 1000); // Convert seconds to milliseconds
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenValid(String token, String username) {
        return extractUsername(token).equals(username) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

}
