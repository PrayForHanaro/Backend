package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 일회성 축복 송금 이력 (BLESS_SPEC §5)
 * - Gift와 독립. 적금 가입 없이 즉시 송금 (decisions/003 대상자 한정 규칙 미적용)
 * - 수신자는 등록 대상자일 수도, 미등록 계좌번호일 수도 있음 (BLESS_SPEC §5-1-7)
 * - Mock 송금이나 기록은 실제 저장 (BLESS_SPEC §5-1-9)
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ONCE_TRANSFER", indexes = {
    @Index(name = "idx_once_transfer_sender_sent", columnList = "sender_id, sentAt DESC")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class OnceTransfer extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long onceTransferId;

    /** 보내는 사람 ID (user_db 참조, FK 없음) */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /** 출금 계좌 ID (user_db 참조, 자동으로 defaultAccountId, decisions/001) */
    @Column(nullable = false)
    private Long fromAccountId;

    /** 수신자 계좌번호 (외부 은행 포함 가능, 하이픈 포함 문자열) */
    @Column(nullable = false, length = 50)
    private String toAccountNumber;

    /** 송금액 */
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    /** 축복 메시지 (BLESS_SPEC §3-5 최대 250자, 선택 입력) */
    @Column(length = 250)
    private String message;

    /** 송금 시각 */
    @Column(nullable = false)
    private LocalDateTime sentAt;
}
