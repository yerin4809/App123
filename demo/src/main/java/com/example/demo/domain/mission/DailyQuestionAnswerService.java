package com.example.demo.domain.mission;

import com.example.demo.domain.member.Member;

import com.example.demo.domain.member.MemberRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class DailyQuestionAnswerService {

    private final DailyQuestionAnswerRepository answerRepository;
    private final MemberRepository memberRepository;
    // private final MissionRepository missionRepository;
    private final MissionService missionService;

    // 오늘의 질문 세팅 및 저장(최초 생성)
    @Transactional
    public String saveDailyAnswer(Long memberId, String question, String answer) {

        // 회원 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        LocalDate today = LocalDate.now();

        // 이미 오늘 답변이 있으면 수정 처리
        DailyQuestionAnswer existing = answerRepository
                .findByMemberIdAndCreatedDate(memberId, today);

        // 오늘 처음 작성하면 새 답변 생성
        if (existing == null) {
            DailyQuestionAnswer newAnswer = new DailyQuestionAnswer();
            newAnswer.setMember(member);
            newAnswer.setQuestionText(question);
            newAnswer.setAnswer(answer);
            newAnswer.setCreatedDate(today);
            newAnswer.setIsAnswered(true);

            answerRepository.save(newAnswer);

        } else {
            // 기존 답변 수정 기능
            existing.setAnswer(answer);
            existing.setUpdatedDate(today); // 수정 날짜
            answerRepository.save(existing);
        }

        /*
        // Mission 테이블의 qa(일일 질문 완료) true로 변경 + 포인트 15점
        Mission mission = missionRepository.findByMember(member)
                .orElseThrow(() -> new RuntimeException("Mission 없음"));

        if (!mission.getQa()) {
            mission.setQa(true);
            mission.setTotalPoint(mission.getTotalPoint() + 15); // 포인트 추가
            missionRepository.save(mission);
        }
         */

        // Mission 테이블 qa 호출(내부적으로 자동 처리)
        missionService.setQaTrue(memberId);

        return "일일 질문 저장 및 미션 완료 처리됨";
    }

    // 오늘 답변 조회
    public DailyQuestionAnswer getTodayAnswer(Long memberId) {
        LocalDate today = LocalDate.now();
        return answerRepository.findByMemberIdAndCreatedDate(memberId, today);
    }

    // 특정 답변 수정 (id 기반)
    @Transactional
    public DailyQuestionAnswer updateAnswer(Long id, String answer) {
        DailyQuestionAnswer existing = answerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("답변 없음"));
        existing.setAnswer(answer);
        existing.setUpdatedDate(LocalDate.now());
        return answerRepository.save(existing);
    }
}