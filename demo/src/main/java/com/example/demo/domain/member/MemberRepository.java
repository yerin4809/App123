// 멤버 조회 저장을 위한 리포지토리
// 회원 데이터를 DB와 연결해주는 핵심 JPA 리포지토리 클래스
// Service에서 DI 받아 회원 조회/저장/유효성 검사에 사용(데이터를 읽고 쓰는 역할만 담당)
package com.example.demo.domain.member;

import java.util.Optional;  // Null 안전 클래스
import org.springframework.data.jpa.repository.JpaRepository;   //CRUD 기능


// 인터페이스 선언
// JpaRepository 상속하면 save(entity) - 엔티티 저장, findById(id) - 기본키로 조회,
// findAll() - 전체 조회, deleteById(id) - 삭제, count() - 데이터 개수 확인
// existsById(Long id) - 존재 여부 확인 기능이 자동 생성됨
// -> Member 엔티티를 DB에서 쉽게 다룰 수 있게 됨
// Long은 Member의 PK 타입으로 지정
public interface MemberRepository extends JpaRepository<Member, Long> {
    // 직접 정의한 조회 메서드
    // Spring Data JPA의 메서드 이름 규칙 기능을 이용해 자동으로 쿼리 생성
    // -> Spring이 findByEmail이란 이름을 보면 자동으로 SQL 문을 생성해줌
    Optional<Member> findByEmail(String email);
}
