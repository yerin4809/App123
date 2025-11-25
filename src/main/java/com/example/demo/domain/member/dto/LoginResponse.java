// 로그인 응답(JWT 토큰)
// 로그인 성공 시 클라이언트에게 전달할 응답 객체
package com.example.demo.domain.member.dto;

import lombok.*;

@Getter // private String token 필드에 대해 자동으로 getToken() 메서드 만듦
@AllArgsConstructor // 클래스에 모든 필드를 매개변수로 받는 생성자 자동 생성
public class LoginResponse {
    private String token;   // 로그인 성공 후 생성된 JWT 토큰 문자열 담는 변수
                            // 클라이언트가 이 토큰을 받고 이후 요청시 Authorization 헤더에 보냄
}
