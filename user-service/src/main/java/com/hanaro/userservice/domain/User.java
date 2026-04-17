package com.hanaro.userservice.domain;

import com.hanaro.common.auth.UserRole;
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
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "USER")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	/** 소속 교회/성당/절 ID (org_db 참조, FK 없음) */
	@Column(nullable = false)
	private Long orgId;

	/** 이름 */
	@Column(nullable = false, length = 50)
	private String name;

	/** 생년월일 */
	@Column(nullable = false)
	private LocalDate birthDate;

	/** 전화번호 (로그인 ID로 사용) */
	//형식 알아서 지정
	@Column(nullable = false, unique = true, length = 20)
	private String phone;

	@Column(nullable = false)
	private String password;
	/**
	 * 역할
	 * ex 일반 / 집사 / 권사 / 목사 / 관리자
	 * 회원가입 시 교회 성도 리스트에서 자동 식별
	 */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private UserRole role;

	/**
	 * 기본 출금 계좌 ID
	 * 같은 DB이지만 순환참조로 인해 FK 후처리
	 * (ACCOUNT 테이블 생성 후 ALTER TABLE로 FK 추가)
	 */

	// User.java 내부 수정
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "default_account_id", nullable = true) // 초기 생성시엔 null일 수 있음
	private Account defaultAccount; // 필드명을 ID가 아닌 객체로 변경

	/**
	 * 헌금 기여율
	 * 하나은행 연금 1개당 1% 증가 ???
	 * 예: 연금 2개 = 2.00%
	 */
	@Column(nullable = false)
	private Double donationRate;

	// 같은 DB → 양방향 관계 정상 사용
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Account> accounts = new ArrayList<>();

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	@Builder.Default
	private List<Point> points = new ArrayList<>();

	@PrePersist
	protected void onCreate() {
		this.donationRate = 0.0;
		this.role = UserRole.일반사용자;
	}

	/** 기본 계좌 변경 */
	public void updateDefaultAccount(Account account) {
		this.defaultAccount = account;
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
	public void updateRole(UserRole role) {
		this.role = role;
	}
}