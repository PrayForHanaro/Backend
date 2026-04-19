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
    name = "Gift",
    uniqueConstraints = @UniqueConstraint(name = "uq_gift_sender_receiver", columnNames = {"senderId", "receiverId"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Gift extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "giftId")
	private Long giftId;

	/** 보내는 사람 ID (user_db 참조, FK 없음) */
	@Column(name = "senderId", nullable = false)
	private Long senderId;

	/** 받는 사람 ID (user_db 참조, FK 없음). 가입자 한정 (decisions/003) */
	@Column(name = "receiverId", nullable = false)
	private Long receiverId;

	/** 대상자와의 관계 (아들/딸/손주) */
	@Enumerated(EnumType.STRING)
	@Column(name = "giftReceiverType", nullable = false, length = 20)
	private GiftReceiverType giftReceiverType;

	@Column(name = "fromAccountId", nullable = false)
	private Long fromAccountId;

	@Column(name = "toSavingsAccountId", nullable = false)
	private Long toSavingsAccountId;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	/** 매달 이체 실행일 (1~31) (BLESS_SPEC §6-4) */
	@Column(name = "transferDay", nullable = false)
	private int transferDay;

	/** 기도 목표 기간 (일). 기본 365 (BLESS_SPEC §10 — 사용자 입력, Mock) */
	@Column(name = "goalDays", nullable = false)
	private int goalDays;

	/** 자동이체 활성화 여부 */
	@Column(name = "isActive", nullable = false)
	private boolean isActive;

	/** 이 시점까지의 누적 송금 총액 (캐싱, SUCCESS 합계로 갱신) */
	@Column(name = "cumulativeTotal", nullable = false)
	private BigDecimal cumulativeTotal;

	/** 가입한 적금 상품 ID. 상품 변경·삭제와 독립적으로 이 Gift의 스냅샷(name·rate)은 유지 */
	@Column(name = "savingsProductId", nullable = false)
	private Long savingsProductId;

	/** 적금 상품 이름 (가입 시점 스냅샷) */
	@Column(name = "savingsProductName", nullable = false)
	private String savingsProductName;

	@Column(name = "interestRate", nullable = false)
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
