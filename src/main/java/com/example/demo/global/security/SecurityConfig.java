// spring security 설정
// 인증, 인가, 필터, 암호화를 등록하고 관리(설계도)
package com.example.demo.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration  // 설정 클래스임을 명시
@RequiredArgsConstructor    // final 필드를 자동 주입
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;  // JWT 필터 주입해서 사용
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // csrf.disable() -> CSRF(사이트 간 요청 위조) 보호 꺼둠
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // api/auth/** 경로(회원가입, 로그인)은 인증 없이 접근 가능
                        .requestMatchers("/api/auth/**").permitAll()
                        // 위 경로 외 모든 요청은 JWT 인증 필요
                        .anyRequest().authenticated()
                )
                // UsernamePasswordAuthenticationFilter 실행 전 JWT 필터가 먼저 실행(요청의 JWT 검증)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    // 비밀번호 암호화 설정
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // BCrypt 알고리즘 사용
    }
    // 인증 매니저 등록
    // 로그인 시 아이디/비밀번호 검증하는 핵심 컴포넌트임
    // 내부적으로 CustomUserDetailsService를 호출해 DB의 사용자 정보 확인함
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
