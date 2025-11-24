// 미션 관련 API
package com.example.demo.domain.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mission") // 경로 설정
@RequiredArgsConstructor
public class MissionController {
    // 밑에 있는 모든 API는 내부적으로 MissionService를 호출함
    private final MissionService missionService;

    /* [보안 개선 사항]
     현재 개발 편의성과 마감 기한 준수를 위해 클라이언트로부터 memberId를 직접 전달받고 있음
     실제 운영 서비스에서는 보안 취약점(ID 변조 등)을 방지하기 위해,
     JWT 토큰(Authentication)에서 사용자 ID를 직접 추출하여 본인 확인 과정을 거치도록 개선해야 함
     */

    // 포춘쿠키 뽑기 미션 완료 처리 API
    @PostMapping("/fortune")
    public ResponseEntity<String> fortune(@RequestParam Long memberId) {
        missionService.setFortuneTrue(memberId);
        return ResponseEntity.ok("포춘쿠키 뽑기 완료");
    }

    // 물 미션 완료 처리 API
    // @RequestParam Long memberId -> URL의 ?memberId=1 값으로 전달
    // missionService.setWaterTrue() -> 해당하는 회원의 미션을 가져와 true로 변경 및 DB에 저장
    @PostMapping("/water")
    public ResponseEntity<String> water(@RequestParam Long memberId) {
        missionService.setWaterTrue(memberId);
        return ResponseEntity.ok("물 마시기 완료");
    }

    // 걸음 수 기록 API
    // 사용자의 걸음 수(steps)를 DB에 업뎃.
    // steps=1234 전달하면 해당 회원의 walk 필드가 1234로 바뀜
    // -> /api/mission/walk?memberId=1&steps=1234
    @PostMapping("/walk")
    public ResponseEntity<String> walk(@RequestParam Long memberId, @RequestParam Integer steps) {
        missionService.updateWalk(memberId, steps);
        return ResponseEntity.ok("걸음수 업데이트");
    }

    // 앱 접속 기록 API
    // 사용자가 앱을 하루에 한 번이라도 실행하면 호출
    @PostMapping("/min") // typo fixed in actual file
    public ResponseEntity<String> setMin(@RequestParam Long memberId) {
        missionService.setMinTrue(memberId);
        return ResponseEntity.ok("앱 접속 처리 완료");
    }

    // 오늘의 기분 저장 API
    @PostMapping("/condition")
    public ResponseEntity<String> condition(@RequestParam Long memberId, @RequestParam String condition) {
        missionService.updateCondition(memberId, condition);
        return ResponseEntity.ok("오늘의 기분 저장 완료");
    }

    // 사용목적1 미션 완료 처리 API
    @PostMapping("/Purpose1")
    public ResponseEntity<String> purpose1(@RequestParam Long memberId) {
        missionService.setPurpose1True(memberId);
        return ResponseEntity.ok("사용목적1 미션 완료");
    }

    // 사용목적2 미션 완료 처리 API
    @PostMapping("/Purpose2")
    public ResponseEntity<String> purpose2(@RequestParam Long memberId) {
        missionService.setPurpose2True(memberId);
        return ResponseEntity.ok("사용목적2 미션 완료");
    }
}
