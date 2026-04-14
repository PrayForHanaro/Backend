package com.hanaro.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 계좌 정보
 * - 하나은행 여부 (is_hana): 헌금 기여율 계산에 사용
 * - 기본 계좌 (is_default): 헌금/증여 출금 기본값
 * - 적금 계좌 (is_savings): 기도적금/증여 입금 대상
 */
@Entity
@Table(name = "ACCOUNT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Account {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long accountId;

	/** 계좌 소유자 (같은 DB → FK 정상 사용) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/** 은행명 (예: 하나은행, 국민은행) */
	@Column(nullable = false, length = 50)
	private String bankName;

	/** 계좌번호 */
	@Column(nullable = false, unique = true, length = 30)
	private String accountNumber;

	/**
	 * 하나은행 여부
	 * true = 하나은행 → donation_rate 계산에 포함
	 */
	@Column(nullable = false)
	private boolean isHana;

	/**
	 * 기본 출금 계좌 여부
	 * 헌금/증여 시 자동 선택되는 계좌
	 */
	@Column(nullable = false)
	private boolean isDefault;

	/**
	 * 적금 계좌 여부
	 * 기도적금/증여 입금 대상으로 선택 가능
	 */
	@Column(nullable = false)
	private boolean isSavings;

	/** 생성일시 */
	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

	/** 기본 계좌로 설정 */
	public void setAsDefault() {
		this.isDefault = true;
	}

	/** 기본 계좌 해제 */
	public void unsetDefault() {
		this.isDefault = false;
	}
}