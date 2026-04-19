package com.hanaro.offeringservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ## 가입할 연금, 적금 등 상품 정보는 API로 불러온다 ##
 *
 * 연금 정보
 * - 하나은행/타행 연금 구분해서 표시
 * - 하나은행 연금 등록 시 user.donation_rate 증가
 *   (하나은행 연금 1개 = 1%)
 * - 월 예상 수령액 표시
 * - user_id, account_id 모두 다른 DB 참조 → FK 없음
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "Pension")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Pension extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pensionId;

	@Column(name = "userId", nullable = false)
	private Long userId;

	@Column(name = "accountId")
	private Long accountId;

	@Column(name = "pensionType", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private PensionType pensionType;

	@Column(name = "isHanaBank", nullable = false)
	private boolean isHanaBank;

	@Column(name = "totalContribution", nullable = false)
	private BigDecimal totalContribution;

	@Column(name = "totalWithdrawal", nullable = false)
	private BigDecimal totalWithdrawal;

	@Column(name = "profit", nullable = false)
	private BigDecimal profit;

	@Column(name = "returnRate", nullable = false)
	private BigDecimal returnRate;

	@Column(name = "institutionName", length = 100)
	private String institutionName;
}