package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 증여 자동이체 설정
 * - 매달 지정된 사람의 적금계좌에 자동이체
 * - 대상이 여러 명일 수 있음 (Gift 여러 개 생성)
 * - 메시지는 GiftLog에 날짜별 기록
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "GIFT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Gift extends  BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "int unsigned")
	private Long giftId;

	/** 보내는 사람 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long senderId;

	/** 받는 사람 ID (user_db 참조, FK 없음) */
	@Column(nullable = true) /** 가입하지 않은 사용자에게 보낼 경우 null 처리 */
	private Long receiverId;

	@Enumerated(EnumType.STRING)
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

	/** 자동이체 활성화 여부 */
	@Column(nullable = false)
	private boolean isActive;

	/** 이 시점까지의 누적 송금 총액 */
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

	/** 자동이체 비활성화 */
	public void deactivate() {
		this.isActive = false;
	}

	/** 자동이체 금액 변경 */
	public void updateAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
