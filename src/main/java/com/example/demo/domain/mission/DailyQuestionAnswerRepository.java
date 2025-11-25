package com.example.demo.domain.mission;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface DailyQuestionAnswerRepository extends JpaRepository<DailyQuestionAnswer, Long> {

    DailyQuestionAnswer findByMemberIdAndCreatedDate(Long memberId, LocalDate createdDate);
}
