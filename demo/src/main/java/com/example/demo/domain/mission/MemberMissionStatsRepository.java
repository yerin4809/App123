package com.example.demo.domain.mission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberMissionStatsRepository extends JpaRepository<MemberMissionStats, Long> {
    Optional<MemberMissionStats> findByMemberId(Long memberId);
}