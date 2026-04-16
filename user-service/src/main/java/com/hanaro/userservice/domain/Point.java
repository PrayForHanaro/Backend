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

	@Column(nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private PointType pointType;

	/**
	 * 지급/차감 포인트
	 * 양수 = 적립, 음수 = 차감
	 */
	@Column(nullable = false)
	private int amount;

	private Long refId;

	// @Column(length = 200)
	private String info;

	//유형별 info 형식
	//1. Offering_Once: "헌금: {유형}"
	//2. OFFERING_RECURRING: "정기헌금: {유형}"
	//3. ACTIVITY_VOLUNTEER: "봉사활동: {제목}"
	//4. ACTIVITY_CHURCH: "교회활동: {제목}"
	//5. SAVINGS_JOIN: "적금가입: {상품명} {대상 이름}"
	//6. SAVINGS_RECURRING: "적금자동이체: {대상이름}"

	public static Point createOfferingOnce(User user, int amount, String offeringType) {
		return Point.builder()
				.user(user)
				.pointType(PointType.OFFERING_SINGLE)
				.amount(amount)
				.info("헌금: " + offeringType)
				.build();
	}

	public static Point createActivityVolunteer(User user, String title) {
		return Point.builder()
				.user(user)
				.pointType(PointType.ACTIVITY_VOLUNTEER)
				.amount(100)
				.info("봉사활동: " + title)
				.build();
	}

	public static Point createActivityChurch(User user, String title) {
		return Point.builder()
				.user(user)
				.pointType(PointType.ACTIVITY_CHURCH)
				.amount(50)
				.info("교회활동: " + title)
				.build();
	}

	public static Point createSavingsJoin(User user, String productName, String targetName) {
		return Point.builder()
				.user(user)
				.pointType(PointType.SAVINGS_JOIN)
				.amount(5000)
				.info(String.format("적금가입: %s %s", productName, targetName))
				.build();
	}

	public static Point createSavingsRecurring(User user, String targetName) {
		return Point.builder()
				.user(user)
				.pointType(PointType.SAVINGS_RECURRING)
				.amount(100)
				.info("적금기도: " + targetName)
				.build();
	}

}
