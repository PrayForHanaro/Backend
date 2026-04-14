package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 헌금 단건 내역
 * - 일회성 헌금 기록
 * - 기명/무기명 선택 가능
 * - 기도제목 함께 작성 가능 (최대 250자)
 * - user_id, org_id, account_id 모두 다른 DB 참조 → FK 없음
 */
@Entity
@Table(name = "OFFERING")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Offering {

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
	private Long accountId;

	/**
	 * 헌금 종류
	 * 십일조 / 감사헌금 / 선교헌금 / 건축헌금 / 기타
	 */
	@Column(nullable = false, length = 20)
	private String offeringType;

	/** 헌금 금액 */
	@Column(nullable = false)
	private BigDecimal amount;

	/**
	 * 기명/무기명 여부
	 * true = 기명 (이름 공개)
	 * false = 무기명 (이름 비공개)
	 */
	@Column(nullable = false)
	private boolean isNamed;

	/**
	 * 헌금자 이름
	 * isNamed = false 이면 null
	 */
	@Column(length = 50)
	private String offererName;

	/** 기도제목 (최대 250자, 선택) */
	@Column(length = 250)
	private String prayerContent;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}
}