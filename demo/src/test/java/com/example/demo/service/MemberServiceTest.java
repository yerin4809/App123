package com.example.demo.service;


import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import com.example.demo.domain.member.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional  // 테스트 후 자동 롤백 (DB 깨끗하게 유지)

public class MemberServiceTest {
    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("회원 데이터 추가 테스트")
    void testSaveMember() {
        Member member = new Member();   // 생성하기위해
        member.setEmail("test@test.com");
        member.setPassword("test1234");
        member.setUsername("강아지");
        memberRepository.save(member);
    }
}
