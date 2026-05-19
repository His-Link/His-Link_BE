package com.hislink.common.util;

import com.hislink.domain.user.entity.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String CLAIM_TYPE = "type";
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";

    @Value("${security.jwt.secret}")
    private String secret;

    @Value("${security.jwt.access-token-expire-ms}")
    private long accessTokenExpireMs;

    @Value("${security.jwt.refresh-token-expire-ms}")
    private long refreshTokenExpireMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(Long userId, String email, Role role) {
        return createToken(userId, email, role, TokenType.ACCESS, accessTokenExpireMs);
    }

    public String createRefreshToken(Long userId, String email, Role role) {
        return createToken(userId, email, role, TokenType.REFRESH, refreshTokenExpireMs);
    }

    private String createToken(Long userId, String email, Role role, TokenType tokenType, long expireMs) {
        Date now = new Date();
        Date expiredAt = new Date(now.getTime() + expireMs);
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_TYPE, tokenType.name())
                .setIssuedAt(now)
                .setExpiration(expiredAt)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean validateToken(String token, TokenType expectedType) {
        try {
            Claims claims = parseClaims(token);
            String type = claims.get(CLAIM_TYPE, String.class);
            return expectedType.name().equals(type);
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException ex) {
            return false;
        }
    }

    public Long getUserId(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    public TokenType getTokenType(String token) {
        String type = parseClaims(token).get(CLAIM_TYPE, String.class);
        return TokenType.valueOf(type);
    }
}
