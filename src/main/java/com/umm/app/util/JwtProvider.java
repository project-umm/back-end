package com.umm.app.util;

import com.umm.app.dto.SignInResponse;
import com.umm.exception.BaseException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtProvider {
    private final Key key;

    public JwtProvider(@Value("${spring.jwt.secret}") String jwtKey) {
        byte[] keyBytes = Decoders.BASE64.decode(jwtKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.getSubject();
    }

    public SignInResponse generateToken(Authentication authentication) {
        long now = System.currentTimeMillis();
        String accessToken = generateAccessToken(authentication, now);
        String refreshToken = generateRefreshToken();

        return SignInResponse.builder()
                .grantType("Bearer")
                .access(accessToken)
                .refresh(refreshToken)
                .build();
    }

    public String generateAccessToken(Authentication authentication, long now) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        Date accessTokenExpiresIn = new Date(now + 1800000); // 30분

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(accessTokenExpiresIn)
                .setIssuer("umm")
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken() {
        return UUID.randomUUID().toString().replace("-","");
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);
        String tokenType = claims.get("type", String.class);

        if (!"access".equals(tokenType)) {
            throw new BaseException(401, "해당 토큰은 유효하지 않은 토큰 타입입니다. : " + tokenType);
        }

        if (claims.get("auth") == null) {
            throw new BaseException(401, "로그인이 필요합니다.. : " + tokenType);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
                .filter(role -> !role.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, null, authorities);
    }

    public void validateToken(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            String tokenType = claims.get("type", String.class);
            if (!"access".equals(tokenType)) {
                throw new BaseException(401, "해당 토큰은 유효하지 않은 토큰 타입입니다. : " + tokenType);
            }

            if (claims.getExpiration().before(new Date())) {
                throw new BaseException(401, "기간이 유효하지 않은 토큰입니다.");
            }
        } catch (ExpiredJwtException e) {
            throw new BaseException(401, "만료된 토큰입니다. : ");
        } catch (Exception e) {
            throw new BaseException(500, "토큰 발급 중 예기치 못한 장애가 발생했습니다. \n 사유 : " + e.getMessage());
        }
    }

    private Claims parseClaims(String token) {
        validateToken(token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
