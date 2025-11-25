// REST API(회원가입/로그인)
// 클라이언트와 백엔드 서버 사이의 통신 담당(요청 응답)
// 클라이언트 로그인 요청 -> MemberService 호출
// -> 인증 성공시 LoginResponse(token) 반환
package com.example.demo.domain.member;

import com.example.demo.domain.member.dto.MemberDto;
import com.example.demo.domain.member.dto.LoginResponse;
import com.example.demo.global.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    // 회원가입 요청 응답
    // JSON 데이터를 @RequestBody로 받아서 MemberDto 객체에 매핑
    // memberService.register() 호출 -> 실제 DB 저장 로직 수행(이메일 중복, 비밀번호 암호화 확인, DB에 저장)
    // 성공시 HTTP 200 응답("회원가입 성공" 메시지) 반환
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MemberDto dto) {
        memberService.register(dto.getEmail(), dto.getPassword(), dto.getUsername(), dto.getPurpose(), dto.getCharacterName());
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인 요청 응답
    // JSON 데이터를 @RequestBody로 받아서 MemberDto 객체에 매핑
    // memberService.login() 호출 -> 이메일로 사용자 검색, 비밀번호 검증, 성공시 JWT 토큰 생성)
    // 생성된 토큰 LoginResponse(token) 객체에 담아 반환
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody MemberDto dto) {
        String token = memberService.login(dto.getEmail(), dto.getPassword(), jwtTokenProvider);
        return ResponseEntity.ok(new LoginResponse(token));
    }
}
