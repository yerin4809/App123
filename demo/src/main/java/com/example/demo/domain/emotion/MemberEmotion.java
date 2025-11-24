// 회원별 누적 감정 카운트 저장 엔티티
package com.example.demo.domain.emotion;

import com.example.demo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Table(name = "member_emotion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberEmotion {
    // 회원당 하나의 레코드만 존재
    // 각 감정의 누적 카운트(초깃값 0, 매일 감정 선택시 해당 감정 컬럼만 +1)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 1:1 관계
    @OneToOne
    @JoinColumn(name = "member_id", unique = true)
    private Member member;

    private int happyCount = 0;
    private int joyCount = 0;
    private int normalCount = 0;
    private int tiredCount = 0;
    private int sadCount = 0;
    private int angryCount = 0;
}
