package com.hanaro.userservice.domain;

import com.hanaro.common.auth.UserRole;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
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
@Table(name = "User")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class User extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long userId;

	/** 소속 교회/성당/절 ID (org_db 참조, FK 없음) */
	@Column(name = "orgId", nullable = true)
	private Long orgId;

	/** 이름 */
	@Column(name = "name", nullable = false, length = 50)
	private String name;

	/** 생년월일 */
	@Column(name = "birthDate", nullable = false)
	private LocalDate birthDate;

	/** 전화번호 (로그인 ID로 사용) */
	@Column(name = "phone", nullable = false, unique = true, length = 20)
	private String phone;

	@Column(name = "password", nullable = false, length = 255)
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "role", nullable = false, length = 10)
	private UserRole role;

	@Column(name = "profileUrl", nullable = true)
	String profileUrl;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "defaultAccountId", nullable = true)
	private Account defaultAccount;

	@Column(name = "donationRate", nullable = false)
	private Double donationRate;

	@Column(name = "pointSum")
	private int pointSum;

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
		this.role = UserRole.USER;
		this.pointSum = 0;
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
		this.donationRate = hanaPensionCount / 100.0;
	}

	/** 역할 변경 */
	public void updateRole(UserRole role) {
		this.role = role;
	}

	public void updateProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public void addPoint(int amount){
		this.pointSum+=amount;
	}

	public void minusPoint(int amount){
		this.pointSum-=amount;
	}
}
