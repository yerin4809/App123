package com.example.demo.domain.mission;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemberMissionStatsService {

    private final MemberMissionStatsRepository statsRepository;
    private final MemberRepository memberRepository;

    @Transactional
    // 회원 가입 시 Stats 생성
    public MemberMissionStats createInitialStats(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalStateException("회원 ID를 찾을 수 없습니다: " + memberId));

        // Builder 패턴을 사용하여 모든 필드를 명시적으로 초기화
        MemberMissionStats stats = MemberMissionStats.builder()
                .member(member)

                // 통계 초기값(int 타입은 기본값 0이므로 생략 가능하나, 명시적으로 나열)
                .mon(0).tue(0).wed(0).thu(0).fri(0).sat(0).sun(0)

                // List 필드 초기화
                .dateCounts(new ArrayList<>(Collections.nCopies(31, 0)))
                .monthlyCounts(new ArrayList<>(Collections.nCopies(12, 0)))

                .build();

        return statsRepository.save(stats);
    }

    // 미션 완료 시 카운트 증가조회 시 데이터가 없으면 자동 생성하는 안전 로직 적용
    @Transactional
    public void addMissionCount(Long memberId) {

        // MissionStats를 조회하고, 없으면 새로 생성하여 반환합니다. (orElseGet 사용)
        MemberMissionStats stats = statsRepository.findById(memberId)
                .orElseGet(() -> createInitialStats(memberId)); // 데이터가 없으면 초기화 메서드 호출

        // 시간 기반 데이터 업데이트 로직
        LocalDate today = LocalDate.now();
        DayOfWeek day = today.getDayOfWeek();

        // 주간 반영
        switch (day) {
            case MONDAY -> stats.setMon(stats.getMon() + 1);
            case TUESDAY -> stats.setTue(stats.getTue() + 1);
            case WEDNESDAY -> stats.setWed(stats.getWed() + 1);
            case THURSDAY -> stats.setThu(stats.getThu() + 1);
            case FRIDAY -> stats.setFri(stats.getFri() + 1);
            case SATURDAY -> stats.setSat(stats.getSat() + 1);
            case SUNDAY -> stats.setSun(stats.getSun() + 1);
        }

        // 월간 반영
        int dayIndex = today.getDayOfMonth() - 1;
        // List 범위 체크 (31일보다 짧은 달이나 엔티티 초기화 문제 대비)
        if (dayIndex >= 0 && dayIndex < stats.getDateCounts().size()) {
            stats.getDateCounts().set(dayIndex, stats.getDateCounts().get(dayIndex) + 1);
        }

        // 연간 반영
        int monthIndex = today.getMonthValue() - 1;
        stats.getMonthlyCounts().set(monthIndex,
                stats.getMonthlyCounts().get(monthIndex) + 1
        );

        // 누적 미션 완료 횟수 증가
        stats.setTotalMissionsCompleted(stats.getTotalMissionsCompleted() + 1);

        statsRepository.save(stats);

        // 레벨업 검사 로직 호출
        checkLevelUp(stats.getMember().getId()); // memberId만 전달하여 레벨업 확인
    }

    // 완료 횟수 기준 정의(key: 현재 레벨, value: 다음 레벨 달성에 필요한 횟수)
    private static final Map<Integer, Integer> RELATIVE_LEVEL_REQUIREMENTS = Map.of(
            1, 500,      // Lv 1 -> Lv 2: 500회 완료
            2, 1500,         // Lv 2 -> Lv 3: 1500회 완료
            3, 2000,         // Lv 3 -> Lv 4: 2000회 완료
            4, 2500             // Lv 4 -> Lv 5: 2500회 완료
                                // Lv 5는 최대 레벨로 가정
    );

    // 레벨 체크
    @Transactional
    public void checkLevelUp(Long memberId) {
        // MemberMissionStats에서 완료 횟수와 Member 정보를 가져옴
        MemberMissionStats stats = statsRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Stats 정보가 존재하지 않습니다."));

        Member member = stats.getMember();

        // 레벨업 이후 0부터 다시 셈
        int currentCompletedCount = stats.getTotalMissionsCompleted();
        int currentLevel = member.getLevel();

        // 다음 레벨업에 필요한 횟수를 조회(현재 레벨을 기준으로 다음 목표를 찾음)
        Integer requiredRelativeCount = RELATIVE_LEVEL_REQUIREMENTS.get(currentLevel);

        // 다음 레벨 기준이 존재하고(최대 레벨 아닐 때),
        // 현재 완료 횟수가 다음 레벨 기준을 충족하면 레벨업
        if (requiredRelativeCount != null && currentCompletedCount >= requiredRelativeCount) {

            int newLevel = currentLevel + 1;

            // 레벨 업데이트
            member.setLevel(newLevel);
            memberRepository.save(member);

            // 누적 완료 횟수 초기화 및 저장
            // 다음 레벨까지의 카운트를 0부터 다시 시작
            stats.setTotalMissionsCompleted(0);
            statsRepository.save(stats);

            // System.out.println( + newLevel + "로 레벨업");
        }
    }
}
