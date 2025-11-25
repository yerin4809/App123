// 사용자 인증 정보 로드
// 사용자 정보를 DB에서 꺼내 인증 과정에 제공하는 역할(사용자 정보 로딩 서비스)
package com.example.demo.global.security;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;

// UserDetailsService -> Spring Security의 로그인 시 사용자 정보를 가져오는 인터페이스
// MemberRepository -> DB에서 회원을 찾기 위한 JPA Repository
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;

    // UserDetails -> Security가 인증할 때 필요한 사용자 정보 객체
    // loadUserByUsername(String email) -> 로그인할 때 호출. email 기준으로 찾음
    // User.builder() -> Spring Security가 내부적으로 인증할 때 사용하는 UserDetails 객체 생성기
    // 사용자 로그인 요청 -> Spring Security가 자동으로 loadUserByUsername() 호출
    // -> 찾은 정보 Spring Security용 User 객체로 반환
    // -> Spring Security가 내부적으로 입력 비밀번호, DB 비밀번호 비교, 일치시 인증 성공
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles("USER")
                .build();
    }
}
