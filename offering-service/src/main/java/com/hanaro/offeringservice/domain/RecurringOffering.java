package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.apache.kafka.shaded.com.google.protobuf.Enum;

/**
 * 정기 헌금 자동이체 설정
 * - 매달 지정일에 자동으로 헌금 처리
 * - 시작일/종료일 설정 가능 (종료일 없으면 무기한)
 * - 다음 납부일 관리로 배치 처리
 * - 등록 시 포인트 지급 (OFFERING_RECURRING 500p)
 * - user_id, account_id, org_id 모두 다른 DB 참조 → FK 없음
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "RECURRING_OFFERING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RecurringOffering extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recurringId;

	/** 정기헌금 등록한 성도 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long userId;

	/** 자동이체 출금 계좌 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long accountId;

	/** 헌금 대상 교회/성당/절 ID (org_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long orgId;

	/**
	 * 헌금 종류
	 * 십일조 / 감사헌금 / 선교헌금 / 건축헌금 / 기타
	 */
	@Column(nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private Enum offeringType;

	/** 매달 자동이체 금액 */
	@Column(nullable = false)
	private BigDecimal amount;

	/** 자동이체 시작일 */
	@Column(nullable = false)
	private LocalDate startDate;

	/** 자동이체 종료일 (null = 무기한) */
	private LocalDate endDate;

	/**
	 * 다음 납부 예정일
	 * 배치 스케줄러가 이 날짜 기준으로 자동이체 실행
	 * 이체 완료 후 다음달로 업데이트
	 */
	@Column(nullable = false)
	private LocalDate nextPaymentDate;

	/**
	 * 활성화 여부
	 * true = 자동이체 진행 중
	 * false = 중지
	 */
	@Column(nullable = false)
	private boolean isActive;

	@PrePersist
	protected void onCreate() {
		this.isActive = true;
	}

	/** 정기헌금 중지 */
	public void deactivate() {
		this.isActive = false;
	}

	/**
	 * 다음 납부일 업데이트
	 * 이체 완료 후 다음달로 갱신
	 */
	public void updateNextPaymentDate(LocalDate nextDate) {
		this.nextPaymentDate = nextDate;
	}

	/** 금액 변경 */
	public void updateAmount(BigDecimal amount) {
		this.amount = amount;
	}

	/** 종료일 설정 */
	public void updateEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}