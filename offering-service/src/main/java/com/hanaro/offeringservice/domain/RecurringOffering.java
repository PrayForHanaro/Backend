package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


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
@Table(name = "RecurringOffering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class RecurringOffering extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long recurringId;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(name = "accountId", nullable = false)
	private Long accountId;

	@Column(name = "orgId", nullable = false)
	private Long orgId;

	@Column(name = "offeringType", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private OfferingType offeringType;

	@Column(name = "amount", nullable = false)
	private BigDecimal amount;

	@Column(name = "startDate", nullable = false)
	private LocalDate startDate;

	@Column(name = "endDate")
	private LocalDate endDate;

	@Column(name = "nextPaymentDate", nullable = false)
	private LocalDate nextPaymentDate;

	@Column(name = "isActive", nullable = false)
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