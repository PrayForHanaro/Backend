package com.hanaro.offeringservice.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OfferingRequestDTO {
    Long orgId;
    Long accountId;
    String type;     // 십일조, 감사헌금 등
    String personType;  // 기명, 무기명
    String name;       // 기명일 때 이름
    BigDecimal amount;
    BigDecimal usedPoint;
    int point;          // 추가: 사용한 포인트 (기록용)
    String prayerTopic;

}

