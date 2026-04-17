package com.hanaro.userservice.dto.event;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PointEvent {
    private Long userId;
    private String pointType;

    private int amount;

    private String title;       // 활동명, 헌금 유형 등
    private String targetName;  // 적금 대상자
    private String productName; // 적금 상품명
}
