package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 연금 정보
 * - 하나은행/타행 연금 구분해서 표시
 * - 하나은행 연금 등록 시 user.donation_rate 증가
 *   (하나은행 연금 1개 = 1%)
 * - 월 예상 수령액 표시
 * - user_id, account_id 모두 다른 DB 참조 → FK 없음
 */
@Entity
@Table(name = "PENSION")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Pension {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pensionId;

	/** 연금 가입자 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long userId;

	/**
	 * 연결된 계좌 ID (user_db 참조, FK 없음)
	 * 연금 수령 계좌 (null 허용)
	 */
	private Long accountId;

	/**
	 * 연금 종류
	 * 국민연금 / 퇴직연금 / 개인연금
	 */
	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private PensionType pensionType;

	/**
	 * 하나은행 여부
	 * true = 하나은행 → 포인트 이율 계산에 포함
	 * false = 타행 → 포인트 이율 계산 제외
	 */
	@Column(nullable = false)
	private boolean isHana;

	/** 현재 잔액 */
	@Column(nullable = false)
	private BigDecimal balance;

	/**
	 * 월 예상 수령액
	 * 사용자가 직접 입력 또는 외부 API 연동
	 */
	@Column(nullable = false)
	private BigDecimal monthlyExpectedAmount;

	/** 연금 시작일 */
	private LocalDate startDate;

	/** 연금 종료 예정일 */
	private LocalDate expectedEndDate;

	/** 연금 기관명 (예: 하나은행, 국민연금공단) */
	@Column(length = 100)
	private String institutionName;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	/** 잔액 업데이트 */
	public void updateBalance(BigDecimal balance) {
		this.balance = balance;
	}

	/** 월 예상 수령액 업데이트 */
	public void updateMonthlyExpectedAmount(BigDecimal amount) {
		this.monthlyExpectedAmount = amount;
	}
}