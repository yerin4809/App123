package com.example.demo.domain.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/daily-question")
@RequiredArgsConstructor
public class DailyQuestionAnswerController {

    private final DailyQuestionAnswerService dailyQuestionAnswerService;

    // 오늘의 답변 저장 및 수정
    // 프론트에서 question + answer를 보내면 저장

    @PostMapping("/save")
    public ResponseEntity<String> saveAnswer(
            @RequestParam Long memberId,
            @RequestParam String question,
            @RequestParam String answer
    ) {
        String result = dailyQuestionAnswerService.saveDailyAnswer(memberId, question, answer);
        return ResponseEntity.ok(result);
    }

    // 오늘 답변 조회(프론트에서 오늘 답변 있는지 확인 용도)
    @GetMapping("/today")
    public ResponseEntity<DailyQuestionAnswer> getTodayAnswer(@RequestParam Long memberId) {
        DailyQuestionAnswer answer = dailyQuestionAnswerService.getTodayAnswer(memberId);
        return ResponseEntity.ok(answer);
    }

    // 답변 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<DailyQuestionAnswer> updateAnswer(
            @PathVariable Long id,
            @RequestParam String answer
    ) {
        DailyQuestionAnswer updated = dailyQuestionAnswerService.updateAnswer(id, answer);
        return ResponseEntity.ok(updated);
    }
}
