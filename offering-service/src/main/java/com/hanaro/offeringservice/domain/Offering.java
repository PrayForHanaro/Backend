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
@Table(name = "Offering")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Offering extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long offeringId;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(name = "orgId", nullable = false)
	private Long orgId;

	@Column(name = "accountId", nullable = false)
	private Long accountId;

	@Enumerated(EnumType.STRING)
	@Column(name = "offeringType")
	private OfferingType offeringType;

	@Column(name = "amount", nullable = false, precision = 15, scale = 2)
	private BigDecimal amount;

	@Column(name = "offererName", length = 50)
	private String offererName;

	@Column(name = "prayerContent", length = 250)
	private String prayerContent;

	@Column(name = "usedPoint")
	private BigDecimal usedPoint;
}
