// 감정 누적 데이터 저장/조회용 저장소
package com.example.demo.domain.emotion;

import com.example.demo.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberEmotionRepository extends JpaRepository<MemberEmotion, Long> {
    Optional<MemberEmotion> findByMember(Member member);
}
