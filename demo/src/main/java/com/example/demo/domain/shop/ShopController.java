package com.example.demo.domain.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shop")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // [Note] 포인트 사용 로직이므로, 향후 서버 세션 또는 토큰 기반의 검증 로직 추가
    @PostMapping("/buy/{memberId}")
    public String buyTheme(@PathVariable Long memberId, @RequestParam String theme) {
        return shopService.buyTheme(memberId, theme);
    }
}