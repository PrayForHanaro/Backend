package com.hanaro.offeringservice.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferingEvent {
    private Long userId;
    private Long orgId;
    private BigDecimal amount;
    private int usedPoint;
    private String offeringType;
}
