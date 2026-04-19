package com.hanaro.prayerservice.dto;

import com.hanaro.prayerservice.domain.SavingsProduct;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavingsProductResponse {

    private Long savingsProductId;
    private String name;
    private BigDecimal interestRate;
    private boolean isActive;

    public static SavingsProductResponse from(SavingsProduct product) {
        return SavingsProductResponse.builder()
                .savingsProductId(product.getSavingsProductId())
                .name(product.getName())
                .interestRate(product.getInterestRate())
                .isActive(product.isActive())
                .build();
    }
}
