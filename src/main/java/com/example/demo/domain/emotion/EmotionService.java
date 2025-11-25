package com.example.demo.domain.emotion;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmotionService {

    private final MemberRepository memberRepository;
    private final MemberEmotionRepository memberEmotionRepository;
    private final EmotionLogRepository emotionLogRepository;

    // MemberEmotion 객체를 Builder를 사용하여 생성하고 초기화하는 헬퍼 메서드
    private MemberEmotion createInitialMemberEmotion(Member member) {
        // Builder 패턴을 사용하여 모든 필드를 명시적으로 초기화(NULL 방지)
        return MemberEmotion.builder()
                .member(member)
                // 모든 카운트 필드를 0으로 명시적으로 초기화
                .happyCount(0)
                .joyCount(0)
                .normalCount(0)
                .tiredCount(0)
                .sadCount(0)
                .angryCount(0)
                .build();
    }

    /*
    감정 기록 (하루 1회만 가능)
    @param memberId 회원 ID
    @param emotionType 선택한 감정 (행복, 기쁨, 보통, 피곤, 우울, 짜증)
    @return true: 기록 성공 / false: 이미 오늘 기록됨
     */
    @Transactional
    public boolean recordEmotion(Long memberId, String emotionType) {

        // 회원 존재 여부 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        // 오늘 이미 감정 기록 여부 확인
        boolean alreadyRecorded = emotionLogRepository.existsByMemberIdAndEmotionDate(memberId, LocalDate.now());
        if (alreadyRecorded) {
            return false; // 하루 1회 제한
        }

        // EmotionLog에 오늘 감정 기록 저장
        EmotionLog log = new EmotionLog();
        log.setMember(member);
        log.setEmotionType(emotionType);
        log.setEmotionDate(LocalDate.now());
        emotionLogRepository.save(log);

        // MemberEmotion(누적 감정) 조회 또는 새로 생성
        MemberEmotion emotion = memberEmotionRepository.findByMember(member)
                .orElseGet(() -> {
                    MemberEmotion newEmotion = createInitialMemberEmotion(member);
                    return memberEmotionRepository.save(newEmotion);
                });

        // 감정 종류별 카운트 +1
        switch (emotionType) {
            case "행복" -> emotion.setHappyCount(emotion.getHappyCount() + 1);
            case "기쁨" -> emotion.setJoyCount(emotion.getJoyCount() + 1);
            case "보통" -> emotion.setNormalCount(emotion.getNormalCount() + 1);
            case "피곤" -> emotion.setTiredCount(emotion.getTiredCount() + 1);
            case "슬픔" -> emotion.setSadCount(emotion.getSadCount() + 1);
            case "화남" -> emotion.setAngryCount(emotion.getAngryCount() + 1);
            default -> throw new IllegalArgumentException("유효하지 않은 감정 타입: " + emotionType);
        }

        memberEmotionRepository.save(emotion);

        return true; // 정상적으로 감정 기록 완료
    }

    // 회원 감정 누적 데이터 조회
    @Transactional(readOnly = true)
    public MemberEmotion getMemberEmotion(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원이 존재하지 않습니다."));

        return memberEmotionRepository.findByMember(member)
                .orElseGet(() -> {
                    return createInitialMemberEmotion(member);
                });
    }

    // 회원 감정 요약 (그래프용 Map)
    @Transactional(readOnly = true)
    public Map<String, Integer> getEmotionSummary(Long memberId) {
        MemberEmotion emotion = getMemberEmotion(memberId);
        Map<String, Integer> summary = new HashMap<>();
        summary.put("행복", emotion.getHappyCount());
        summary.put("기쁨", emotion.getJoyCount());
        summary.put("보통", emotion.getNormalCount());
        summary.put("피곤", emotion.getTiredCount());
        summary.put("슬픔", emotion.getSadCount());
        summary.put("화남", emotion.getAngryCount());
        return summary;
    }
}
