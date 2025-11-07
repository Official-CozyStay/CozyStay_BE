package com.project.cozystay.auth;

import com.project.cozystay.user.domain.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Component
public class JwtProvider {

    @Value("${jwt.secret}")
    private String secretString;

    @Value("${jwt.expiration}") // 액세스 토큰 만료 시간 (초)
    private Long accessTokenExpirationSeconds;

    // (선택) 리프레시 토큰 만료 시간 (예: 7일)
    private final Long REFRESH_TOKEN_EXPIRATION_SECONDS = 60L * 60 * 24 * 7;

    private SecretKey secretKey;

    @PostConstruct
    protected void init() {
        // yml의 secretString을 SecretKey 객체로 변환
        this.secretKey = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 8단계: Access Token (입장권) 생성
     */
    public String createAccessToken(Long userId, Role role) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + accessTokenExpirationSeconds * 1000); // 밀리초 단위

        return Jwts.builder()
                .subject(String.valueOf(userId)) // 토큰의 주체(subject) = 우리 DB 유저 ID
                .claim("role", role.getKey())    // "role"이라는 이름으로 권한 정보 추가
                .issuedAt(now)                   // 발급 시간
                .expiration(validity)            // 만료 시간
                .signWith(secretKey)             // 비밀키로 서명
                .compact();
    }

    /**
     * 8단계: Refresh Token (재발급권) 생성
     */
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION_SECONDS * 1000);

        return Jwts.builder()
                .subject(String.valueOf(userId)) // Access Token과 마찬가지로 유저 ID
                .issuedAt(now)
                .expiration(validity)
                .signWith(secretKey)
                .compact();
    }

    // --- (다음 단계) JWT 필터에서 사용할 메서드들 ---

    /**
     * 토큰에서 Claims(정보) 추출
     */
    public Claims getClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.warn("유효하지 않은 토큰입니다. {}", e.getMessage());
            throw new IllegalArgumentException("Invalid token", e); // 예외 처리 필요
        }
    }

    /**
     * 토큰 유효성 검사
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 토큰에서 사용자 ID(Long) 추출
     */
    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }
}
