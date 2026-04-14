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
 * - 구역(District) 목록 보유
 */
@Entity
@Table(name = "RELIGIOUS_ORG")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class ReligiousOrg {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orgId;

	/**
	 * 종교단체 유형
	 * 교회 / 성당 / 절
	 */
	@Column(nullable = false, length = 10)
	private String orgType;

	/** 종교단체 이름 (예: 하나교회, 명동성당) */
	@Column(nullable = false, length = 100)
	private String orgName;

	/** 주소 */
	@Column(nullable = false)
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
	@Column(length = 20)
	private String phone;

	/** 대표자 이름 (담임목사 / 주임신부 / 주지스님) */
	@Column(length = 50)
	private String representativeName;

	/** 사업자 등록번호 */
	@Column(length = 20)
	private String registrationNumber;

	/**
	 * 등록 성도 수
	 * 회원가입/탈퇴 시 자동 증감
	 */
	@Column(nullable = false)
	private int memberCount;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	/**
	 * 구역 목록
	 * 같은 DB → 양방향 관계 정상 사용
	 * 교회 삭제 시 구역도 자동 삭제 (cascade)
	 */
	@OneToMany(mappedBy = "org", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<District> districts = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.memberCount = 0;
		this.totalOfferingAmount = BigDecimal.ZERO;
		this.totalPointAmount = BigDecimal.ZERO;
	}

	/** 성도 수 증가 (회원가입 시) */
	public void increaseMemberCount() {
		this.memberCount++;
	}

	/** 성도 수 감소 (탈퇴 시) */
	public void decreaseMemberCount() {
		if (this.memberCount > 0) {
			this.memberCount--;
		}
	}

	/** 대표자 변경 */
	public void updateRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	/** 전화번호 변경 */
	public void updatePhone(String phone) {
		this.phone = phone;
	}

	/** 주소 변경 */
	public void updateAddress(String address) {
		this.address = address;
	}

	/** 헌금 총액 누적 */
	public void addOfferingAmount(BigDecimal amount) {
		this.totalOfferingAmount = this.totalOfferingAmount.add(amount);
	}

	/** 포인트 총액 누적 */
	public void addPointAmount(BigDecimal amount) {
		this.totalPointAmount = this.totalPointAmount.add(amount);
	}
}