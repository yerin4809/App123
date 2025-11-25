// 하루 1회 감정 입력 제한 기능 지원
package com.example.demo.domain.emotion;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;

public interface EmotionLogRepository extends JpaRepository<EmotionLog, Long> {
    boolean existsByMemberIdAndEmotionDate(Long memberId, LocalDate date);
}