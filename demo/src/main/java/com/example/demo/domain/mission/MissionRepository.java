// 미션 조회 저장을 위한 JPA 리포지토리
// MissionService에서 사용
package com.example.demo.domain.mission;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.domain.member.Member;

// JpaRepository(현재 Repository가 관리하는 엔티티 클래스, 엔티티의 기본 키 타입)
public interface MissionRepository extends JpaRepository<Mission, Long> {
    // 특정 회원의 미션을 찾아주는 커스텀 조회 메서드
    Optional<Mission> findByMember(Member member);

    Optional<Mission> findByMember_Id(Long memberId);
}