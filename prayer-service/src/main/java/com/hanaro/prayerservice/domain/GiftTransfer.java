package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 정기이체 이력 (Gift N : 1, 이체 발생마다 1 row)
 * - BLESS_SPEC §2-3/2-4 타임라인 (이체 + 메시지 혼합)
 * - BLESS_SPEC §6-7/6-8 실패 상태 저장
 * - Gift.cumulativeTotal은 SUCCESS 합으로 파생. 조회 성능 위해 캐싱 유지
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GIFT_TRANSFER", indexes = {
    @Index(name = "idx_gift_transfer_gift_date", columnList = "gift_id, transferDate DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class GiftTransfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long transferId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gift_id", nullable = false,
        foreignKey = @ForeignKey(name = "fk_gift_transfer_gift"))
    private Gift gift;

    /** 이체 실행 일자 */
    @Column(nullable = false)
    private LocalDate transferDate;

    /** 이체 금액 (Gift.amount 스냅샷. 추후 금액 변경 대비) */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransferStatus status;

    /** 실패 시 원인 (SUCCESS일 땐 null) */
    @Column(length = 255)
    private String failureReason;
}
