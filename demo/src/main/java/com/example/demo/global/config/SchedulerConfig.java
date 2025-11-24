// 스케줄러
// 미션 상태 초기화
package com.example.demo.global.config;

import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.demo.domain.mission.MissionRepository;
import lombok.RequiredArgsConstructor;

@Component  // 스프링이 클래스를 자동으로 Bean으로 등록해 실행하게 함
@EnableScheduling   // 스케줄러 기능 활성화(Spring이 정해진 시간마다 실행하도록 설정)
@RequiredArgsConstructor    // final 필드 자동 생성자 주입
public class SchedulerConfig {
    private final MissionRepository missionRepository;


    // 매일 자정 미션 리셋
    // 특정 주기(cron 표현식)에 맞춰 메서드를 자동 실행
    // 매일 0시 0분 0초(자정) 실행(한국 시간 기준)
    @Scheduled(cron = "0 0 0 * * *", zone = "Asia/Seoul")
    @Transactional  // 데이터 변경 작업을 트랜잭션 단위로 안전하게 처리
    public void resetDailyMissions() {
        // 모든 회원의 미션 데이터 조회 -> findAll()
        // 각 미션 상태 초기 상태로 되돌림 -> forEach(m -> {})
        missionRepository.findAll().forEach(m -> {
            m.setFortune(false);
            m.setWater(false);
            m.setMinValue(false);
            m.setQa(false);
            m.setWalk(0);
            m.setPurpose1(false);
            m.setPurpose2(false);
        });
        missionRepository.flush();  // 변경 내용 DB에 즉시 반영
    }
}
