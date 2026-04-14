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
@Table(name = "RELIGIOUS_ORG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ReligiousOrg extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long religiousOrgId;

	/**
	 * 종교단체 유형
	 * 교회 / 성당 / 절
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private OrgType orgType;

	/** 종교단체 이름 (예: 하나교회, 명동성당) */
	@Column(nullable = false, length = 100)
	private String orgName;

	/** 주소 */
	@Column(nullable = false, length = 30)
	private String address;

	/**
	 * 누적 헌금 총액
	 * 성도들이 이 교회에 헌금한 전체 누적 금액
	 * 헌금 등록 시 offering-service → org-service API 호출로 업데이트
	 */
	@Column(nullable = false)
	private BigDecimal totalOfferingAmount;

	/**
	 * 누적 포인트 총액
	 * 이 교회 성도들이 헌금으로 적립한 포인트 합산
	 * 포인트 적립 시 업데이트
	 */
	@Column(nullable = false)
	private BigDecimal totalPointAmount;

	/** 대표 전화번호 */
	//형식 지정 필요
	@Column(length = 20)
	private String phone;

	// 대표자
	@Column(length = 50)
	private Long representativeId; //userTable에 대표자 등록되어있음.


	@PrePersist
	protected void onCreate() {
		this.totalOfferingAmount = BigDecimal.ZERO;
		this.totalPointAmount = BigDecimal.ZERO;
	}

	@Column(nullable = false)
	private Long accountId; // api로 연결, fk 금지



	/** 헌금 총액 누적 */
	public void addOfferingAmount(BigDecimal amount) {
		this.totalOfferingAmount = this.totalOfferingAmount.add(amount);
	}

	/** 포인트 총액 누적 */
	public void addPointAmount(BigDecimal amount) {
		this.totalPointAmount = this.totalPointAmount.add(amount);
	}
}