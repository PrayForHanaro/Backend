package com.hanaro.prayerservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 기도 적금
 * - 기도 제목을 걸고 적금 납입
 * - D+N / 총 N일 형태로 진행 현황 표시
 * - 목표 금액 달성 여부 추적
 */
@Entity
@Table(name = "PRAYER_SAVINGS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class PrayerSavings {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long prayerSavingsId;

	/** 적금 가입자 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long userId;

	/** 입금될 적금 계좌 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long toSavingsAccountId;

	/** 기도 제목 (예: 홍길동 아들 100일 기도) */
	@Column(nullable = false, length = 100)
	private String prayerTitle;

	/** 기도 대상 (예: 홍길동) */
	@Column(length = 50)
	private String prayerTarget;

	/** 목표 금액 */
	@Column(nullable = false)
	private BigDecimal targetAmount;

	/** 월 납입액 */
	@Column(nullable = false)
	private BigDecimal monthlyAmount;

	/** 현재 납입 누적액 */
	@Column(nullable = false)
	private BigDecimal currentAmount;

	/** 시작일 */
	@Column(nullable = false)
	private LocalDate startDate;

	/** 종료일 (null이면 무기한) */
	private LocalDate endDate;

	/** D+N 현재 경과일수 */
	@Column(nullable = false)
	private int dDay;

	/** 총 기도 목표일수 (기본 100일) */
	@Column(nullable = false)
	private int totalDays;

	/** 활성화 여부 */
	@Column(nullable = false)
	private boolean isActive;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.isActive = true;
		this.currentAmount = BigDecimal.ZERO;
		this.dDay = 0;
	}

	/** 납입액 추가 + D+N 증가 */
	public void addPayment(BigDecimal amount) {
		this.currentAmount = this.currentAmount.add(amount);
		this.dDay++;
	}

	/** 목표 달성 여부 */
	public boolean isCompleted() {
		return this.currentAmount.compareTo(this.targetAmount) >= 0
			|| this.dDay >= this.totalDays;
	}

	/** 진행률 (%) */
	public int getProgressRate() {
		if (this.targetAmount.compareTo(BigDecimal.ZERO) == 0) return 0;
		return this.currentAmount
			.multiply(BigDecimal.valueOf(100))
			.divide(this.targetAmount, 0, java.math.RoundingMode.DOWN)
			.intValue();
	}

	/** 적금 비활성화 */
	public void deactivate() {
		this.isActive = false;
	}
}