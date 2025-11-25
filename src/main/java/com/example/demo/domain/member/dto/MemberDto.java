// 회원가입/로그인 요청 데이터를 전송하기 위한 기본적인 구조
package com.example.demo.domain.member.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberDto {
    private String email;
    private String password;
    private String username;
    private String purpose;
    private String characterName;
}
