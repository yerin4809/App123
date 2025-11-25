package com.example.demo.domain.shop;

import com.example.demo.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shop {

    @Id
    private Long memberId;   // Member PK + FK

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_id")
    private Member member;

    // 기본 테마 + 구매 가능 테마들
    @Builder.Default
    private boolean defaultTheme = true;
    @Builder.Default
    private boolean sunset = false;
    @Builder.Default
    private boolean forest = false;
    @Builder.Default
    private boolean ocean = false;
    @Builder.Default
    private boolean lavender = false;
    @Builder.Default
    private boolean cherry = false;
    @Builder.Default
    private boolean night = false;
    @Builder.Default
    private boolean autumn = false;
}
