// JWT 생성/검증 로직
// 토큰 발급, 검증(유효성 검사)하고 그 안에서 사용자 정보를 꺼내는 역할
// 로그인 세션 대체 역할
package com.example.demo.global.security;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {
    // application.properties의 설정값 불러옴
    @Value("${jwt.secret}")
    private String secretKey;   // 토큰 서명(암호화)에 사용할 비밀키


    @Value("${jwt.expirationMs}")
    private long validityInMilliseconds;    // 토큰 유효 기간(1시간으로 설정해둠)

    private final CustomUserDetailsService userDetailsService;
    
    // userDetailsService -> 토큰에서 사용자 정보 복원할 때 사용
    public JwtTokenProvider(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // JWT 토큰 생성 후 반환
    public String createToken(String email) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(email)  // 토큰에 들어갈 정보 설정
                .setIssuedAt(now)   // 발급 시간
                .setExpiration(exp) // 만료 시간
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 비밀키로 서명(HS256알고리즘 설정)
                .compact(); // 문자열 형태의 토큰 완성
    }

    // 토큰 유효성 검사
    // 토큰이 비밀키로 올바르게 서명되었는지, 만료되었는지 검사
    // 예외 발생시 false 리턴(인증 실패)
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰 안에 저장된 사용자 이메일 추출
    public String getEmail(String token) {
        return Jwts.parser().setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // 토큰 기반으로 Spring Security 인증 객체 생성
    // 토큰 -> 이메일 -> DB에서 사용자 정보 조회
    // Spring Security 표준 인증 객체(UsernamePasswordAuthenticationToken)로 변환
    // 객체가 SecurityContextHolder에 저장되면 로그인된 상태가 됨
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(getEmail(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }
}
