package com.example.demo.domain.emotion;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/member-emotion")
@RequiredArgsConstructor
public class MemberEmotionController {

    private final EmotionService emotionService;

    // [Note] 추후 JWT 토큰 기반 인증 방식으로 변경하여, 타인의 감정 기록을 수정할 수 없도록 보완 필요

    // 감정 입력 (하루 1회 제한)
    @PostMapping("/record")
    public ResponseEntity<String> recordEmotion(
            @RequestParam Long memberId,
            @RequestParam String emotionType
    ) {
        boolean success = emotionService.recordEmotion(memberId, emotionType);
        if (success) {
            return ResponseEntity.ok("오늘의 감정 기록 완료!");
        } else {
            return ResponseEntity.badRequest().body("오늘 이미 감정을 기록했습니다.");
        }
    }

    // 감정 누적 카운트 조회
    @GetMapping("/get")
    public ResponseEntity<MemberEmotion> getMemberEmotion(@RequestParam Long memberId) {
        MemberEmotion memberEmotion = emotionService.getMemberEmotion(memberId);
        return ResponseEntity.ok(memberEmotion);
    }

    // 감정 요약 (그래프용 JSON)
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Integer>> getEmotionSummary(@RequestParam Long memberId) {
        Map<String, Integer> summary = emotionService.getEmotionSummary(memberId);
        return ResponseEntity.ok(summary);
    }
}
