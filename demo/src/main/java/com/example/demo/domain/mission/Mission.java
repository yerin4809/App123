// 미션 엔티티
// 하루 미션을 관리하는 데이터 구조
package com.example.demo.domain.mission;

import com.example.demo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Builder
@Entity
@Table(name = "mission")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Mission {
    // num필드(PK)
    // 새로운 미션이 추가될 때마다 1씩 증가
    // 각 레코드 고유하게 구분
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long num;

    // 회원과의 연관관계
    // 미션은 어떤 회원이 수행한 미션인지 나타내야 함 -> Member 엔티티와 N:1관계로 연결
    @ManyToOne(fetch = FetchType.LAZY)  // 한 회원이 여러 개의 미션을 가질 수 있음(지연 로딩 방식 사용.
                                        // 필요할 때만 미션 불러옴)
    @JoinColumn(name = "member_id")     // mission 테이블에 member_id라는 외래키(FK) 컬럼 만들어줌
    private Member member;  // FK 연결

    // 미션 데이터 필드들
    private Boolean fortune = false;        // 포춘쿠키
    private Boolean water = false;          // 물 1L 섭취 여부
    private int walk = 0;                   // 걸음수(만보기)
    private Boolean minValue = false;       // 앱 접속(타임)
    private Boolean qa = false;             // 일일 질문 완료 여부
    private String todayCondition;          // 오늘의 감정(행복, 기쁨, 보통, 피곤, 우울)
    private Boolean purpose1 = false;       // 사용목적에 따른 질문 완료 여부1
    private Boolean purpose2 = false;       // 사용목적에 따른 질문 완료 여부2
    private int totalPoint = 0;             // 총 포인트
    private LocalDate lastUpdatedDate;      // 자정 초기화
}