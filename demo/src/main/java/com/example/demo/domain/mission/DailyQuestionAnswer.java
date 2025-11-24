package com.example.demo.domain.mission;

import com.example.demo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "daily_question_answer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DailyQuestionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 회원이 답변했는지
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;  // FK 연결

    // 프론트에서 주는 오늘의 질문 텍스트
    @Column(nullable = false)
    private String questionText;

    // 사용자 답변
    @Column(length = 50)
    private String answer;

    // 오늘 날짜 (YYYY-MM-DD, 하루 하나만 작성)
    @Column(nullable = false)
    private LocalDate createdDate;              // 최초 답변 날짜

    // 마지막 수정 날짜 기록
    private LocalDate updatedDate;

    // 오늘 답변 완료 여부
    private Boolean isAnswered = false;
}
