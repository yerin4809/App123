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
    private boolean defaultTheme = true;
    private boolean sunset = false;
    private boolean forest = false;
    private boolean ocean = false;
    private boolean lavender = false;
    private boolean cherry = false;
    private boolean night = false;
    private boolean autumn = false;
}
