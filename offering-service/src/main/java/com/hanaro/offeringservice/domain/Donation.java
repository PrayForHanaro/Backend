package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 하나은행 기부금 기록
 * - 유저가 헌금할 때 하나은행이 추가로 기부하는 금액
 * - 기부금 = 헌금액 × donation_rate
 * - donation_rate는 하나은행 연금 갯수에 비례 (1개 = 1%)
 * - 이번달 교회 헌금 + 하나은행 기부금 합산액을 메인화면에 표시
 * - org_id, user_id 모두 다른 DB 참조 → FK 없음
 *
 * 예시)
 * 헌금액 300,000원 × donation_rate 2% = 기부금 6,000원
 */
@Entity
@Table(name = "DONATION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Donation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long donationId;

    /** 기부금 대상 교회/성당/절 ID (org_db 참조, FK 없음) */
    @Column(nullable = false)
    private Long orgId;

    /** 기부금 기반이 된 헌금자 ID (user_db 참조, FK 없음) */
    @Column(nullable = false)
    private Long userId;

    /**
     * 기반 헌금액
     * 이 금액에 donation_rate를 곱해서 기부금 산출
     */
    @Column(nullable = false)
    private BigDecimal offeringAmount;

    /**
     * 적용된 헌금 기여율 (%)
     * 기부금 산출 당시의 donation_rate 스냅샷
     * 나중에 rate가 바뀌어도 당시 기록 보존
     */
    @Column(nullable = false)
    private BigDecimal donationRate;

    /**
     * 실제 기부 금액
     * = offeringAmount × donationRate / 100
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * 기부금 산출 당시 하나은행 연금 갯수
     * 이력 추적용
     */
    @Column(nullable = false)
    private int hanaPensionCount;

    /** 기부 일시 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime donatedAt;

    @PrePersist
    protected void onCreate() {
        this.donatedAt = LocalDateTime.now();
    }
}