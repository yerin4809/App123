// 미션 로직(상태 업데이트)
package com.example.demo.domain.mission;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import com.example.demo.domain.emotion.EmotionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;


@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;  // 회원 미션 정보 조회 및 저장
    private final MemberRepository memberRepository;    // 회원 정보 조회
    private final EmotionService emotionService;
    private final MemberMissionStatsService statsService;   // 통계


    // 회원 ID로 미션 엔티티 찾고 없으면 새로운 미션 객체를 생성해 DB에 저장
    // memberRepository.findById(memberId) -> 회원 존재 확인
    // missionRepository.findByMember(member) -> 해당 회원 미션 조회
    // 존재하면 반환, 없으면 new Mission 생성 후 save()
    // 회원당 미션 데이터는 하나만 존재하도록 보장하는 메서드
    @Transactional
    public Mission getOrCreateMissionForMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalStateException("회원 없음"));
        return missionRepository.findByMember(member).orElseGet(() -> {
//            Mission m = new Mission();
//            m.setMember(member);
//            m.setTotalPoint(0); // 초기 포인트 0
//            return missionRepository.save(m);
//        });
            Mission newMission = Mission.builder()
                    .member(member)             // ✅ 필수: Member 객체 연결
                    // --- 나머지 필드 초기화 (NULL 방지) ---
                    .fortune(false)             // Boolean 타입 명시적 초기화
                    .water(false)               // Boolean 타입 명시적 초기화
                    .walk(0)                    // int 타입 초기화
                    .minValue(false)            // Boolean 타입 명시적 초기화
                    .qa(false)                  // Boolean 타입 명시적 초기화
                    .todayCondition("")     // String 타입 초기화
                    .purpose1(false)            // Boolean 타입 명시적 초기화
                    .purpose2(false)            // Boolean 타입 명시적 초기화
                    .totalPoint(0)              // 포인트 초기화
                    .lastUpdatedDate(LocalDate.now()) // 날짜 타입 초기화
                    .build();

            return missionRepository.save(newMission);
        });
    }

    // 미션 완료 로직
    // 1. 포춘쿠키 완료 상태 true로 업데이트
    @Transactional
    public Mission setFortuneTrue(Long memberId) {
        Mission m = getOrCreateMissionForMember(memberId);  // 미션 조회 및 생성
        if (!Boolean.TRUE.equals(m.getFortune())) {
            m.setFortune(true);   // Boolean 값 변경
            m.setTotalPoint(m.getTotalPoint() + 10);    // 미션 완료시 +15포인트
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        return missionRepository.save(m);   // DB에 반영
    }

    // 2. 물 섭취 완료 상태를 true로 업데이트
    @Transactional
    public Mission setWaterTrue(Long memberId) {
        Mission m = getOrCreateMissionForMember(memberId);  // 미션 조회 및 생성
        if (!Boolean.TRUE.equals(m.getWater())) {
            m.setWater(true);   // Boolean 값 변경
            m.setTotalPoint(m.getTotalPoint() + 15);    // 미션 완료시 +15포인트
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        return missionRepository.save(m);   // DB에 반영
    }

    // 3. 걸음수 업데이트
    @Transactional
    public Mission updateWalk(Long memberId, Integer steps) {
        Mission m = getOrCreateMissionForMember(memberId);
        if (steps >= 5000 && m.getWalk() < 5000) {
            m.setTotalPoint(m.getTotalPoint() + 20);
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        m.setWalk(steps);
        return missionRepository.save(m);
    }

    // 4. 앱 접속 미션 상태 설정
    @Transactional
    public Mission setMinTrue(Long memberId) {
        Mission m = getOrCreateMissionForMember(memberId);
        if (!Boolean.TRUE.equals(m.getMinValue())) {
            m.setMinValue(true);
            m.setTotalPoint(m.getTotalPoint() + 10);
            statsService.addMissionCount(memberId);
        }
        return missionRepository.save(m);
    }

    // 5. 오늘 감정 상태 기록
    @Transactional
    public Mission updateCondition(Long memberId, String condition) {
        Mission m = getOrCreateMissionForMember(memberId);
        // EmotionService에서 하루 1회 제한 처리
        boolean recorded = emotionService.recordEmotion(memberId, condition);

        // EmotionService에서 true 반환 시 (정상 등록일 경우만 포인트 부여)
        if (recorded) {
            m.setTodayCondition(condition);
            m.setTotalPoint(m.getTotalPoint() + 10);
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        return missionRepository.save(m);
    }

    // 6. 일일 질문 완료 시 +15포인트
    @Transactional
    public Mission setQaTrue(Long memberId) {
        Mission m = getOrCreateMissionForMember(memberId);
        if (!Boolean.TRUE.equals(m.getQa())) {
            m.setQa(true);
            m.setTotalPoint(m.getTotalPoint() + 15);
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        return missionRepository.save(m);
    }

    // 7. 사용목적1에 따른 질문 완료시 +15포인트
    @Transactional
    public Mission setPurpose1True(Long memberId) {
        Mission m = getOrCreateMissionForMember(memberId);
        if (!Boolean.TRUE.equals(m.getPurpose1())) {
            m.setPurpose1(true);
            m.setTotalPoint(m.getTotalPoint() + 15);
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        return missionRepository.save(m);
    }

    // 8. 사용목적2에 따른 질문 완료시 +15포인트
    @Transactional
    public Mission setPurpose2True(Long memberId) {
        Mission m = getOrCreateMissionForMember(memberId);
        if (!Boolean.TRUE.equals(m.getPurpose2())) {
            m.setPurpose2(true);
            m.setTotalPoint(m.getTotalPoint() + 15);
            statsService.addMissionCount(memberId);  // 통계 자동 반영
        }
        return missionRepository.save(m);
    }

    // 포인트 조회 메서드
    @Transactional(readOnly = true)
    public int getTotalPoint(Long memberId) {
        Mission m = missionRepository.findByMember_Id(memberId)
                .orElseThrow(() -> new IllegalStateException("미션 정보 없음"));
        return m.getTotalPoint();
    }

    @Transactional
    public void updateTotalPoint(Long memberId, int newPoint) {
        Mission m = missionRepository.findByMember_Id(memberId)
                .orElseThrow(() -> new IllegalStateException("미션 정보 없음"));
        m.setTotalPoint(newPoint);
        missionRepository.save(m);
    }

    /*
    @Transactional
    public void checkLevelUp(Long memberId) {
        Mission mission = missionRepository.findByMember_Id(memberId).orElseThrow();
        Member member = mission.getMember(); // Mission 엔티티에서 Member를 가져옴

        int currentPoints = mission.getTotalPoint();
        int currentLevel = member.getLevel();
        int newLevel = currentLevel;

        // 0~99: 1레벨, 100~299: 2레벨, 300~599: 3레벨 ...
        if (currentPoints >= 1000) newLevel = 5;      // 중년
        else if (currentPoints >= 600) newLevel = 4;  // 사회초년생
        else if (currentPoints >= 300) newLevel = 3;  // 청소년
        else if (currentPoints >= 100) newLevel = 2;  // 어린이
        else newLevel = 1;                            // 아기

        // 레벨이 올랐을 때만 저장 (불필요한 DB 쓰기 방지)
        if (newLevel > currentLevel) {
            member.setLevel(newLevel);
            memberRepository.save(member); // 멤버 정보 업데이트
        }
    }
     */
}
