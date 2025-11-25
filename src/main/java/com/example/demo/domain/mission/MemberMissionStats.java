package com.example.demo.domain.mission;

import com.example.demo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MemberMissionStats {

    @Id
    private Long memberId;  // PK 각 회원 1개

    @OneToOne
    @MapsId   // PK = Member PK
    @JoinColumn(name = "member_id")
    private Member member;

    // 주간(월~일)
    private int mon;
    private int tue;
    private int wed;
    private int thu;
    private int fri;
    private int sat;
    private int sun;

    // 월간 (1일~말일까지)
    @ElementCollection
    private List<Integer> dateCounts = new ArrayList<>();

    // 연간 (1월~12월)
    @ElementCollection
    private List<Integer> monthlyCounts = new ArrayList<>();

    // 누적 완료 횟수(레벨업 용)
    private int totalMissionsCompleted = 0;
}

