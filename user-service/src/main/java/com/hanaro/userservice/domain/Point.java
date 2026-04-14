package com.hanaro.userservice.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * 포인트 적립/차감 이력
 * - 모든 서비스의 포인트 지급은 user-service API 호출로 통일
 *
 * point_type 종류
 * ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
 * OFFERING_ONCE       일시헌금 (헌금액 × donation_rate)
 * OFFERING_RECURRING  정기헌금 등록 (고정 500p)
 * ACTIVITY_VOLUNTEER  봉사 활동 참여 (고정 100p)
 * ACTIVITY_CHURCH     교회 행사 참여 (고정 50p)
 * SAVINGS_JOIN        정기 적금 가입 (고정 5000p)
 */
@Entity
@Table(name = "POINT")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Point extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long pointId;

	/** 포인트 적립/차감 대상 성도 (같은 DB → FK 정상 사용) */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * 포인트 지급 유형
	 * OFFERING_ONCE / OFFERING_RECURRING /
	 * ACTIVITY_VOLUNTEER / ACTIVITY_CHURCH /
	 * SAVINGS_JOIN / SAVINGS_AUTO
	 */
	@Column(nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private PointType pointType;

	/**
	 * 지급/차감 포인트
	 * 양수 = 적립, 음수 = 차감
	 */
	@Column(nullable = false)
	private int amount;

	// /**
	//  * 지급 사유 메모
	//  * 예: "홍길동 봉사 활동 참여"
	//  */
	// @Column(length = 200)
	// private String reason;

	/**
	 * 연관 ID (출처 추적용)
	 * 예: offering_id, apply_id, prayer_savings_id
	 * 다른 서비스 ID라 FK 없음
	 */
	@Column
	private Long refId;
}