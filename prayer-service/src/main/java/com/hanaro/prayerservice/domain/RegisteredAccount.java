package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

/**
 * 일회성 축복 송금(OnceTransfer) 수신 계좌 즐겨찾기 (BLESS_SPEC §5-1-5)
 * - OnceTransfer 성공 시 (sender, accountNumber) 쌍으로 자동 UPSERT
 * - M1: GET 조회만 노출. alias 편집·명시 POST/DELETE 는 M2
 * - sender 격리. unique(sender_id, accountNumber) 로 동일 sender 내 중복 방지
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "REGISTERED_ACCOUNT",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_registered_account_sender_account",
        columnNames = {"sender_id", "accountNumber"}
    ),
    indexes = @Index(
        name = "idx_registered_account_sender_last_used",
        columnList = "sender_id, lastUsedAt DESC"
    )
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RegisteredAccount extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long registeredAccountId;

    /** 보내는 사람 ID (user_db 참조, FK 없음) */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;

    /** 수신자 계좌번호 (하이픈 포함, 외부 은행 포함 가능) */
    @Column(nullable = false, length = 50)
    private String accountNumber;

    /** 사용자 지정 별칭. M1 에선 항상 null (편집 기능은 M2) */
    @Column(length = 50)
    private String alias;

    /** 마지막 송금 시각. UPSERT 시 갱신되어 최근순 정렬 기준이 됨 */
    @Column(nullable = false)
    private Instant lastUsedAt;

    public void touch(Instant at) {
        this.lastUsedAt = at;
    }
}
