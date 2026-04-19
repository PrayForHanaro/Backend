package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * 기도적금 상품 마스터.
 * 관리자가 등록·수정하며, 활성 상품은 단일(isActive=true 1건).
 * 가입 시 Gift에 name·interestRate 스냅샷을 복사해 이후 상품 변경과 독립적으로 유지.
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "SAVINGS_PRODUCT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class SavingsProduct extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long savingsProductId;

    @Column(nullable = false, length = 100)
    private String name;

    /** 금리 (%). 예: 5.000 */
    @Column(nullable = false, precision = 5, scale = 3)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private boolean isActive;

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updateInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }
}
