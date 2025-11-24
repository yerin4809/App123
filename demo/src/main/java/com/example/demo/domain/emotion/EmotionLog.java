// 오늘 감정 입력 여부(1일 1입력 확인용)
package com.example.demo.domain.emotion;

import com.example.demo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "emotion_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmotionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 감정을 입력한 회원
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 감정 종류
    private String emotionType;

    // 감정을 기록한 날짜(중복 방지용)
    private LocalDate emotionDate;
}