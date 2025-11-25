// 회원 엔티티
// 회원(Member) 정보를 DB에 저장하고 관리하기 위한 JPA 엔티티 클래스
package com.example.demo.domain.member;

import jakarta.persistence.*;
import lombok.*;

// 클래스가 DB 테이블과 매핑되는 엔티티임을 표시
@Entity
// 엔티티가 매핑될 실제 테이블 이름 명시
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
public class Member {

    // 테이블의 기본 키(PK)로 설정된 필드 지정(회원을 고유하게 식별하기 위해 필요)
    @Id
    // id 값이 자동으로 생성되도록 설정
    // MySQL의 AUTO_INCREMENT와 같은 기능. 새로 저장할 때마다 +1씩 증가
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 회원 정보 필드들
    
    // 반드시 값이 있어야 하고(null 금지) 중복되면 안되는(unique) 속성을 가짐
    // -> 같은 이메일로 회원가입x
    @Column(nullable = false, unique = true)
    private String email;

    // 비밀번호, 반드시 입력 되어야 함
    @Column(nullable = false)
    private String password;

    // 사용자 이름 저장
    @Column(nullable = false)
    private String username;

    // 사용 목적 저장(사용자 정의 필드)
    @Column(nullable = false)
    private String purpose;

    // 캐릭터
    @Column(nullable = false)
    private String characterName;

    // 캐릭터 레벨 (기본값 1)
    @Column(nullable = false)
    private int level = 1;
}

