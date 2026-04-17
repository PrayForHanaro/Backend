package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 기도적금 자동이체 설정 (정기)
 * - 매달 지정된 날짜에 대상자 적금계좌로 자동이체
 * - 한 사용자가 여러 대상자에게 보낼 때 Gift 여러 개 생성
 * - 대상자 1명당 적금 1건 (BLESS_SPEC §10-8, decisions/003 → unique constraint)
 * - 대상자는 가입자 한정 (decisions/003). receiverId nullable=false
 * - 메시지는 PrayerSavings, 이체 로그는 GiftTransfer에 별도 저장
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(
    name = "GIFT",
    uniqueConstraints = @UniqueConstraint(name = "uq_gift_sender_receiver", columnNames = {"sender_id", "receiver_id"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Gift extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long giftId;

	/** 보내는 사람 ID (user_db 참조, FK 없음) */
	@Column(name = "sender_id", nullable = false)
	private Long senderId;

	/** 받는 사람 ID (user_db 참조, FK 없음). 가입자 한정 (decisions/003) */
	@Column(name = "receiver_id", nullable = false)
	private Long receiverId;

	/** 대상자와의 관계 (자녀/손주/증손주) */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private GiftReceiverType giftReceiverType;

	/** 출금 계좌 ID - 나의 대표 헌금계좌 (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long fromAccountId;

	/** 입금 계좌 ID - 상대방 적금계좌 (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long toSavingsAccountId;

	/** 매달 자동이체 금액 */
	@Column(nullable = false)
	private BigDecimal amount;

	/** 매달 이체 실행일 (1~31) (BLESS_SPEC §6-4) */
	@Column(nullable = false)
	private int transferDay;

	/** 기도 목표 기간 (일). 기본 365 (BLESS_SPEC §10 — 사용자 입력, Mock) */
	@Column(nullable = false)
	private int goalDays;

	/** 자동이체 활성화 여부 */
	@Column(nullable = false)
	private boolean isActive;

	/** 이 시점까지의 누적 송금 총액 (캐싱, SUCCESS 합계로 갱신) */
	@Column(nullable = false)
	private BigDecimal cumulativeTotal;

	/** 적금 상품 이름 */
	@Column(nullable = false)
	private String savingsProductName;

	/** 적금 상품 혜택률 */
	@Column(nullable = false)
	private BigDecimal interestRate;

	@OneToMany(mappedBy = "gift", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<PrayerSavings> prayerSavings = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.isActive = true;
	}

	public void deactivate() {
		this.isActive = false;
	}

	public void updateAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void addCumulativeTotal(BigDecimal delta) {
		this.cumulativeTotal = this.cumulativeTotal.add(delta);
	}
}
