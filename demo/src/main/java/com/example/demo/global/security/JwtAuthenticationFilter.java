// 요청 필터에서 JWT 검증
// 토큰 검사, 유저 인증 상태 세팅 필터(문지기)
package com.example.demo.global.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 요청마다 한 번씩 동작하는 필터
// 사용자 요청 -> 요청 가로챔(Authorization 헤더 확인) -> Bearer로 시작하면 JWT 토큰 추출
// -> 토큰 유효성 검사 -> 토큰에서 이메일 꺼내 DB에서 유저 정보 조회
// -> 인증 객체 생성(시큐리티에서 로그인 인식) -> 현재 요청의 SecurityContext에 인증 정보 저장
// -> 다음 필터 또는 컨트롤러로 요청 전달
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 요청 가로챔(Authorization 헤더 확인)
        String header = request.getHeader("Authorization");

        // Bearer로 시작하면 JWT 토큰 추출
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            // 토큰 유효성 검사
            // 유효하지 않으면 다음 필터로 넘김, 유효하면 이메일 추출
            if (jwtTokenProvider.validateToken(token)) {
                // 토큰에서 이메일 추출. 유저 정보 조회
                String email = jwtTokenProvider.getEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);
                // 인증 객체 생성
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 현재 요청 SecurityContext에 인증 정보 저장
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        }
        // 다음 필터 또는 컨트롤러로 요청 전달
        filterChain.doFilter(request, response);
    }
}
