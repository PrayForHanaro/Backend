package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 헌금 내역
 * - 일회성 & 정기 헌금 모두 기록
 * - 기도제목 함께 작성 가능 (최대 250자)
 * - user_id, org_id, account_id 모두 다른 DB 참조 → FK 없음
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "OFFERING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Offering extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long offeringId;

	/** 헌금한 성도 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long userId;

	/** 소속 교회/성당/절 ID (org_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long orgId;

	/** 출금 계좌 ID (user_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long accountId; //사용자가 출금 계좌를 변경할 수 있으므로, 어떤 계좌에서 보낸건지는 기록해야됨.

	/**
	 * 헌금 종류
	 */
	@Enumerated(EnumType.STRING)
	private OfferingType offeringType;

	/** 헌금 금액 */
	@Column(nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(length = 50) //무기명이면 null
	private String offererName;

	/** 기도제목 (최대 250자, 선택) */
	@Column(length = 250)
	private String prayerContent;

	/** 사용한 포인트 (0일 수 있음) */
	private BigDecimal usedPoint;
}
