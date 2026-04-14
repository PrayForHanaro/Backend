package com.hanaro.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 성도 (앱 사용자)
 * - org_id, district_id는 org_db 참조 (FK 없음)
 * - default_account_id는 순환참조로 인해 후처리
 * - donation_rate: 하나은행 연금 갯수에 비례 (1개=1%)
 */
@Entity
@Table(name = "USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	/** 소속 교회/성당/절 ID (org_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long orgId;

	/** 소속 구역 ID (org_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long districtId;

	/** 성도 이름 */
	@Column(nullable = false, length = 50)
	private String name;

	/** 생년월일 */
	@Column(nullable = false)
	private LocalDate birthDate;

	/** 전화번호 (로그인 ID로 사용) */
	@Column(nullable = false, unique = true, length = 20)
	private String phone;

	/**
	 * 역할
	 * 일반 / 집사 / 권사 / 목사 / 관리자
	 * 회원가입 시 교회 성도 리스트에서 자동 식별
	 */
	@Column(nullable = false, length = 10)
	private String role;

	/**
	 * 기본 출금 계좌 ID
	 * 같은 DB이지만 순환참조로 인해 FK 후처리
	 * (ACCOUNT 테이블 생성 후 ALTER TABLE로 FK 추가)
	 */
	@Column
	private Long defaultAccountId;

	/** 마지막 앱 진입 시간 (미접속일수 계산용) */
	private LocalDateTime lastCheckinAt;

	/**
	 * 헌금 기여율 (하나은행 기부금 비율)
	 * 하나은행 연금 1개당 1% 증가
	 * 예: 연금 2개 = 2.00%
	 */
	@Column(nullable = false)
	private Double donationRate;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	// 같은 DB → 양방향 관계 정상 사용
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Account> accounts = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Checkin> checkins = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Point> points = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
		this.donationRate = 0.0;
		this.role = "일반";
	}

	/** 기본 계좌 변경 */
	public void updateDefaultAccount(Long accountId) {
		this.defaultAccountId = accountId;
	}

	/** 마지막 체크인 갱신 */
	public void updateLastCheckin() {
		this.lastCheckinAt = LocalDateTime.now();
	}

	/**
	 * 헌금 기여율 갱신
	 * 하나은행 연금 갯수 변경 시 호출
	 * @param hanaPensionCount 현재 하나은행 연금 갯수
	 */
	public void updateDonationRate(int hanaPensionCount) {
		this.donationRate = (double) hanaPensionCount;
	}

	/** 역할 변경 */
	public void updateRole(String role) {
		this.role = role;
	}

	/** 미접속 일수 계산 */
	public long getDaysWithoutCheckin() {
		if (this.lastCheckinAt == null) return 0;
		return java.time.temporal.ChronoUnit.DAYS.between(
			this.lastCheckinAt.toLocalDate(),
			LocalDate.now()
		);
	}
}