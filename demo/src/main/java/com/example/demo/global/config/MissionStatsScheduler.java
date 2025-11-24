package com.example.demo.global.config;

import com.example.demo.domain.mission.MemberMissionStats;
import com.example.demo.domain.mission.MemberMissionStatsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class MissionStatsScheduler {

    private final MemberMissionStatsRepository statsRepository;

    // 매주 월요일 00:00 주간 초기화
    @Scheduled(cron = "0 0 0 * * MON", zone = "Asia/Seoul")
    public void resetWeeklyStats() {
        List<MemberMissionStats> list = statsRepository.findAll();

        for (MemberMissionStats s : list) {
            s.setMon(0);
            s.setTue(0);
            s.setWed(0);
            s.setThu(0);
            s.setFri(0);
            s.setSat(0);
            s.setSun(0);
        }

        statsRepository.saveAll(list);
        System.out.println("Weekly mission stats reset (월요일 00:00)");
    }

    // 매월 1일 00:00 월간 초기화
    @Scheduled(cron = "0 0 0 1 * *", zone = "Asia/Seoul")
    public void resetMonthlyStats() {
        List<MemberMissionStats> list = statsRepository.findAll();

        for (MemberMissionStats s : list) {
            for (int i = 0; i < s.getDateCounts().size(); i++) {
                s.getDateCounts().set(i, 0);
            }
        }

        statsRepository.saveAll(list);
        System.out.println("Monthly mission stats reset (1일 00:00)");
    }
}

