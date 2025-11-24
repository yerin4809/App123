// 회원가입&로그인
// 사용자 인증, 데이터 관리
package com.example.demo.domain.member;

import com.example.demo.domain.emotion.MemberEmotion;
import com.example.demo.domain.emotion.MemberEmotionRepository;
import com.example.demo.domain.mission.MemberMissionStats;
import com.example.demo.domain.mission.MemberMissionStatsRepository;
import com.example.demo.domain.mission.Mission;
import com.example.demo.domain.mission.MissionRepository;
import com.example.demo.domain.shop.Shop;
import com.example.demo.domain.shop.ShopRepository;
import com.example.demo.global.security.JwtTokenProvider;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor    // final이나 @NonNull이 붙은 필드에 대해 자동 생성자 주입을 만듦
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    private final MissionRepository missionRepository;
    private final MemberMissionStatsRepository memberMissionStatsRepository;
    private final ShopRepository shopRepository;
    private final MemberEmotionRepository memberEmotionRepository;

    // 헬퍼 메서드: MemberMissionStats 초기화
    private MemberMissionStats createInitialStats(Member member) {
        List<Integer> days = new ArrayList<>(Collections.nCopies(31, 0));
        List<Integer> months = new ArrayList<>(Collections.nCopies(12, 0));

        return MemberMissionStats.builder()
                .member(member)
                .mon(0).tue(0).wed(0).thu(0).fri(0).sat(0).sun(0)
                .dateCounts(days)
                .monthlyCounts(months)
                .build();
    }

    // 헬퍼 메서드: MemberEmotion 초기화
    private MemberEmotion createInitialMemberEmotion(Member member) {
        return MemberEmotion.builder()
                .member(member)
                .happyCount(0).joyCount(0).normalCount(0).tiredCount(0).sadCount(0).angryCount(0)
                .build();
    }

    // 회원가입(register)
    // 새 사용자 DB에 저장(비밀번호는 암호화 후 저장)
    // 1.중복 이메일 검사
    // findByEmail(email)로 DB 조회 -> 값 존재시 예외 발생(IllegalStateException)
    // 2. 비밀번호 암호화
    // 입력받은 비밀번호(rawPassword)를 passwordEncoder.encode()로 암호화
    // 해시된 문자열로 DB에 저장(복호화 불가)
    // 3. 회원 정보 저장
    // 새로운 Member 객체 생성해 입력값 설정
    @Transactional
    public Member register(String email, String rawPassword, String username, String purpose, String characterName) {
        memberRepository.findByEmail(email).ifPresent(m -> { throw new IllegalStateException("이미 존재하는 이메일입니다."); });
        String encoded = passwordEncoder.encode(rawPassword);
        Member member = new Member();
        member.setEmail(email);
        member.setPassword(encoded);
        member.setUsername(username);
        member.setPurpose(purpose);
        member.setCharacterName(characterName);
        // Member를 먼저 저장하여 ID를 얻습니다. (FK 연결을 위해 필수)
        member = memberRepository.save(member);


        // 2. Builder를 통한 필수 상태 엔티티 초기화 및 저장
        // Mission 엔티티 초기화
        Mission initialMission = Mission.builder()
                .member(member)
                .fortune(false).water(false).walk(0).minValue(false).qa(false)
                .todayCondition("")
                .purpose1(false).purpose2(false)
                .totalPoint(0) // 초기 포인트는 0으로 설정
                .lastUpdatedDate(LocalDate.now())
                .build();

        missionRepository.save(initialMission);


        // MemberMissionStats 엔티티 초기화
        MemberMissionStats initialStats = createInitialStats(member);
        memberMissionStatsRepository.save(initialStats);


        // Shop 엔티티 초기화
        Shop initialShop = Shop.builder()
                .member(member)
                .defaultTheme(true)
                .sunset(false).forest(false).ocean(false).lavender(false)
                .cherry(false).night(false).autumn(false) // 모든 테마 false 초기화
                .build();

        shopRepository.save(initialShop);


        // MemberEmotion 엔티티 초기화
        MemberEmotion initialEmotion = createInitialMemberEmotion(member);
        memberEmotionRepository.save(initialEmotion);


        return member;
    }

    // 로그인(login)
    // 이메일로 회원 조회, 비밀번호 암호화 비교. 성공시 JWT 토큰 발급
    // findByEmail(email)로 회원 존재 여부 확인 -> 없으면 예외 발생
    // passwordEncoder.matches(rawPassword, member.getPassword())
    // -> DB에 저장된 암호화된 비밀번호와 입력 비밀번호를 비교
    public String login(String email, String rawPassword, JwtTokenProvider jwt) {
        Member member = memberRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("존재하지 않는 이메일입니다."));
        if (!passwordEncoder.matches(rawPassword, member.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");    // 불일치 예외
        }
        return jwt.createToken(member.getEmail());  // 로그인 성공시 호출(이후 API 요청 시 인증에 사용)
    }

    // 단순 회원 조회(findByEmail)
    // 이메일로 사용자 정보 가져오기(이메일이 존재하지 않으면 예외 발생)
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new IllegalStateException("사용자를 찾을 수 없습니다."));
    }

}
