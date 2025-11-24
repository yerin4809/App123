// 통계 조회 API
package com.example.demo.domain.mission;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/stats")
public class MissionStatsController {

    private final MemberMissionStatsRepository statsRepository;

    @GetMapping("/{memberId}")
    public MemberMissionStats getStats(@PathVariable Long memberId) {
        return statsRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("통계 없음"));
    }
}