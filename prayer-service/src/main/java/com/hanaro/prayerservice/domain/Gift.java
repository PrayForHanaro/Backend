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
@Table(name = "Gift")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Gift extends  BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "giftId")
	private Long giftId;

	@Column(name = "senderId", nullable = false)
	private Long senderId;

	@Column(name = "receiverId")
	private Long receiverId;

	@Enumerated(EnumType.STRING)
	@Column(name = "giftReceiverType")
	private GiftReceiverType giftReceiverType;

	@Column(name = "fromAccountId", nullable = false)
	private Long fromAccountId;

	@Column(name = "toSavingsAccountId", nullable = false)
	private Long toSavingsAccountId;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "isActive", nullable = false)
	private boolean isActive;

	@Column(name = "cumulativeTotal", nullable = false)
	private BigDecimal cumulativeTotal;

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

	/** 자동이체 비활성화 */
	public void deactivate() {
		this.isActive = false;
	}

	/** 자동이체 금액 변경 */
	public void updateAmount(BigDecimal amount) {
		this.amount = amount;
	}
}
