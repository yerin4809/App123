package com.example.demo.domain.shop;

import com.example.demo.domain.member.Member;
import com.example.demo.domain.mission.Mission;
import com.example.demo.domain.member.MemberRepository;
import com.example.demo.domain.mission.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ShopService {

    private final ShopRepository shopRepository;
    private final MissionRepository missionRepository;
    private final MemberRepository memberRepository;

    // 테마 가격표
    private static final Map<String, Integer> THEME_PRICES = Map.of(
            "sunset", 500,
            "forest", 800,
            "ocean", 700,
            "lavender", 600,
            "cherry", 900,
            "night", 1000,
            "autumn", 750
    );

    // 테마 필드를 true로 설정하는 매핑
    private static final Map<String, Consumer<Shop>> THEME_SETTERS = Map.of(
            "sunset", shop -> shop.setSunset(true),
            "forest", shop -> shop.setForest(true),
            "ocean", shop -> shop.setOcean(true),
            "lavender", shop -> shop.setLavender(true),
            "cherry", shop -> shop.setCherry(true),
            "night", shop -> shop.setNight(true),
            "autumn", shop -> shop.setAutumn(true)
    );

    // 이미 구매한 테마 확인 매핑
    private static final Map<String, java.util.function.Function<Shop, Boolean>> THEME_CHECKERS = Map.of(
            "sunset", Shop::isSunset,
            "forest", Shop::isForest,
            "ocean", Shop::isOcean,
            "lavender", Shop::isLavender,
            "cherry", Shop::isCherry,
            "night", Shop::isNight,
            "autumn", Shop::isAutumn
    );


    // 상점 조회 또는 생성
    @Transactional
    public Shop getOrCreateShop(Long memberId) {

        return shopRepository.findById(memberId).orElseGet(() -> {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

//            Shop shop = new Shop();
//            shop.setMemberId(memberId);
//            shop.setMember(member);
//            shop.setDefaultTheme(true);

            Shop shop = Shop.builder()
                    .member(member)             // Member 객체 연결 (FK)
                    .defaultTheme(true)         // 기본 테마 True로 설정
                    // 나머지 테마 필드를 모두 false로 명시적으로 초기화 (NULL 방지)
                    .sunset(false)
                    .forest(false)
                    .ocean(false)
                    .lavender(false)
                    .cherry(false)
                    .night(false)
                    .autumn(false)
                    .build();
            return shopRepository.save(shop);
        });
    }


    // 테마 구매 로직
    @Transactional
    public String buyTheme(Long memberId, String themeName) {

        if (!THEME_PRICES.containsKey(themeName)) {
            return "존재하지 않는 테마입니다.";
        }

        Shop shop = getOrCreateShop(memberId);

        // 이미 보유 여부 확인
        boolean alreadyOwned = THEME_CHECKERS.get(themeName).apply(shop);

        if (alreadyOwned) {
            return "이미 구매한 테마입니다.";
        }

        // 포인트 조회 (findByMember_Id 사용 권장)
        Mission mission = missionRepository.findByMember_Id(memberId)
                .orElseThrow(() -> new IllegalArgumentException("미션 정보가 존재하지 않습니다."));

        int price = THEME_PRICES.get(themeName);

        // 포인트 부족
        if (mission.getTotalPoint() < price) {
            return "포인트 부족으로 구매할 수 없습니다.";
        }

        // 포인트 차감
        mission.setTotalPoint(mission.getTotalPoint() - price);

        // 테마 활성화
        THEME_SETTERS.get(themeName).accept(shop);

        return "테마 구매 완료!";
    }
}
