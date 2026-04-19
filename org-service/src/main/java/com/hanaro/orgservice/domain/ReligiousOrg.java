package com.hanaro.orgservice.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 종교단체 (교회 / 성당 / 절)
 * - 회원가입 시 검색해서 선택
 * - 하나은행 제휴 교회 등록 정보
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "ReligiousOrg")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ReligiousOrg extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long religiousOrgId;

	@Enumerated(EnumType.STRING)
	@Column(name = "orgType", nullable = false, length = 10)
	private OrgType orgType;

	/** 종교단체 이름 (예: 하나교회, 명동성당) */
	@Column(name = "orgName", nullable = false, length = 100)
	private String orgName;

	/** 주소 */
	@Column(name = "address", nullable = false, length = 30)
	private String address;

	@Column(name = "totalOfferingAmount", nullable = false)
	private BigDecimal totalOfferingAmount;

	@Column(name = "totalPointAmount", nullable = false)
	private BigDecimal totalPointAmount;

	/** 대표 전화번호 */
	@Column(name = "phone", length = 20)
	private String phone;

	// 대표자
	@Column(name = "representativeId", length = 50)
	private Long representativeId;

	@PrePersist
	protected void onCreate() {
		this.totalOfferingAmount = BigDecimal.ZERO;
		this.totalPointAmount = BigDecimal.ZERO;
	}

	@Column(name = "accountId", nullable = false)
	private Long accountId;



	/** 헌금 총액 누적 */
	public void addOfferingAmount(BigDecimal amount) {
		this.totalOfferingAmount = this.totalOfferingAmount.add(amount);
	}

	/** 포인트 총액 누적 */
	public void addPointAmount(BigDecimal amount) {
		this.totalPointAmount = this.totalPointAmount.add(amount);
	}
}